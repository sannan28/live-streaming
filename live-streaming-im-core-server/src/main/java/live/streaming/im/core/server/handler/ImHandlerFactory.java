package live.streaming.im.core.server.handler;


import io.netty.channel.ChannelHandlerContext;
import live.streaming.im.core.server.common.ImMsg;

public interface ImHandlerFactory {

    // 按照immsg的code去筛选
    void doMsgHandler(ChannelHandlerContext channelHandlerContext, ImMsg imMsg);
}
