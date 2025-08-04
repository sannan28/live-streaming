package live.streaming.im.core.server.service.impl;


import com.alibaba.fastjson.JSON;
import live.streaming.framework.redis.starter.key.ImCoreServerProviderCacheKeyBuilder;
import live.streaming.im.core.server.service.IMsgAckCheckService;
import live.streaming.im.interfaces.dto.ImMsgBody;
import live.streaming.interfaces.topic.ImCoreServerProviderTopicNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MsgAckCheckServiceImpl implements IMsgAckCheckService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private ImCoreServerProviderCacheKeyBuilder cacheKeyBuilder;

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;


    public void doMsgAck(ImMsgBody imMsgBody) {
        String key = cacheKeyBuilder.buildImAckMapKey(imMsgBody.getUserId(), imMsgBody.getAppId());
        redisTemplate.opsForHash().delete(key, imMsgBody.getMsgId());
        redisTemplate.expire(key, 30, TimeUnit.MINUTES);
    }

    public void recordMsgAck(ImMsgBody imMsgBody, int times) {
        String key = cacheKeyBuilder.buildImAckMapKey(imMsgBody.getUserId(), imMsgBody.getAppId());
        redisTemplate.opsForHash().put(key, imMsgBody.getMsgId(), times);
        redisTemplate.expire(key, 30, TimeUnit.MINUTES);
    }

    public void sendDelayMsg(ImMsgBody imMsgBody) {
        String json = JSON.toJSONString(imMsgBody);
        ListenableFuture<SendResult<String, String>> sendResultListenableFuture = kafkaTemplate.send(ImCoreServerProviderTopicNames.IM_ONLINE_TOPIC, JSON.toJSONString(json));
        sendResultListenableFuture.addCallback(result -> {
            log.info(" [MsgAckCheckServiceImpl] msg is {},sendResult is {}", result);
        }, ex -> {
            log.error(" [MsgAckCheckServiceImpl] error is  ", ex);
            throw new RuntimeException(ex);
        });
    }

    public int getMsgAckTimes(String msgId, long userId, int appId) {
        Object value = redisTemplate.opsForHash().get(cacheKeyBuilder.buildImAckMapKey(userId, appId), msgId);

        if (value == null) {
            return -1;
        }
        return (Integer) value;
    }
}
