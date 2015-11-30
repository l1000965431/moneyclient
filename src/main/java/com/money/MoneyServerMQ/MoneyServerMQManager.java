package com.money.MoneyServerMQ;

import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.money.config.Config;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import until.GsonUntil;
import until.UmengPush.UMengMessage;
import until.UmengPush.UmengSendParameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 服务器消息队列管理器
 * 负责消息队列的初始化 发送工作
 * 采用阿里云的消息队列服务器 全部消息是订阅消息 异步回掉消费者
 * <p>User: seele
 * <p>Date: 15-7-9 下午5:50
 * <p>Version: 1.0
 */


public class MoneyServerMQManager {

    static Producer producer;
    static Consumer consumer;


    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MoneyServerMQManager.class);

    public MoneyServerMQManager( List<MoneyServerListener> ListMoneyServerListener ){
        InitMoneyServerMQManager();
        RegisteredListener(ListMoneyServerListener);
    }


    private void InitMoneyServerMQManager(){
        Properties propertiesproducer = new Properties();
        propertiesproducer.put(PropertyKeyConst.ProducerId, Config.MESSAFE_PRODUCERID);
        propertiesproducer.put(PropertyKeyConst.AccessKey, Config.MESSAFE_ACCESSKEY);
        propertiesproducer.put(PropertyKeyConst.SecretKey, Config.MESSAFE_SECRETKEY);
        producer = ONSFactory.createProducer(propertiesproducer);
        producer.start();

        Properties propertiesconsumer = new Properties();
        propertiesconsumer.put(PropertyKeyConst.ConsumerId, Config.MESSAFE_CONSUMERID);
        propertiesconsumer.put(PropertyKeyConst.AccessKey, Config.MESSAFE_ACCESSKEY);
        propertiesconsumer.put(PropertyKeyConst.SecretKey, Config.MESSAFE_SECRETKEY);
        consumer = ONSFactory.createConsumer(propertiesconsumer);
        consumer.start();

        /*LOGGER.error(consumer.toString());
        LOGGER.error( propertiesconsumer.toString() );*/
    }

    /**
     * 发送消息 目前所有消息全部是订阅消息 异步回调消费者
     * @param Message 消息信息
     */

    public static int SendMessage( MoneyServerMessage Message ){
        try {
            producer.send( Message );
            return  Config.SENDCODE_SUCESS;
        }catch ( Exception e ){
            LOGGER.error( e.toString() );
            return Config.SENDCODE_FAILED;
        }
    }


    void RegisteredListener( List<MoneyServerListener> ListMoneyServerListener ){

        for( MoneyServerListener listenertemp:ListMoneyServerListener ){
            consumer.subscribe( listenertemp.getMessageTopic(),"*",listenertemp );
        }
    }

    /**
     * 注销一个MessageTopic的消费者
     * @param MessageTopic 消息标识
     */
    public void UnRegistered( String MessageTopic ){
        consumer.unsubscribe( MessageTopic );
    }


}
