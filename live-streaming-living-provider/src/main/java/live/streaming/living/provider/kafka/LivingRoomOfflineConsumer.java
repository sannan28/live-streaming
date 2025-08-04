package live.streaming.living.provider.kafka;


import com.alibaba.fastjson.JSON;
import live.streaming.im.core.server.interfaces.dto.ImOfflineDTO;
import live.streaming.interfaces.topic.ImCoreServerProviderTopicNames;
import live.streaming.living.provider.service.ILivingRoomService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class LivingRoomOfflineConsumer {

    @Resource
    private ILivingRoomService livingRoomService;

    @KafkaListener(topics = ImCoreServerProviderTopicNames.IM_OFFLINE_TOPIC, groupId = "im-offline-consumer")
    public void consumeOnline(String imOfflineDTOStr) {
        ImOfflineDTO imOfflineDTO = JSON.parseObject(imOfflineDTOStr, ImOfflineDTO.class);
        livingRoomService.userOfflineHandler(imOfflineDTO);
    }

}
