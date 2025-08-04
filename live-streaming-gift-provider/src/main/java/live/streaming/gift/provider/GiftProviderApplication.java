package live.streaming.gift.provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.concurrent.CountDownLatch;

@EnableDubbo
@SpringBootApplication
@EnableDiscoveryClient
public class GiftProviderApplication {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        SpringApplication.run(GiftProviderApplication.class, args);
        countDownLatch.await();
    }
}
