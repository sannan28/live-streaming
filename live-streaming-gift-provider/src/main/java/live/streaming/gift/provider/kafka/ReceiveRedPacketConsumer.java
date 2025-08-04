package live.streaming.gift.provider.kafka;

import com.alibaba.fastjson.JSON;
import live.streaming.gift.interfaces.bo.SendRedPacketBO;
import live.streaming.gift.provider.service.IRedPacketConfigService;
import live.streaming.interfaces.topic.GiftProviderTopicNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

// 处理抢红包mq消息的消费者
@Slf4j
@Component
public class ReceiveRedPacketConsumer {

    @Resource
    private IRedPacketConfigService redPacketConfigService;

    @KafkaListener(topics = GiftProviderTopicNames.RECEIVE_RED_PACKET, groupId = "receive-red-packet")
    public void receiveRedPacket(String sendRedPacketBOStr) {
        try {
            SendRedPacketBO sendRedPacketBO = JSON.parseObject(sendRedPacketBOStr, SendRedPacketBO.class);
            redPacketConfigService.receiveRedPacketHandler(sendRedPacketBO.getReqDTO(), sendRedPacketBO.getPrice());
            log.info("[ReceiveRedPacketConsumer] receiveRedPacket success");
        } catch (Exception e) {
            log.error("[ReceiveRedPacketConsumer] receiveRedPacket error, mqBody is {}", sendRedPacketBOStr);
        }
    }
}
