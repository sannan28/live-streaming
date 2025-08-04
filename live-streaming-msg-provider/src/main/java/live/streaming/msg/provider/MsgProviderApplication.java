package live.streaming.msg.provider;

import live.streaming.msg.enums.MsgSendResultEnum;
import live.streaming.msg.provider.service.ISmsService;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import javax.annotation.Resource;


@EnableDubbo
@SpringBootApplication
@EnableDiscoveryClient
public class MsgProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsgProviderApplication.class, args);

    }

    /*@Resource
    private ISmsService smsService;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(MsgProviderApplication.class);
        springApplication.run(args);
    }

    public void run(String... args) {
        MsgSendResultEnum msgSendResultEnum = smsService.sendLoginCode("18243083968");
        System.out.println("The result is:" + msgSendResultEnum);
    }*/

}
