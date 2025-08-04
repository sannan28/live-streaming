package live.streaming.living.provider.kafka;

import com.alibaba.fastjson.JSON;
import live.streaming.im.core.server.interfaces.dto.ImOnlineDTO;
import live.streaming.interfaces.topic.ImCoreServerProviderTopicNames;
import live.streaming.living.provider.service.ILivingRoomService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class LivingRoomOnlineConsumer {

    @Resource
    private ILivingRoomService livingRoomService;

    @KafkaListener(topics = ImCoreServerProviderTopicNames.IM_ONLINE_TOPIC, groupId = "im-online-consumer")
    public void consumeOnline(String imOnlineDTOStr) {
        ImOnlineDTO imOnlineDTO = JSON.parseObject(imOnlineDTOStr, ImOnlineDTO.class);
        livingRoomService.userOnlineHandler(imOnlineDTO);
    }


}
