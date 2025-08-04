package live.streaming.im.core.server.handler;

import io.netty.channel.ChannelHandlerContext;
import live.streaming.im.core.server.common.ImMsg;

public interface SimplyHandler {

    // 消息处理函数
    void handler(ChannelHandlerContext ctx, ImMsg imMsg);

}
