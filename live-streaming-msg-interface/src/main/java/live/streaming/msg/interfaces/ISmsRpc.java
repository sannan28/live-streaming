package live.streaming.msg.interfaces;

import live.streaming.msg.dto.MsgCheckDTO;
import live.streaming.msg.enums.MsgSendResultEnum;

public interface ISmsRpc {


    // 发送短信接口
    MsgSendResultEnum sendLoginCode(String phone);

    // 校验登录验证码
    MsgCheckDTO checkLoginCode(String phone, Integer code);

    // 插入一条短信记录
    void insertOne(String phone, Integer code);

}
