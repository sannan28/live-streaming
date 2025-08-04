package live.streaming.im.core.server.handler.impl;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import live.streaming.im.core.server.common.ImContextUtils;
import live.streaming.im.core.server.common.ImMsg;
import live.streaming.im.core.server.handler.SimplyHandler;
import live.streaming.im.core.server.service.IMsgAckCheckService;
import live.streaming.im.interfaces.dto.ImMsgBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class AckImMsgHandler implements SimplyHandler {

    @Resource
    private IMsgAckCheckService msgAckCheckService;

    public void handler(ChannelHandlerContext ctx, ImMsg imMsg) {
        Long userId = ImContextUtils.getUserId(ctx);
        Integer appid = ImContextUtils.getAppId(ctx);
        if (userId == null && appid == null) {
            ctx.close();
            throw new IllegalArgumentException("attr is error");
        }
        msgAckCheckService.doMsgAck(JSON.parseObject(imMsg.getBody(), ImMsgBody.class));

    }
}
