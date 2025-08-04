package live.streaming.im.core.server.handler.impl;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import live.streaming.im.core.server.common.ChannelHandlerContextCache;
import live.streaming.im.core.server.common.ImContextUtils;
import live.streaming.im.core.server.common.ImMsg;
import live.streaming.im.core.server.handler.SimplyHandler;
import live.streaming.im.core.server.interfaces.constants.ImCoreServerConstants;
import live.streaming.im.core.server.interfaces.dto.ImOfflineDTO;
import live.streaming.im.interfaces.dto.ImMsgBody;
import live.streaming.im.interfaces.enums.ImMsgCodeEnum;
import live.streaming.interfaces.topic.ImCoreServerProviderTopicNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;

// 登出消息的处理逻辑统一收拢到这个类中
@Slf4j
@Component
public class LogoutMsgHandler implements SimplyHandler {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;


    public void handler(ChannelHandlerContext ctx, ImMsg imMsg) {
        Long userId = ImContextUtils.getUserId(ctx);
        Integer appId = ImContextUtils.getAppId(ctx);
        if (userId == null || appId == null) {
            log.error("attr error,imMsg is {}", imMsg);
            // 有可能是错误的消息包导致，直接放弃连接
            ctx.close();
            throw new IllegalArgumentException("attr is error");
        }
        // 将im消息回写给客户端
        logoutMsgNotice(ctx, userId, appId);
        logoutHandler(ctx, userId, appId);
    }

    public void sendLogoutMQ(ChannelHandlerContext ctx, Long userId, Integer appId) {
        ImOfflineDTO imOfflineDTO = new ImOfflineDTO();
        imOfflineDTO.setUserId(userId);
        imOfflineDTO.setRoomId(ImContextUtils.getRoomId(ctx));
        imOfflineDTO.setAppId(appId);
        imOfflineDTO.setLoginTime(System.currentTimeMillis());

        ListenableFuture<SendResult<String, String>> sendResultListenableFuture = kafkaTemplate.send(ImCoreServerProviderTopicNames.IM_ONLINE_TOPIC, JSON.toJSONString(imOfflineDTO));
        sendResultListenableFuture.addCallback(result -> {
            log.info("[sendLogoutMQ] send result is {}", result);
        }, ex -> {
            log.error(" [sendLogoutMQ] send loginMQ error, error is   ", ex);
            throw new RuntimeException(ex);
        });

    }


    public void logoutHandler(ChannelHandlerContext ctx, Long userId, Integer appId) {
        log.info("[LogoutMsgHandler] logout success,userId is {},appId is {}", userId, appId);
        // 理想情况下，客户端断线的时候，会发送一个断线消息包
        ChannelHandlerContextCache.remove(userId);
        stringRedisTemplate.delete(ImCoreServerConstants.IM_BIND_IP_KEY + appId + ":" + userId);
        ImContextUtils.removeUserId(ctx);
        ImContextUtils.removeAppId(ctx);
        sendLogoutMQ(ctx, userId, appId);
    }

    private void logoutMsgNotice(ChannelHandlerContext ctx, Long userId, Integer appId) {
        ImMsgBody respBody = new ImMsgBody();
        respBody.setAppId(appId);
        respBody.setUserId(userId);
        respBody.setData("true");
        ImMsg respMsg = ImMsg.build(ImMsgCodeEnum.IM_LOGOUT_MSG.getCode(), JSON.toJSONString(respBody));
        ctx.writeAndFlush(respMsg);
        ctx.close();
    }


}
