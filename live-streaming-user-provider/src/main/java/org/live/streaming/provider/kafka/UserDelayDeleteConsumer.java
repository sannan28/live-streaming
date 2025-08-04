package org.live.streaming.provider.kafka;

import cn.hutool.json.JSONUtil;
import live.streaming.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.*;

// 用户缓存延迟双删
@Slf4j
@Component
public class UserDelayDeleteConsumer {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;

    private static final DelayQueue<DelayedTask> DELAY_QUEUE = new DelayQueue<>();

    private static final ExecutorService DELAY_QUEUE_THREAD_POOL = new ThreadPoolExecutor(
            3, 10,
            10L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100)
    );


    @PostConstruct()
    private void init() {
        DELAY_QUEUE_THREAD_POOL.submit(() -> {
            while (true) {
                try {
                    DelayedTask task = DELAY_QUEUE.take();
                    task.execute();
                    log.info("DelayQueue延迟双删了一个用户缓存");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Thread-user-delay-delete-cache");
    }


    @KafkaListener(topics = "user-delete-cache")
    public void consumerTopic(String kafkaObjectJSON) {
        KafkaObject kafkaObject = JSONUtil.toBean(kafkaObjectJSON, KafkaObject.class);
        String code = kafkaObject.getCode();
        long userId = Long.parseLong(kafkaObject.getUserId());
        log.info("kafka  topic user-delete-cache 接收到的json：{}", kafkaObjectJSON);

        if (code.equals(KafkaCodeConstants.USER_INFO_CODE)) {
            DELAY_QUEUE.offer(new DelayedTask(1000,
                    () -> redisTemplate.delete(userProviderCacheKeyBuilder.buildUserInfoKey(userId))));
            log.info("Kafka 接收延迟双删消息成功，类别：UserInfo，用户ID：{}", userId);
        }
    }

}
