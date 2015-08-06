package com.money.Service.Wallet;

import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.dao.GeneraDAO;
import com.money.model.WalletModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 钱包服务
 * <p>User: liumin
 * <p>Date: 15-7-23
 * <p>Version: 1.0
 */

@Service( "WalletService" )
public class WalletService extends ServiceBase implements ServiceInterface {

    @Autowired
    GeneraDAO generaDAO;


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
    public int RechargeWallet( String UserID,int Lines ){
        WalletModel walletModel = (WalletModel)generaDAO.load( WalletModel.class,UserID );

        if( walletModel == null ){
            return 0;
        }
        int WalletLines = walletModel.getWalletLines();
        WalletLines+=Lines;
        walletModel.setWalletLines( WalletLines );
        generaDAO.update( walletModel );

        return 0;
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

        walletModel.setWalletLines( CurLinse-CostLines );

        generaDAO.updateNoTransaction( walletModel );

        return true;
    }

}
