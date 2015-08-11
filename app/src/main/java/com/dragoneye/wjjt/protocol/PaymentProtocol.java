package com.dragoneye.wjjt.protocol;

import com.dragoneye.wjjt.config.HttpUrlConfig;

/**
 * Created by happysky on 15-8-10.
 * 支付接口
 */
public interface PaymentProtocol {
    String URL_PAYMENT = HttpUrlConfig.URL_ROOT + "Wallet/RechargeWallet";
}
