package live.streaming.framework.web.starter.context;

import live.streaming.framework.web.starter.constants.RequestConstants;
import live.streaming.interfaces.enums.GatewayHeaderEnum;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LiveUserInfoInterceptor implements HandlerInterceptor {

    // 所有web请求来到这里的时候，都要被拦截
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userIdStr = request.getHeader(GatewayHeaderEnum.USER_LOGIN_ID.getName());
        // 参数判断，userID是否为空
        // 可能走的是白名单url
        if (StringUtils.isEmpty(userIdStr)) {
            return true;
        }
        // 如果userId不为空，则把它放在线程本地变量里面去
        LiveRequestContext.set(RequestConstants.USER_ID, Long.valueOf(userIdStr));
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        LiveRequestContext.clear();
    }


}
