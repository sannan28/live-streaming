package live.streaming.im.core.server.handler.impl;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import live.streaming.im.core.server.common.ImContextUtils;
import live.streaming.im.core.server.common.ImMsg;
import live.streaming.im.core.server.handler.SimplyHandler;
import live.streaming.interfaces.topic.ImCoreServerProviderTopicNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;

@Slf4j
@Component
public class BizImMsgHandler implements SimplyHandler {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    public void handler(ChannelHandlerContext ctx, ImMsg imMsg) {
        System.out.println(" BizImMsgHandler 走了 " + new String(imMsg.getBody()));
        System.out.println("BizImMsgHandler userId" + ImContextUtils.getUserId(ctx));
        System.out.println("BizImMsgHandler appId" + ImContextUtils.getAppId(ctx));
        //前期的参数校验
        Long userId = ImContextUtils.getUserId(ctx);
        Integer appId = ImContextUtils.getAppId(ctx);
        if (userId == null || appId == null) {
            log.error("attr error,imMsg is {}", imMsg);
            //有可能是错误的消息包导致，直接放弃连接
            ctx.close();
            throw new IllegalArgumentException("attr is error");
        }
        byte[] body = imMsg.getBody();
        if (body == null || body.length == 0) {
            log.error("body error,imMsg is {}", imMsg);
            return;
        }
        ListenableFuture<SendResult<String, String>> sendResultListenableFuture = kafkaTemplate.send(ImCoreServerProviderTopicNames.LIVE_IM_BIZ_MSG_TOPIC, new String(body));
        sendResultListenableFuture.addCallback(result -> {
            log.info("[BizImMsgHandler]消息投递成功, sendResult is {}", result);
        }, ex -> {
            log.error("send error ,erros is :", ex);
            throw new RuntimeException(ex);
        });
    }
}
