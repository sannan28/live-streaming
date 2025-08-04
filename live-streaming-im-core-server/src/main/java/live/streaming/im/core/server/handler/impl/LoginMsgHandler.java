package live.streaming.im.core.server.handler.impl;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import live.streaming.im.core.server.common.ChannelHandlerContextCache;
import live.streaming.im.core.server.common.ImContextUtils;
import live.streaming.im.core.server.common.ImMsg;
import live.streaming.im.core.server.handler.SimplyHandler;
import live.streaming.im.core.server.interfaces.constants.ImCoreServerConstants;
import live.streaming.im.core.server.interfaces.dto.ImOnlineDTO;
import live.streaming.im.interfaces.constants.ImConstants;
import live.streaming.im.interfaces.dto.ImMsgBody;
import live.streaming.im.interfaces.enums.ImMsgCodeEnum;
import live.streaming.im.interfaces.interfaces.ImTokenRpc;
import live.streaming.interfaces.topic.ImCoreServerProviderTopicNames;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class LoginMsgHandler implements SimplyHandler {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @DubboReference
    private ImTokenRpc imTokenRpc;


    public void handler(ChannelHandlerContext ctx, ImMsg imMsg) {
        // 防止重复请求
        if (ImContextUtils.getUserId(ctx) != null) {
            return;
        }
        byte[] body = imMsg.getBody();
        if (body == null || body.length == 0) {
            ctx.close();
            log.error("body error,imMsg is {}", imMsg);
            throw new IllegalArgumentException("body error");
        }
        ImMsgBody imMsgBody = JSON.parseObject(new String(body), ImMsgBody.class);
        Long userIdFromMsg = imMsgBody.getUserId();
        Integer appId = imMsgBody.getAppId();
        String token = imMsgBody.getToken();
        if (StringUtils.isEmpty(token) || userIdFromMsg < 10000 || appId < 10000) {
            ctx.close();
            log.error("param error,imMsg is {}", imMsg);
            throw new IllegalArgumentException("param error");
        }
        Long userId = imTokenRpc.getUserIdByToken(token);
        //token校验成功，而且和传递过来的userId是同一个，则允许建立连接
        if (userId != null && userId.equals(userIdFromMsg)) {
            loginSuccessHandler(ctx, userId, appId, null);
            return;
        }
        ctx.close();
        log.error("token check error,imMsg is {}", imMsg);
        throw new IllegalArgumentException("token check error");


    }

    private void sendLoginMQ(Long userId, Integer appId, Integer roomId) {
        ImOnlineDTO imOnlineDTO = new ImOnlineDTO();
        imOnlineDTO.setUserId(userId);
        imOnlineDTO.setAppId(appId);
        imOnlineDTO.setRoomId(roomId);
        imOnlineDTO.setLoginTime(System.currentTimeMillis());
        ListenableFuture<SendResult<String, String>> sendResultListenableFuture = kafkaTemplate.send(ImCoreServerProviderTopicNames.IM_ONLINE_TOPIC, JSON.toJSONString(imOnlineDTO));
        sendResultListenableFuture.addCallback(result -> {
            log.info("[sendLoginMQ] send result is {}", result);
        }, ex -> {
            log.error(" [sendLoginMQ] send loginMQ error, error is  ", ex);
            throw new RuntimeException(ex);
        });
    }


    public void loginSuccessHandler(ChannelHandlerContext ctx, Long userId, Integer appId, Integer roomId) {
        // 按照userId保存好相关的channel对象信息
        ChannelHandlerContextCache.put(userId, ctx);
        ImContextUtils.setUserId(ctx, userId);
        ImContextUtils.setAppId(ctx, appId);
        if (roomId != null) {
            ImContextUtils.setRoomId(ctx, roomId);
        }
        // 将im消息回写给客户端
        ImMsgBody respBody = new ImMsgBody();
        respBody.setAppId(appId);
        respBody.setUserId(userId);
        respBody.setData("true");
        ImMsg respMsg = ImMsg.build(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(), JSON.toJSONString(respBody));
        stringRedisTemplate.opsForValue().set(ImCoreServerConstants.IM_BIND_IP_KEY + appId + ":" + userId,
                ChannelHandlerContextCache.getServerIpAddress() + "%" + userId,
                ImConstants.DEFAULT_HEART_BEAT_GAP * 2, TimeUnit.SECONDS);
        log.info("[LoginMsgHandler] login success,userId is {},appId is {}", userId, appId);
        ctx.writeAndFlush(respMsg);
        sendLoginMQ(userId, appId, roomId);
    }
}
