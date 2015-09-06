package com.money.Service.Wallet;

import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.config.Config;
import com.money.dao.GeneraDAO;
import com.money.dao.TransactionSessionCallback;
import com.money.dao.userDAO.UserDAO;
import com.money.model.TransferModel;
import com.money.model.UserModel;
import com.money.model.WalletModel;
import com.money.model.WalletOrderModel;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import until.MoneyServerDate;

import java.text.ParseException;

/**
 * 钱包服务
 * <p>User: liumin
 * <p>Date: 15-7-23
 * <p>Version: 1.0
 */

@Service( "WalletService" )
public class WalletService extends ServiceBase implements ServiceInterface {

    @Autowired
    UserDAO generaDAO;


    /**
     * 获取用户钱包剩余金额
     * @param UserID
     * @return
     */
    public int getWalletLines( String UserID ){
        WalletModel walletModel = (WalletModel)generaDAO.load( WalletModel.class,UserID );

        if( walletModel == null ){
            return 0;
        }

       return walletModel.getWalletLines();
    }

    /**
     * 充值钱包
     * @param UserID 用户ID
     * @param Lines  充值金额
     * @return
     */
    public int RechargeWallet(String UserID, int Lines) throws Exception{
        WalletModel walletModel = (WalletModel)generaDAO.loadNoTransaction( WalletModel.class,UserID );

        if( walletModel == null ){
            return 0;
        }
        int WalletLines = walletModel.getWalletLines();
        WalletLines+=Lines;
        walletModel.setWalletLines( WalletLines );
        generaDAO.updateNoTransaction( walletModel );
        return 1;
    }

    /**
     * 花费
     * @param CostLines
     * @return
     */
    public boolean CostLines( String UserID,int CostLines ){
        WalletModel walletModel = (WalletModel)generaDAO.loadNoTransaction(WalletModel.class, UserID);

        if( walletModel == null ){
            return false;
        }

        if( !walletModel.IsLinesEnough( CostLines ) ){
            return false;
        }

        int CurLinse = walletModel.getWalletLines();
        walletModel.setWalletLines(CurLinse-CostLines);
        generaDAO.updateNoTransaction( walletModel );
        return true;
    }

    public boolean TransferLines( final String OrderId, final String OpenId, final int Lines, final String status ) throws ParseException {

        if(generaDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            public boolean callback(Session session) throws Exception {
                UserModel userModel = generaDAO.getUSerModelByOpenIdNoTransaction(OpenId);
                if( userModel == null ){
                    return false;
                }

                InsertTransferOrder( userModel,OrderId,OpenId,Lines,status );

                WalletModel walletModel = (WalletModel)generaDAO.loadNoTransaction(WalletModel.class, userModel.getUserId());

                if( walletModel == null ){
                    return false;
                }

                if( !walletModel.IsLinesEnough( Lines ) ){
                    return false;
                }

                int CurLines = walletModel.getWalletLines();
                walletModel.setWalletLines(CurLines-Lines);
                generaDAO.updateNoTransaction( walletModel);

                return true;
            }
        }) == Config.SERVICE_SUCCESS );

       return false;
    }


    /**
     * 插入充值订单
     * @param OrderID
     * @param Lines
     * @param ChannelID
     */
    public void InsertWalletOrder( String OrderID,int Lines,String ChannelID )throws Exception{

        WalletOrderModel walletOrderModel = new WalletOrderModel();
        walletOrderModel.setOrderID( OrderID );
        walletOrderModel.setWalletLines( Lines );
        walletOrderModel.setWalletChannel( ChannelID );
        walletOrderModel.setOrderDate(MoneyServerDate.getDateCurDate());
        generaDAO.saveNoTransaction( walletOrderModel );

    }

    public void InsertTransferOrder( UserModel userModel,String OrderId, String OpenId,int Lines,String status ) throws ParseException {
        TransferModel transferModel = new TransferModel();
        transferModel.setOrderId( OrderId );
        transferModel.setOpenId( OpenId );
        transferModel.setTransferLines( Lines );
        transferModel.setTransferDate( MoneyServerDate.getDateCurDate() );
        transferModel.setUserId( userModel.getUserId() );
        transferModel.setStatus( status );
        generaDAO.saveNoTransaction(transferModel);
    }


    public boolean IsWalletEnough( String UserID,int Lines ){
        WalletModel walletModel = (WalletModel)generaDAO.loadNoTransaction(WalletModel.class, UserID);

        if( walletModel == null ){
            return false;
        }

        if( !walletModel.IsLinesEnough( Lines ) ){
            return false;
        }else{
            return true;
        }
    }


    public boolean IsWalletEnoughTransaction( String UserID,int Lines ){
        WalletModel walletModel = (WalletModel)generaDAO.load(WalletModel.class, UserID);

        if( walletModel == null ){
            return false;
        }

        if( !walletModel.IsLinesEnough( Lines ) ){
            return false;
        }else{
            return true;
        }
    }

}
