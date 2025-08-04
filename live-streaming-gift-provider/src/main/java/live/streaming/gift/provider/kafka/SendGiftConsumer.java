package live.streaming.gift.provider.kafka;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import live.streaming.bank.interfaces.dto.AccountTradeReqDTO;
import live.streaming.bank.interfaces.dto.AccountTradeRespDTO;
import live.streaming.bank.interfaces.rpc.LiveCurrencyAccountRpc;
import live.streaming.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import live.streaming.gift.interfaces.constants.SendGiftTypeEnum;
import live.streaming.im.interfaces.dto.ImMsgBody;
import live.streaming.im.interfaces.enums.AppIdEnum;
import live.streaming.im.router.interfaces.contants.ImMsgBizCodeEnum;
import live.streaming.im.router.interfaces.rpc.ImRouterRpc;
import live.streaming.interfaces.dto.SendGiftMq;
import live.streaming.interfaces.topic.GiftProviderTopicNames;
import live.streaming.living.inerfaces.dto.LivingRoomReqDTO;
import live.streaming.living.inerfaces.dto.LivingRoomRespDTO;
import live.streaming.living.inerfaces.rpc.ILivingRoomRpc;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class SendGiftConsumer {

    @DubboReference
    private LiveCurrencyAccountRpc liveCurrencyAccountRpc;

    @DubboReference
    private ImRouterRpc routerRpc;

    @DubboReference
    private ILivingRoomRpc livingRoomRpc;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;

    private static final Long PK_INIT_NUM = 50L;

    private static final Long PK_MAX_NUM = 100L;

    private static final Long PK_MIN_NUM = 0L;

    private static final DefaultRedisScript<Long> redisScript;

    static {
        redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        redisScript.setLocation(new ClassPathResource("getPkNumAndSeqId.lua"));
    }


    @KafkaListener(topics = GiftProviderTopicNames.SEND_GIFT, groupId = "send-gift-consumer", containerFactory = "batchFactory")
    public void consumeSendGift(List<ConsumerRecord<?, ?>> records) {
        System.out.println("11111111111111111111");
        // 批量拉取消息进行处理
        for (ConsumerRecord<?, ?> record : records) {
            System.out.println("222222222222222222");
            String sendGiftMqStr = (String) record.value();
            SendGiftMq sendGiftMq = JSON.parseObject(sendGiftMqStr, SendGiftMq.class);
            String mqConsumerKey = cacheKeyBuilder.buildGiftConsumeKey(sendGiftMq.getUuid());
            Boolean lockStatus = redisTemplate.opsForValue().setIfAbsent(mqConsumerKey, -1, 5L, TimeUnit.MINUTES);
            if (Boolean.FALSE.equals(lockStatus)) {
                // 代表曾经消费过，防止重复消费
                continue;
            }
            Long userId = sendGiftMq.getUserId();
            AccountTradeReqDTO accountTradeReqDTO = new AccountTradeReqDTO();
            accountTradeReqDTO.setUserId(userId);
            accountTradeReqDTO.setNum(sendGiftMq.getPrice());
            AccountTradeRespDTO tradeRespDTO = liveCurrencyAccountRpc.consumeForSendGift(accountTradeReqDTO);

            // 判断余额扣减情况：
            JSONObject jsonObject = new JSONObject();
            Integer sendGiftType = sendGiftMq.getType();
            if (tradeRespDTO.isSuccess()) {
                System.out.println("333333333333333333333333333");
                // 如果余额扣减成功
                // 0 查询在直播间的userId
                LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
                Integer roomId = sendGiftMq.getRoomId();
                livingRoomReqDTO.setRoomId(roomId);
                livingRoomReqDTO.setAppId(AppIdEnum.LIVE_BIZ.getCode());
                List<Long> userIdList = livingRoomRpc.queryUserIdByRoomId(livingRoomReqDTO);
                // TODO 触发礼物特效推送功能
                if (sendGiftType.equals(SendGiftTypeEnum.DEFAULT_SEND_GIFT.getCode())) {
                    // 默认送礼，发送给全直播用户礼物特效
                    // 利用封装方法发送单播消息，bizCode为success类型
                    jsonObject.put("url", sendGiftMq.getUrl());
                    this.batchSendImMsg(userIdList, ImMsgBizCodeEnum.LIVING_ROOM_SEND_GIFT_SUCCESS.getCode(), jsonObject);
                    log.info("[sendGiftConsumer] send success, msg is {}", record);
                } else if (sendGiftType.equals(SendGiftTypeEnum.PK_SEND_GIFT.getCode())) {
                    System.out.println("4444444444444444444444");
                    pkImMsgSend(sendGiftMq, jsonObject, roomId, userIdList);
                    log.info("[sendGiftConsumer] send pk msg success, msg is {}", record);
                }
            } else {
                // 没成功，返回失败信息
                // TODO 利用IM将发送失败的消息告知用户
                jsonObject.put("msg", tradeRespDTO.getMsg());
                // 利用封装方法发送单播消息，bizCode为fail类型
                this.sendImMsgSingleton(userId, ImMsgBizCodeEnum.LIVING_ROOM_SEND_GIFT_FAIL.getCode(), jsonObject);
                log.info("[sendGiftConsumer] send fail, msg is {}", tradeRespDTO.getMsg());
            }
        }
    }

    /**
     * PK直播间送礼扣费成功后的流程：
     * 1 设置礼物特效url
     * 2 设置PK进度条的值
     * 3 批量推送给直播间全体用户
     * @param sendGiftMq 发送消息请求req
     * @param jsonObject 返回的ImMsgBody的data部分
     * @param roomId     直播间id
     * @param userIdList 直播间在线用户列表
     */
    private void pkImMsgSend(SendGiftMq sendGiftMq, JSONObject jsonObject, Integer roomId, List<Long> userIdList) {
        // PK送礼，要求全体可见
        // 1 TODO PK进度条全直播间可见

        String isOverCacheKey = cacheKeyBuilder.buildLivingPkIsOver(roomId);
        // 1.1 判断直播PK是否已经结束
        Boolean isOver = redisTemplate.hasKey(isOverCacheKey);
        if (Boolean.TRUE.equals(isOver)) {
            return;
        }
        // 1.2 获取 pkUserId 和 pkObjId
        Long pkObjId = livingRoomRpc.queryOnlinePkUserId(roomId);
        LivingRoomRespDTO livingRoomRespDTO = livingRoomRpc.queryByRoomId(roomId);
        if (pkObjId == null || livingRoomRespDTO == null || livingRoomRespDTO.getAnchorId() == null) {
            log.error("[sendGiftConsumer] 两个用户已经有不在线的，roomId is {}", roomId);
            return;
        }
        Long pkUserId = livingRoomRespDTO.getAnchorId();
        // 1.3 获取当前进度条值 和 送礼序列号
        String pkNumKey = cacheKeyBuilder.buildLivingPkKey(roomId);
        Long pkNum = 0L;
        // 获取该条消息的序列号，避免消息乱序
        Long sendGiftSeqNum = System.currentTimeMillis();
        if (sendGiftMq.getReceiverId().equals(pkUserId)) {
            Integer moveStep = sendGiftMq.getPrice() / 10;
            // 收礼人是房主userId，则进度条增加
            pkNum = redisTemplate.execute(redisScript, Collections.singletonList(pkNumKey), PK_INIT_NUM, PK_MAX_NUM, PK_MIN_NUM, moveStep);
            if (PK_MAX_NUM <= pkNum) {
                jsonObject.put("winnerId", pkUserId);
            }
        } else if (sendGiftMq.getReceiverId().equals(pkObjId)) {
            Integer moveStep = sendGiftMq.getPrice() / 10 * -1;
            // 收礼人是来挑战的，则进图条减少
            pkNum = redisTemplate.execute(redisScript, Collections.singletonList(pkNumKey), PK_INIT_NUM, PK_MAX_NUM, PK_MIN_NUM, moveStep);
            if (PK_MIN_NUM >= pkNum) {
                jsonObject.put("winnerId", pkObjId);
            }
        }
        jsonObject.put("receiverId", sendGiftMq.getReceiverId());
        jsonObject.put("sendGiftSeqNum", sendGiftSeqNum);
        jsonObject.put("pkNum", pkNum);
        // 2 礼物特效url全直播间可见
        jsonObject.put("url", sendGiftMq.getUrl());
        // 3 搜索要发送的用户
        // 利用封装方法发送批量消息，bizCode为PK_SEND_SUCCESS
        this.batchSendImMsg(userIdList, ImMsgBizCodeEnum.LIVING_ROOM_PK_SEND_GIFT_SUCCESS.getCode(), jsonObject);
    }

    // 单向通知送礼对象
    private void sendImMsgSingleton(Long userId, Integer bizCode, JSONObject jsonObject) {
        ImMsgBody imMsgBody = new ImMsgBody();
        imMsgBody.setAppId(AppIdEnum.LIVE_BIZ.getCode());
        imMsgBody.setBizCode(bizCode);
        imMsgBody.setUserId(userId);
        imMsgBody.setData(jsonObject.toJSONString());
        routerRpc.sendMsg(imMsgBody);
    }

    // 批量发送im消息
    private void batchSendImMsg(List<Long> userIdList, Integer bizCode, JSONObject jsonObject) {
        List<ImMsgBody> imMsgBodies = new ArrayList<>();

        userIdList.forEach(userId -> {
            ImMsgBody imMsgBody = new ImMsgBody();
            imMsgBody.setAppId(AppIdEnum.LIVE_BIZ.getCode());
            imMsgBody.setBizCode(bizCode);
            imMsgBody.setData(jsonObject.toJSONString());
            imMsgBody.setUserId(userId);
            imMsgBodies.add(imMsgBody);
        });
        routerRpc.batchSendMsg(imMsgBodies);
    }
}
