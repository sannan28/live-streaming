package live.streaming.im.core.server.service.impl;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import live.streaming.im.core.server.common.ChannelHandlerContextCache;
import live.streaming.im.core.server.common.ImMsg;
import live.streaming.im.core.server.service.IMsgAckCheckService;
import live.streaming.im.core.server.service.IRouterHandlerService;
import live.streaming.im.interfaces.dto.ImMsgBody;
import live.streaming.im.interfaces.enums.ImMsgCodeEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;

@Service
public class RouterHandlerServiceImpl implements IRouterHandlerService {

    @Resource
    private IMsgAckCheckService msgAckCheckService;


    public void onReceive(ImMsgBody imMsgBody) {

        // 需要进行消息通知的userid
        if (sendMsgToClient(imMsgBody)) {
            // 当im服务器推送了消息给到客户端，然后我们需要记录下ack
            msgAckCheckService.recordMsgAck(imMsgBody, 1);
            msgAckCheckService.sendDelayMsg(imMsgBody);
        }
    }

    public boolean sendMsgToClient(ImMsgBody imMsgBody) {
        Long userId = imMsgBody.getUserId();
        ChannelHandlerContext ctx = ChannelHandlerContextCache.get(userId);
        if (ctx != null) {
            String msgId = UUID.randomUUID().toString();
            imMsgBody.setMsgId(msgId);
            ImMsg respMsg = ImMsg.build(ImMsgCodeEnum.IM_BIZ_MSG.getCode(), JSON.toJSONString(imMsgBody));
            ctx.writeAndFlush(respMsg);
            return true;
        }
        return false;
    }
}
