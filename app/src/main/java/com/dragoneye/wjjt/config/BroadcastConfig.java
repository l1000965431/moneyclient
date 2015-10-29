package com.dragoneye.wjjt.config;

/**
 * 广播消息名称与参数
 */
public interface BroadcastConfig {
    String BROADCAST_ROOT = "com.dragoneye.wjjt.";

    String NEW_EARNING_MESSAGE = BROADCAST_ROOT + "NEW_EARNING_MESSAGE";
    String NEW_PREFERENTIAL_MESSAGE = BROADCAST_ROOT + "NEW_PREFERENTIAL_MESSAGE";
    String NEW_MESSAGE_BOX_ITEM = BROADCAST_ROOT + "NEW_MESSAGE_BOX_ITEM";
}
