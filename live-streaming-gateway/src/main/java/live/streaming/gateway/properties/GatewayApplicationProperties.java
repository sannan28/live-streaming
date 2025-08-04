package live.streaming.gateway.properties;


import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@ToString
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "live.streaming.gateway")
public class GatewayApplicationProperties {

    private List<String> notCheckUrlList;

}
