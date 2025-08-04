package live.streaming.msg.provider.service;


import live.streaming.msg.dto.MsgCheckDTO;
import live.streaming.msg.enums.MsgSendResultEnum;

public interface ISmsService {

    // 发送短信接口
    MsgSendResultEnum sendLoginCode(String phone);

    // 校验登录验证码
    MsgCheckDTO checkLoginCode(String phone, Integer code);

    // 插入一条短信验证码记录
    void insertOne(String phone, Integer code);

}
