package live.streaming.msg.provider.kafka;

import com.alibaba.fastjson.JSON;
import live.streaming.im.interfaces.dto.ImMsgBody;
import live.streaming.interfaces.topic.ImCoreServerProviderTopicNames;
import live.streaming.msg.provider.kafka.handler.MessageHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ImBizMsgKafkaConsumer {

    @Resource
    private MessageHandler singleMessageHandler;


    @KafkaListener(topics = ImCoreServerProviderTopicNames.LIVE_IM_BIZ_MSG_TOPIC, groupId = "im-send-biz-msg")
    public void consumeImTopic(String msg) {
        System.out.println("consumeImTopic 来了 " + msg);
        ImMsgBody imMsgBody = JSON.parseObject(msg, ImMsgBody.class);
        singleMessageHandler.onMsgReceive(imMsgBody);
    }
}
