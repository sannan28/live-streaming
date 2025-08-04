package live.streaming.gift.provider.kafka;

import live.streaming.gift.interfaces.rpc.ISkuStockInfoRpc;
import live.streaming.interfaces.topic.GiftProviderTopicNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class StartLivingRoomConsumer {

    @Resource
    private ISkuStockInfoRpc skuStockInfoRpc;

    @KafkaListener(topics = GiftProviderTopicNames.START_LIVING_ROOM, groupId = "start-living-room-consumer")
    public void startLivingRoom(String anchorIdStr) {
        Long anchorId = Long.valueOf(anchorIdStr);
        boolean isSuccess = skuStockInfoRpc.prepareStockInfo(anchorId);
        if (isSuccess) {
            log.info("[StartLivingRoomConsumer] 同步库存到Redis成功，主播id：{}", anchorId);
        }
    }
}
