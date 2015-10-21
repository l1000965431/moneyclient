package com.money.Service.Wallet;

import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.Service.alipay.AlipayService;
import com.money.config.Config;
import com.money.dao.TransactionSessionCallback;
import com.money.dao.alitarnsferDAO.AlitransferDAO;
import com.money.dao.userDAO.UserDAO;
import com.money.model.*;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import until.MoneyServerDate;
import until.MoneyServerOrderID;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 钱包服务
 * <p>User: liumin
 * <p>Date: 15-7-23
 * <p>Version: 1.0
 */

@Service("WalletService")
public class WalletService extends ServiceBase implements ServiceInterface {

    @Autowired
    UserDAO generaDAO;

    @Autowired
    AlitransferDAO alitransferDAO;


    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WalletService.class);

    /**
     * 获取用户钱包剩余金额
     *
     * @param UserID
     * @return
     */
    public int getWalletLines(String UserID) {
        WalletModel walletModel = (WalletModel) generaDAO.load(WalletModel.class, UserID);

        if (walletModel == null) {
            return 0;
        }

        return walletModel.getWalletLines();
    }

    /**
     * 充值钱包
     *
     * @param UserID 用户ID
     * @param Lines  充值金额
     * @return
     */
    public int RechargeWallet(String UserID, int Lines) throws Exception {
        WalletModel walletModel = (WalletModel) generaDAO.loadNoTransaction(WalletModel.class, UserID);

        if (walletModel == null) {
            return 0;
        }
        if (WalletAdd(UserID, Lines) == 0) {
            return 0;
        }

        return 1;
    }

    public int TestRechargeWallet(final String UserID, final int Lines) throws Exception {

        generaDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {

                WalletModel walletModel = (WalletModel) generaDAO.loadNoTransaction(WalletModel.class, UserID);

                if (walletModel == null) {
                    return false;
                }

                return WalletAdd(UserID, Lines) != 0;

            }
        });

        return 1;
    }

    /**
     * 花费
     *
     * @param CostLines
     * @return
     */
    public boolean CostLines(String UserID, int CostLines) {
        WalletModel walletModel = (WalletModel) generaDAO.loadNoTransaction(WalletModel.class, UserID);

        if (walletModel == null) {
            return false;
        }

        if (!walletModel.IsLinesEnough(CostLines)) {
            return false;
        }

        return WalletCost(UserID, CostLines) != 0;

    }

    public boolean TransferLines(final String OrderId, final String OpenId, final int Lines, final String status) throws ParseException {

        if (Objects.equals(generaDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                UserModel userModel = generaDAO.getUSerModelByOpenIdNoTransaction(OpenId);
                if (userModel == null) {
                    return false;
                }

                InsertTransferOrder(userModel, OrderId, OpenId, Lines, status);

                WalletModel walletModel = (WalletModel) generaDAO.loadNoTransaction(WalletModel.class, userModel.getUserId());

                if (walletModel == null) {
                    return false;
                }

                if (!walletModel.IsLinesEnough(Lines)) {
                    return false;
                }

                if (WalletCost(userModel.getUserId(), Lines) == 0) {
                    return false;
                }

                return true;
            }
        }), Config.SERVICE_SUCCESS)) ;

        return false;
    }


    /**
     * 插入充值订单
     *
     * @param OrderID
     * @param Lines
     * @param ChannelID
     */
    public void InsertWalletOrder(String OrderID, int Lines, String ChannelID) throws Exception {

        WalletOrderModel walletOrderModel = new WalletOrderModel();
        walletOrderModel.setOrderID(OrderID);
        walletOrderModel.setWalletLines(Lines);
        walletOrderModel.setWalletChannel(ChannelID);
        walletOrderModel.setOrderDate(MoneyServerDate.getDateCurDate());
        generaDAO.saveNoTransaction(walletOrderModel);

    }

    public void InsertTransferOrder(UserModel userModel, String OrderId, String OpenId, int Lines, String status) throws ParseException {
        TransferModel transferModel = new TransferModel();
        transferModel.setOrderId(OrderId);
        transferModel.setOpenId(OpenId);
        transferModel.setTransferLines(Lines);
        transferModel.setTransferDate(MoneyServerDate.getDateCurDate());
        transferModel.setUserId(userModel.getUserId());
        transferModel.setStatus(status);
        generaDAO.saveNoTransaction(transferModel);
    }


    public boolean IsWalletEnough(String UserID, int Lines) {
        WalletModel walletModel = (WalletModel) generaDAO.loadNoTransaction(WalletModel.class, UserID);

        if (walletModel == null) {
            return false;
        }

        return walletModel.IsLinesEnough(Lines);
    }


    public boolean IsWalletEnoughTransaction(String UserID, int Lines) {
        WalletModel walletModel = (WalletModel) generaDAO.load(WalletModel.class, UserID);

        if (walletModel == null) {
            return false;
        }

        return walletModel.IsLinesEnough(Lines);
    }

    public boolean IsvirtualSecuritiesEnough(String UserID, int Lines) {
        WalletModel walletModel = (WalletModel) generaDAO.loadNoTransaction(WalletModel.class, UserID);

        if (walletModel == null) {
            return false;
        }

        return walletModel.IsvirtualSecuritiesEnough(Lines);
    }

    /**
     * 金额增加
     *
     * @param UserId
     * @param Lines
     * @return
     */
    private int WalletAdd(String UserId, int Lines) {
        String sql = "update wallet set WalletLines = WalletLines+? where UserID = ? ";
        Session session = generaDAO.getNewSession();
        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter(0, Lines);
        query.setParameter(1, UserId);
        return query.executeUpdate();
    }

    /**
     * 金额花费
     *
     * @param UserId
     * @param Lines
     * @return
     */
    private int WalletCost(String UserId, int Lines) {
        String sql = "update wallet set WalletLines = WalletLines-? where UserID = ? and WalletLines-? >= 0";
        Session session = generaDAO.getNewSession();
        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter(0, Lines);
        query.setParameter(1, UserId);
        query.setParameter(2, Lines);
        return query.executeUpdate();
    }

    /**
     * 微劵花费
     * @param UserId
     * @param Lines
     * @return
     */
    public int virtualSecuritiesCost(String UserId, int Lines) {
        String sql = "update wallet set virtualSecurities = virtualSecurities-? where UserID = ? and virtualSecurities-? >= 0";
        Session session = generaDAO.getNewSession();
        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter(0, Lines);
        query.setParameter(1, UserId);
        query.setParameter(2, Lines);
        return query.executeUpdate();
    }

    /**
     * 微劵充值
     * @param UserId
     * @param Lines
     * @return
     */
    public int virtualSecuritiesAdd(String UserId, int Lines) {
        String sql = "update wallet set virtualSecurities = virtualSecurities+? where UserID = ? and virtualSecurities+?<=?";
        Session session = generaDAO.getNewSession();
        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter(0, Lines);
        query.setParameter(1, UserId);
        query.setParameter(2, Config.MaxVirtualSecurities);
        return query.executeUpdate();
    }

    /**
     * 获得支付宝提现申请清单
     *
     * @return
     */
    public List GetAliTranserOrder() {

        final List[] list = new List[1];
        alitransferDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                list[0] = alitransferDAO.GetAliTransferOdrer();
                return true;
            }
        });


        return list[0];
    }

    /**
     * 获得支付宝提现申请详情信息
     *
     * @return
     */
    public List<AlitransferDAO> GetAliTranserInfo(final int page) {
        final List[] list = new List[1];
        alitransferDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                list[0] = alitransferDAO.GetAliTransferInfo(page);
                return true;
            }
        });


        return list[0];
    }

    public String BindingalipayId(final String UserId, final String AlipayId, final String RealName) {
        return alitransferDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                UserModel userModel = generaDAO.getUSerModelNoTransaction(UserId);
                if (userModel == null) {
                    return false;
                }

                if (!userModel.getAlipayId().equals("0")) {
                    return false;
                }

                userModel.setAlipayRealName(RealName);
                userModel.setAlipayId(AlipayId);
                generaDAO.updateNoTransaction(userModel);
                return true;
            }
        });
    }

    /**
     * 清空支付宝绑定
     *
     * @param UserId
     * @return
     */
    public String ClearalipayId(final String UserId) {

        return alitransferDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                UserModel userModel = generaDAO.getUSerModelNoTransaction(UserId);

                if (userModel == null) {
                    return false;
                }

                userModel.setAlipayRealName("");
                userModel.setAlipayId("0");
                generaDAO.updateNoTransaction(userModel);
                return true;
            }
        });
    }

    /**
     * 支付宝提现
     *
     * @param UserId
     * @param Lines
     * @return
     */
    //-1:失败 0:余额不足 1:提交成功
    public int alipayTransfer(final String UserId, final int Lines) {

        //计算支付宝的手续费
        double poundage = Lines * 0.005;
        double poundageResult = Math.ceil(poundage);

        if (poundageResult < 1.0) {
            poundageResult = 1.0;
        } else if (poundageResult > 25.0) {
            poundageResult = 25.0;
        }

        final int costLines = Lines + (int) poundageResult;

        final int[] state = {1};
        alitransferDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {

                UserModel userModel = generaDAO.getUSerModelNoTransaction(UserId);

                if (userModel == null) {
                    state[0] = -1;
                    return false;
                }

                if (!CostLines(UserId, costLines)) {
                    state[0] = 0;
                    return false;
                }

                if (alitransferDAO.Submitalitansfer(userModel.getUserId(), Lines, userModel.getAlipayRealName(), userModel.getAlipayId()) == 0) {
                    state[0] = -1;
                    Object[] objects = new Object[3];
                    objects[0] = userModel;
                    objects[1] = Lines;
                    objects[2] = MoneyServerDate.getDateCurDate();
                    LOGGER.error("提交提现申请失败", objects);
                    return false;
                }


                InsertTransferOrder(userModel, MoneyServerOrderID.GetOrderID(UserId), userModel.getAlipayId(), costLines, "alipay");


                return true;
            }
        });


        return state[0];
    }


    /**
     * 支付宝批量付款通知
     */
    public String alipayTransferNotify(Map<String, String> NotifyInfo) {

        final String Batchno = NotifyInfo.get("batch_no");
        final String Payuserid = NotifyInfo.get("pay_user_id");
        final String Payusername = NotifyInfo.get("pay_user_name");
        final String Notifytime = NotifyInfo.get("notify_time");
        final String Faildetails = NotifyInfo.get("fail_details");
        final String Successdetails = NotifyInfo.get("success_details");

        return alitransferDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                AliTransferNotifyModel aliTransferNotifyModel = new AliTransferNotifyModel();
                aliTransferNotifyModel.setBatchno(Batchno);
                aliTransferNotifyModel.setPayuserid(Payuserid);
                aliTransferNotifyModel.setPayusername(Payusername);
                aliTransferNotifyModel.setPayStates(1);
                aliTransferNotifyModel.setPayDate(MoneyServerDate.StrToDate(Notifytime));
                session.save(aliTransferNotifyModel);

                List<List<String>> FaildetailsList;
                List<List<String>> SuccessdetailsList;
                if (Faildetails != null) {
                    FaildetailsList = AlipayService.ParsingNotifyParam(Faildetails);

                    if (FaildetailsList == null) {
                        LOGGER.error("FaildetailsList == null");
                        return false;
                    }
                    for (List<String> aFaildetailsList : FaildetailsList) {
                        AlitransferModel alitransferModel = (AlitransferModel) alitransferDAO.loadNoTransaction(AlitransferModel.class, Integer.valueOf(aFaildetailsList.get(0)));
                        if (alitransferModel == null) {
                            continue;
                        }
                        double linestemp = Double.valueOf(aFaildetailsList.get(3));
                        if (alitransferModel.getAliEmail().equals(aFaildetailsList.get(1)) &&
                                alitransferModel.getRealName().equals(aFaildetailsList.get(2)) &&
                                alitransferModel.getLines() == (int) linestemp) {
                            alitransferModel.setIsFaliled(true);
                            session.update(alitransferModel);
                        }

                    }
                }

                if (Successdetails != null) {
                    SuccessdetailsList = AlipayService.ParsingNotifyParam(Successdetails);

                    if (SuccessdetailsList == null) {
                        LOGGER.error("SuccessdetailsList == null");
                        return false;
                    }

                    for (List<String> aSuccessdetailsList : SuccessdetailsList) {
                        AlitransferModel alitransferModel = (AlitransferModel) alitransferDAO.loadNoTransaction(AlitransferModel.class, Integer.valueOf(aSuccessdetailsList.get(0)));
                        if (alitransferModel == null) {
                            continue;
                        }

                        double linestemp = Double.valueOf(aSuccessdetailsList.get(3));
                        if (alitransferModel.getAliEmail().equals(aSuccessdetailsList.get(1)) &&
                                alitransferModel.getRealName().equals(aSuccessdetailsList.get(2)) &&
                                alitransferModel.getLines() == (int) linestemp) {
                            session.delete(alitransferModel);
                        }

                    }
                }


                return true;
            }
        });
    }


}
