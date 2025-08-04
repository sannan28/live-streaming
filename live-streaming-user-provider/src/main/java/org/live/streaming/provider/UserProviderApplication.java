package org.live.streaming.provider;


import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.live.streaming.interfaces.dto.UserLoginDTO;
import org.live.streaming.provider.service.IUserPhoneService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import javax.annotation.Resource;

@EnableDubbo
@EnableDiscoveryClient
@SpringBootApplication
public class UserProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserProviderApplication.class, args);
    }

    /*@Resource
    private IUserPhoneService userPhoneService;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(UserProviderApplication.class);
        springApplication.run(args);

    }

    public void run(String... args) {
        String phone = "18243083968";
        UserLoginDTO userLoginDTO = userPhoneService.login(phone);

        System.out.println("LALALALALALALLALALALALA" + userLoginDTO);
        System.out.println("LALALALALALALLALALALALA" + userPhoneService.queryByUserId(userLoginDTO.getUserId()));
        System.out.println("LALALALALALALLALALALALA" + userPhoneService.queryByPhone(phone));

    }*/


}
