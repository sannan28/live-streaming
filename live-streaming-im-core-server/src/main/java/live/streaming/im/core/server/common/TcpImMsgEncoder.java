package live.streaming.im.core.server.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


// 处理消息的编码过程
public class TcpImMsgEncoder extends MessageToByteEncoder {

    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf byteBuf) throws Exception {
        ImMsg imMsg = (ImMsg) msg;
        byteBuf.writeShort(imMsg.getMagic());
        byteBuf.writeInt(imMsg.getCode());
        byteBuf.writeInt(imMsg.getLen());
        byteBuf.writeBytes(imMsg.getBody());
    }
}

