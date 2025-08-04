package live.streaming.api.service;

import live.streaming.interfaces.vo.WebResponseVO;

import javax.servlet.http.HttpServletResponse;

public interface IUserLoginService {

    // 发送登录验证码
    WebResponseVO sendLoginCode(String phone);

    // 手机号+验证码登录
    WebResponseVO login(String phone, Integer code, HttpServletResponse response);

}
