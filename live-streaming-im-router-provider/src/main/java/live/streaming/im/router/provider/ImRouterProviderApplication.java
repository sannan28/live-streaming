package live.streaming.im.router.provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDubbo
@SpringBootApplication
@EnableDiscoveryClient
public class ImRouterProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImRouterProviderApplication.class, args);
    }
}
