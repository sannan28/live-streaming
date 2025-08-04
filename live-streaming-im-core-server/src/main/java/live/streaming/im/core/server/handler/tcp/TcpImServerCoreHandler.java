package live.streaming.im.core.server.handler.tcp;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import live.streaming.im.core.server.common.ImContextUtils;
import live.streaming.im.core.server.common.ImMsg;
import live.streaming.im.core.server.handler.ImHandlerFactory;
import live.streaming.im.core.server.handler.impl.LogoutMsgHandler;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


// im 消息的统一handler入口
@Component
@ChannelHandler.Sharable
public class TcpImServerCoreHandler extends SimpleChannelInboundHandler {

    @Resource
    private ImHandlerFactory imHandlerFactory;

    @Resource
    private LogoutMsgHandler logoutMsgHandler;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    //
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("啦啦啦啦啦啦啦啦啦" + JSON.toJSONString(msg));
        if (!(msg instanceof ImMsg)) {
            throw new IllegalArgumentException();
        }
        ImMsg imMsg = (ImMsg) msg;
        imHandlerFactory.doMsgHandler(ctx, imMsg);
    }

    // 正常或者意外断线，都会触发到这里
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Long userId = ImContextUtils.getUserId(ctx);
        Integer appId = ImContextUtils.getAppId(ctx);
        if (userId != null && appId != null) {
            logoutMsgHandler.logoutHandler(ctx, userId, appId);
        }
    }
}
