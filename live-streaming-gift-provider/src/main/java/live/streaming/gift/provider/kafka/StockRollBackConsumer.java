package live.streaming.gift.provider.kafka;

import com.alibaba.fastjson.JSON;
import live.streaming.gift.interfaces.bo.RollBackStockBO;
import live.streaming.gift.provider.service.ISkuStockInfoService;
import live.streaming.interfaces.topic.GiftProviderTopicNames;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.*;

@Slf4j
@Component
public class StockRollBackConsumer {

    @Resource
    private ISkuStockInfoService skuStockInfoService;

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
        });
    }


    @KafkaListener(topics = GiftProviderTopicNames.ROLL_BACK_STOCK, groupId = "stock-roll-back")
    public void stockRollBack(String rollBackStockBoStr) {
        RollBackStockBO rollBackStockBO = JSON.parseObject(rollBackStockBoStr, RollBackStockBO.class);
        DELAY_QUEUE.offer(new DelayedTask(30 * 60 * 1000, () -> skuStockInfoService.stockRollBackHandler(rollBackStockBO)));
        log.info("[StockRollBackConsumer] rollback success, rollbackInfo is {}", rollBackStockBO);
    }

}
