package com.dragoneye.wjjt.config;

/**
 * 广播消息名称与参数
 */
public interface BroadcastConfig {
    String BROADCAST_ROOT = "com.dragoneye.wjjt.";

    String NEW_MAIN_ACTIVITY_NEW_ITEM_MESSAGE = BROADCAST_ROOT + "NEW_MAIN_ACTIVITY_NEW_ITEM_MESSAGE";

    String NEW_PREFERENTIAL_RESULT_MESSAGE = BROADCAST_ROOT + "NEW_PREFERENTIAL_RESULT_MESSAGE";
    String NEW_MESSAGE_BOX_ITEM = BROADCAST_ROOT + "NEW_MESSAGE_BOX_ITEM";
}
