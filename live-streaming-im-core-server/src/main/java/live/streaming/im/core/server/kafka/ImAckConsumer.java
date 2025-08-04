package live.streaming.im.core.server.kafka;

import com.alibaba.fastjson.JSON;
import live.streaming.im.core.server.service.IMsgAckCheckService;
import live.streaming.im.core.server.service.IRouterHandlerService;
import live.streaming.im.interfaces.dto.ImMsgBody;
import live.streaming.interfaces.topic.ImCoreServerProviderTopicNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.*;

@Slf4j
@Component
public class ImAckConsumer {

    @Resource
    private IMsgAckCheckService msgAckCheckService;

    @Resource
    private IRouterHandlerService routerHandlerService;

    private static final DelayQueue<DelayedTask> DELAY_QUEUE = new DelayQueue<>();


    private static final ExecutorService DELAY_QUEUE_THREAD_POOL = new ThreadPoolExecutor(
            3, 10,
            10L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100)
    );

    @PostConstruct()
    private void init() {
        DELAY_QUEUE_THREAD_POOL.submit(() -> {
            while (true) {
                try {
                    DelayedTask task = DELAY_QUEUE.take();
                    task.execute();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Thread-im-ack-msg-retry");
    }

    @KafkaListener(topics = ImCoreServerProviderTopicNames.LIVE_IM_BIZ_MSG_TOPIC, groupId = "im-ack-msg-retry")
    public void consumeAckMsg(String imMsgBodyJson, Acknowledgment ack) {
        ImMsgBody imMsgBody = JSON.parseObject(imMsgBodyJson, ImMsgBody.class);

        DELAY_QUEUE.offer(new DelayedTask(4000, () -> {
            int retryTimes = msgAckCheckService.getMsgAckTimes(imMsgBody.getMsgId(), imMsgBody.getUserId(), imMsgBody.getAppId());
            log.info("[ImAckConsumer]retryTimes is {}, msgId is {}", retryTimes, imMsgBody.getMsgId());
            // 返回-1代表Redis中已经没有对应记录，代表ACK消息已经收到了
            if (retryTimes < 0) {
                return;
            }
            // 只支持一次重发
            if (retryTimes < 2) {
                // 发送消息给客户端
                routerHandlerService.sendMsgToClient(imMsgBody);
                // 再次记录未收到ack的消息记录，time加1
                msgAckCheckService.recordMsgAck(imMsgBody, retryTimes + 1);
                // 再次重发消息
                msgAckCheckService.sendDelayMsg(imMsgBody);
                log.info("[ImAckConsumer]DelayQueue重发了一次消息");
            } else {
                // 已经执行过一次重发，不再重试，直接删除
                msgAckCheckService.doMsgAck(imMsgBody);
            }
            //手动提交
            ack.acknowledge();
            log.info("[ImAckConsumer] Kafka手动提交了offset，msg is {}", imMsgBody);
        }));
    }


}
