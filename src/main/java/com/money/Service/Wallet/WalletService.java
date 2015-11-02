package com.money.Service.Wallet;

import com.google.gson.reflect.TypeToken;
import com.money.MoneyServerMQ.MoneyServerMQManager;
import com.money.MoneyServerMQ.MoneyServerMessage;
import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.Service.alipay.AlipayService;
import com.money.config.Config;
import com.money.config.MoneyServerMQ_Topic;
import com.money.dao.TransactionSessionCallback;
import com.money.dao.alitarnsferDAO.AlitransferDAO;
import com.money.dao.userDAO.UserDAO;
import com.money.model.*;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import until.GsonUntil;
import until.MoneyServerDate;
import until.MoneyServerOrderID;
import until.UmengPush.UmengSendParameter;

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

    /**
     * ping++的支付服务回掉
     *
     * @param body
     * @return
     */
    public int RechargeWalletService(String body) throws Exception {
        String UserID;

        final Map<String, Object> map = GsonUntil.jsonToJavaClass(body, new TypeToken<Map<String, Object>>() {
        }.getType());

        if (map == null) {
            return Config.SENDCODE_FAILED;
        }

        final Map<String, Object> mapdata = (Map) map.get("data");
        Map<String, Object> mapobject = (Map) mapdata.get("object");
        Map<String, Object> mapMetadata = (Map) mapobject.get("metadata");

        if (mapMetadata == null) {
            return Config.SENDCODE_FAILED;
        }

        UserID = mapMetadata.get("UserID").toString();
        Double nLinse = (Double) mapobject.get("amount");
        final int Lines = (nLinse.intValue() / 100);
        final String OrderID = mapobject.get("order_no").toString();
        final String ChannelID = mapobject.get("channel").toString();

        final String finalUserID = UserID;
        if (generaDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                if (RechargeWallet(finalUserID, Lines) == 0) {
                    LOGGER.error("充值失败RechargeWallet", mapdata);
                    return false;
                }
                InsertWalletOrder(OrderID, Lines, ChannelID);
                return true;
            }
        }) != Config.SERVICE_SUCCESS) {
            if (UserID != null || UserID.length() != 0) {
                UmengSendParameter umengSendParameter = new UmengSendParameter(UserID, "微距竞投", "充值失败", "你的充值遇到了问题请重新操作", "充值失败");
                String Json = GsonUntil.JavaClassToJson(umengSendParameter);
                MoneyServerMQManager.SendMessage(new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TOPIC,
                        MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TAG, Json, "充值失败"));
                return Config.SENDCODE_FAILED;
            }
        }

        UmengSendParameter umengSendParameter = new UmengSendParameter(UserID, "微距竞投", "充值成功", "充值成功,成功充入" + Integer.toString(Lines) + "元", "充值成功");
        String Json = GsonUntil.JavaClassToJson(umengSendParameter);
        MoneyServerMQManager.SendMessage(new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TOPIC,
                MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TAG, Json, "充值成功"));


        return Config.SENDCODE_SUCESS;


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

    /**
     * ping++的提现服务
     *
     * @param body
     * @return
     */
    public int TranferLinesService(String body) throws Exception {
        Map<String, Object> map = GsonUntil.jsonToJavaClass(body, new TypeToken<Map<String, Object>>() {
        }.getType());

        if (map == null) {
            return Config.SENDCODE_FAILED;
        }

        Map<String, Object> mapdata = (Map) map.get("data");
        Map<String, Object> mapobject = (Map) mapdata.get("object");

        String status = mapobject.get("status").toString();
        double ammont = Double.valueOf(mapobject.get("amount").toString()) / 100.0;
        String openId = mapobject.get("recipient").toString();
        String orderId = mapobject.get("transaction_no").toString();
        if (status.equals("paid")) {
            TransferLines(orderId, openId, (int) ammont, status);
        }

        return Config.SENDCODE_SUCESS;
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
     *
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
     *
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
        query.setParameter(2, Lines);
        query.setParameter(3, Config.MaxVirtualSecurities);
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
    //0:失败 1:提交成功 2:余额不足
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
                    state[0] = 0;
                    return false;
                }

                if (!CostLines(UserId, costLines)) {
                    state[0] = 2;
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
