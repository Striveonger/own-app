package com.striveonger.app.web.controller;

import com.striveonger.common.core.SpringContext;
import com.striveonger.common.core.result.Result;
import com.striveonger.common.core.thread.ThreadKit;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.rocketmq.remoting.protocol.heartbeat.SubscriptionData.SUB_ALL;

/**
 * 消息队列控制器
 *
 * @author Mr.Lee
 * @since 2025-04-26 10:18
 */
@Controller
public class MessageQueueController {
    private final Logger log = LoggerFactory.getLogger(MessageQueueController.class);

    @Resource
    private RocketMQTemplate template;

    private volatile boolean isRunning = true;

    private final String topic = "test";


    @GetMapping("/api/v1/message/queue/test")
    @ResponseBody
    public Result test() {
        log.info("test message queue");

        ThreadKit.run(() -> {
            // 生产者线程
            // DefaultMQProducer producer = template.getProducer();
            int cnt = 0;
            do {
                // template.sendOneWay(topic, "hello message queue");
                String payload = "hi " + cnt;
                MessageExt messageExt = new MessageExt();
                messageExt.setBody(payload.getBytes());
                messageExt.setTags("tag1");

                Message<String> message = MessageBuilder.withPayload(payload).build();
                template.send(topic + ":" + (cnt++ % 2 == 0 ? "tag1" : "tag2"), message);

                ThreadKit.sleep(1500);
                log.info("send message queue success, topic: {}, message: {}", topic, payload);
            } while (isRunning);
        }, "producer-thread", true);

        ThreadKit.run(() -> {
            // 消费者线程
            String group = SpringContext.getProperties("rocketmq.consumer.pull-consumer.group", "");
            String addr = SpringContext.getProperties("rocketmq.name-server", "");
            // 1. 创建消费者实例
            DefaultLitePullConsumer consumer = new DefaultLitePullConsumer(group);
            // 2. 设置名称服务器地址
            consumer.setNamesrvAddr(addr);
            // 3. 设置消费起始位置
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
            try {
                // 4. 订阅主题
                consumer.subscribe(topic, "tag2");

                // 5. 启动消费者
                consumer.start();

                do {
                    List<MessageExt> list = consumer.poll(1000L);
                    for (MessageExt message : list) {
                        log.info("消息ID: {}", message.getMsgId());
                        log.info("消息体: {}", new String(message.getBody()));
                        log.info("Tag: {}", message.getTags());
                        log.info("自定义属性: {}", message.getUserProperty("key"));
                    }
                } while (isRunning);
                // 6. 关闭消费者
                consumer.shutdown();
            } catch (Exception e) {
                log.error("subscribe message queue failed, topic: {}", topic, e);
            }

        }, "consumer-thread", true);

        ThreadKit.run(() -> {
            ThreadKit.sleep(60000);
            isRunning = false;
        });

        // DefaultLitePullConsumer consumer = template.getConsumer();
        // try {
        //     Collection<MessageQueue> messageQueues = consumer.fetchMessageQueues(topic);
        //     Map<MessageQueue, Long> offsetTable = new HashMap<>();
        //     for (MessageQueue queue : messageQueues) {
        //         // 获取队列的最小偏移量
        //         long minOffset = consumer.
        //         offsetTable.put(queue, minOffset);
        //     }
        //
        // } catch (Exception e) {
        //     log.error("fetch message queue failed, topic: {}", topic, e);
        // }

        return Result.success().data("test message queue success");
    }

}
