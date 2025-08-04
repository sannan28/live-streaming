package live.streaming.im.core.server.handler.impl;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import live.streaming.framework.redis.starter.key.ImCoreServerProviderCacheKeyBuilder;
import live.streaming.im.core.server.common.ImContextUtils;
import live.streaming.im.core.server.common.ImMsg;
import live.streaming.im.core.server.handler.SimplyHandler;
import live.streaming.im.core.server.interfaces.constants.ImCoreServerConstants;
import live.streaming.im.interfaces.constants.ImConstants;
import live.streaming.im.interfaces.dto.ImMsgBody;
import live.streaming.im.interfaces.enums.ImMsgCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class HeartBeatImMsgHandler implements SimplyHandler {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ImCoreServerProviderCacheKeyBuilder cacheKeyBuilder;


    public void handler(ChannelHandlerContext ctx, ImMsg imMsg) {
        // 心跳包基本校验
        Long userId = ImContextUtils.getUserId(ctx);
        Integer appId = ImContextUtils.getAppId(ctx);
        if (userId == null || appId == null) {
            log.error("attr error,imMsg is {}", imMsg);
            // 有可能是错误的消息包导致，直接放弃连接
            ctx.close();
            throw new IllegalArgumentException("attr is error");
        }
        // 心跳包record记录，redis存储心跳记录
        String redisKey = cacheKeyBuilder.buildImLoginTokenKey(userId, appId);  // spring.application.name + imOnlineZset:appId:userId % 10000
        //todo 过期时间非原子性
        this.recordOnlineTime(userId, redisKey);
        this.removeExpireRecord(redisKey);
        redisTemplate.expire(redisKey, 5, TimeUnit.MINUTES);
        // 延长用户之前保存的ip绑定记录时间
        stringRedisTemplate.expire(ImCoreServerConstants.IM_BIND_IP_KEY + appId + ":" + userId, ImConstants.DEFAULT_HEART_BEAT_GAP * 2, TimeUnit.SECONDS);
        ImMsgBody msgBody = new ImMsgBody();
        msgBody.setUserId(userId);
        msgBody.setAppId(appId);
        msgBody.setData("true");
        ImMsg respMsg = ImMsg.build(ImMsgCodeEnum.IM_HEARTBEAT_MSG.getCode(), JSON.toJSONString(msgBody));
        log.debug("[HeartBeatImMsgHandler] imMsg is {}", imMsg);
        ctx.writeAndFlush(respMsg);
    }

    // 清理掉过期不在线的用户留下的心跳记录(在两次心跳包的发送间隔中，如果没有重新更新score值，就会导致被删除)
    private void removeExpireRecord(String redisKey) { // 计算当前时间 1 分钟前的 Unix 时间戳（毫秒）
        redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, System.currentTimeMillis() - ImConstants.DEFAULT_HEART_BEAT_GAP * 1000 * 2);
    }

    // 记录用户最近一次心跳时间到zSet上
    private void recordOnlineTime(Long userId, String redisKey) { // key =  spring.application.name + imOnlineZset:appId:userId % 10000 value :userId 时间戳
        redisTemplate.opsForZSet().add(redisKey, userId, System.currentTimeMillis());
    }


}
