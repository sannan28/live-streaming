package live.streaming.im.core.server.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import live.streaming.im.interfaces.constants.ImConstants;

import java.util.List;

// 消息解码器
public class TcpImMsgDecoder extends ByteToMessageDecoder {

    private final int BASE_LEN = 2 + 4 + 4;

    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
        // bytebuf内容的基本校验，长度校验，magic值校验
        if (byteBuf.readableBytes() >= BASE_LEN) {
            if (byteBuf.readShort() != ImConstants.DEFAULT_MAGIC) {
                channelHandlerContext.close();
                return;
            }
            int code = byteBuf.readInt();
            int len = byteBuf.readInt();
            // 确保 bytebuf 剩余的消息长度足够
            if (byteBuf.readableBytes() < len) {
                channelHandlerContext.close();
                return;
            }
            byte[] body = new byte[len];
            byteBuf.readBytes(body);
            // 将bytebuf转换为immsg对象
            ImMsg imMsg = new ImMsg();
            imMsg.setCode(code);
            imMsg.setLen(len);
            imMsg.setMagic(ImConstants.DEFAULT_MAGIC);
            imMsg.setBody(body);
            out.add(imMsg);
        }
    }
}
