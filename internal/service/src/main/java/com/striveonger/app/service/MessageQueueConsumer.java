package com.striveonger.app.service;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mr.Lee
 * @since 2026-03-24 22:36
 */
// @Service
// @RocketMQMessageListener(consumerGroup = "${rocketmq.consumer.pull-consumer.group}", topic = "test")
public class MessageQueueConsumer implements RocketMQListener<MessageExt> {
    private final Logger log = LoggerFactory.getLogger(MessageQueueConsumer.class);

    @Override
    public void onMessage(MessageExt message) {
        log.info("receive message queue success, message: {}", message);
        // 处理消息
        log.info("消息ID: {}", message.getMsgId());
        log.info("消息体: {}", new String(message.getBody()));
        log.info("Tag: {}", message.getTags());
        log.info("自定义属性: {}", message.getUserProperty("key"));

        // 手动确认消息(删除消息)
        // message.ack();
    }
}
