package live.streaming.framework.web.starter.config;

import live.streaming.framework.web.starter.context.LiveUserInfoInterceptor;
import live.streaming.framework.web.starter.context.RequestLimitInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public LiveUserInfoInterceptor liveUserInfoInterceptor() {
        return new LiveUserInfoInterceptor();
    }

    @Bean
    public RequestLimitInterceptor requestLimitInterceptor() {
        return new RequestLimitInterceptor();
    }

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(liveUserInfoInterceptor()).addPathPatterns("/**").excludePathPatterns("/error");
        registry.addInterceptor(requestLimitInterceptor()).addPathPatterns("/**").excludePathPatterns("/error");
    }

}
