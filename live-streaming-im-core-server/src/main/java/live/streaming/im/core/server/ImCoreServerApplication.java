package live.streaming.im.core.server;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDubbo
@SpringBootApplication
@EnableDiscoveryClient
public class ImCoreServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImCoreServerApplication.class, args);
    }
}
