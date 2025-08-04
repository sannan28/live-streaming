package live.streaming.gateway.filter;

import live.streaming.account.interfaces.IAccountTokenRPC;
import live.streaming.gateway.properties.GatewayApplicationProperties;
import live.streaming.interfaces.enums.GatewayHeaderEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;

import static io.netty.handler.codec.http.cookie.CookieHeaderNames.MAX_AGE;
import static org.springframework.web.cors.CorsConfiguration.ALL;

@Slf4j
@Component
public class AccountCheckFilter implements GlobalFilter, Ordered {

    @DubboReference
    private IAccountTokenRPC accountTokenRPC;

    @Resource
    private GatewayApplicationProperties gatewayApplicationProperties;

    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取请求url，判断是否为空，如果为空则返回请求不通过
        ServerHttpRequest request = exchange.getRequest();

        String reqUrl = request.getURI().getPath();
        ServerHttpResponse response = exchange.getResponse();

        HttpHeaders headers = response.getHeaders();
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://127.0.0.1:5500");
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS");
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, ALL);
        headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, MAX_AGE);

        if (StringUtils.isEmpty(reqUrl)) {
            return Mono.empty();
        }

        //根据url，判断是否存在于url白名单中，如果存在，则不对token进行校验
        List<String> notCheckUrlList = gatewayApplicationProperties.getNotCheckUrlList();
        for (String notCheckUrl : notCheckUrlList) {
            if (reqUrl.startsWith(notCheckUrl)) {
                log.info("请求没有进行token校验，直接传达给业务下游");
                //直接将请求转给下游
                return chain.filter(exchange);
            }
        }
        //如果不存在url白名单，那么就需要提取cookie，并且对cookie做基本的格式校验
        List<HttpCookie> httpCookieList = request.getCookies().get("livetk");
        if (CollectionUtils.isEmpty(httpCookieList)) {
            log.error("请求没有检索到livetk的cookie，被拦截");
            return Mono.empty();
        }
        String liveTokenCookieValue = httpCookieList.get(0).getValue();
        if (StringUtils.isEmpty(liveTokenCookieValue) || StringUtils.isEmpty(liveTokenCookieValue.trim())) {
            log.error("请求的cookie中的livetk是空，被拦截");
            return Mono.empty();
        }
        //token获取到之后，调用rpc判断token是否合法，如果合法则吧token换取到的userId传递给到下游
        Long userId = accountTokenRPC.getUserIdByToken(liveTokenCookieValue);
        //如果token不合法，则拦截请求，日志记录token失效
        if (userId == null) {
            log.error("请求的token失效了，被拦截");
            return Mono.empty();
        }
        // gateway --(header)--> springboot-web(interceptor-->get header)
        ServerHttpRequest.Builder builder = request.mutate();
        builder.header(GatewayHeaderEnum.USER_LOGIN_ID.getName(), String.valueOf(userId));
        return chain.filter(exchange.mutate().request(builder.build()).build());
    }

    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
