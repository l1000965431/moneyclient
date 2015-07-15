package com.money.MoneyServerMQ;


import com.aliyun.openservices.ons.api.Message;

/**
 * 服务器消息类 当发送消息时new MoneyServerMessage 传递给MoneyServer SendMessage()方法
 * <p>User: seele
 * <p>Date: 15-7-9 下午5:50
 * <p>Version: 1.0
 */


public class MoneyServerMessage extends Message {

    /**
     *
     * @param MessageTopic 生产者的MessageTopic
     * @param MessageTag   生产者的MessageTag  用于在后台过滤消息
     * @param MessageBody  生产者发送的消息内容
     */
    public MoneyServerMessage( String MessageTopic,String MessageTag,String MessageBody ){
        super( MessageTopic,MessageTag,MessageBody.getBytes() );
    }

    /**
     *
     * @param MessageTopic 生产者的MessageTopic
     * @param MessageTag   生产者的MessageTag  用于在后台过滤消息
     * @param MessageBody  生产者发送的消息内容
     * @param Key          生产者的Key 用于消息失败时的人工补发和查找  最好全局唯一
     */
    public MoneyServerMessage( String MessageTopic,String MessageTag,String MessageBody,String Key ){
        super( MessageTopic,MessageTag,MessageBody.getBytes() );
        this.SetKey(Key);
    }


    /**
     * 设置消息键值
     * @param Key 消息的键值  最好全局唯一
     */
    public void SetKey( String Key ){
        this.setKey(Key);
    }

    public void SetBody( String Body ){
        this.setBody( Body.getBytes() );
    }

    public void SetTopic( String MessageTopic ){
        this.setTopic( MessageTopic );
    }

    public void SetMsgID( String MessageID ){
        this.setMsgID( MessageID );
    }

    public void SetMsgTag( String MessageTag ){
        this.setTag(MessageTag);
    }

    public String ToString(){
        return this.toString();
    }


    public byte[] GetBody(){
        return  this.getBody();
    }

    public String GetTopic(){
        return this.getTopic();
    }

    public String GetMsgID(){
        return this.getMsgID();
    }

    public String GetMsgTag(){
        return this.getMsgID();
    }

}
