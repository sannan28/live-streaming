package live.streaming.api.service.impl;

import live.streaming.account.interfaces.IAccountTokenRPC;
import live.streaming.api.service.IUserLoginService;
import live.streaming.api.vo.UserLoginVO;
import live.streaming.interfaces.utils.ConvertBeanUtils;
import live.streaming.interfaces.vo.WebResponseVO;
import live.streaming.msg.dto.MsgCheckDTO;
import live.streaming.msg.enums.MsgSendResultEnum;
import live.streaming.msg.interfaces.ISmsRpc;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.live.streaming.interfaces.dto.UserLoginDTO;
import org.live.streaming.interfaces.rpc.IUserPhoneRPC;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;


@Slf4j
@Service
public class UserLoginServiceImpl implements IUserLoginService {

    private static String PHONE_REG = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$";

    @DubboReference
    private ISmsRpc smsRpc;

    @DubboReference
    private IUserPhoneRPC userPhoneRPC;

    @DubboReference
    private IAccountTokenRPC accountTokenRPC;


    public WebResponseVO sendLoginCode(String phone) {

//        ErrorAssert.isNotBlank(phone, ApiErrorEnum.PHONE_IS_EMPTY);
//        ErrorAssert.isTure(Pattern.matches(PHONE_REG, phone), ApiErrorEnum.PHONE_IN_VALID);
        MsgSendResultEnum msgSendResultEnum = smsRpc.sendLoginCode(phone);
        if (msgSendResultEnum == MsgSendResultEnum.SEND_SUCCESS) {
            return WebResponseVO.success();
        }
        return WebResponseVO.sysError("短信发送太频繁，请稍后再试");
    }

    public WebResponseVO login(String phone, Integer code, HttpServletResponse response) {
//        ErrorAssert.isNotBlank(phone, ApiErrorEnum.PHONE_IS_EMPTY);
//        ErrorAssert.isTure(Pattern.matches(PHONE_REG, phone), ApiErrorEnum.PHONE_IN_VALID);
//        ErrorAssert.isTure(code != null && code > 1000, ApiErrorEnum.SMS_CODE_ERROR);
        MsgCheckDTO msgCheckDTO = smsRpc.checkLoginCode(phone, code);
        if (!msgCheckDTO.isCheckStatus()) {
            return WebResponseVO.bizError(msgCheckDTO.getDesc());
        }
        // 验证码校验通过
        UserLoginDTO userLoginDTO = userPhoneRPC.login(phone);
//        ErrorAssert.isTure(userLoginDTO.isLoginSuccess(),ApiErrorEnum.USER_LOGIN_ERROR);
        System.out.println("登录用户id是：" + userLoginDTO.getUserId());
        String token = accountTokenRPC.createAndSaveLoginToken(userLoginDTO.getUserId());
        Cookie cookie = new Cookie("livetk", token);
        //http://app.live.com/html/live_list_room.html
        //http://api.live.com/live/api/userLogin/sendLoginCode
        cookie.setDomain("127.0.0.1");
        cookie.setPath("/");
        // cookie有效期，一般他的默认单位是秒
        cookie.setMaxAge(30 * 24 * 3600);
        // 加上它，不然web浏览器不会将cookie自动记录下
        response.addCookie(cookie);
        return WebResponseVO.success(ConvertBeanUtils.convert(userLoginDTO, UserLoginVO.class));
    }
}
