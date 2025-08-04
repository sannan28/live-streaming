package live.streaming.msg.provider.rpc;

import live.streaming.msg.dto.MsgCheckDTO;
import live.streaming.msg.enums.MsgSendResultEnum;
import live.streaming.msg.interfaces.ISmsRpc;
import live.streaming.msg.provider.service.ISmsService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;


@DubboService
public class SmsRpcImpl implements ISmsRpc {

    @Resource
    private ISmsService smsService;

    public MsgSendResultEnum sendLoginCode(String phone) {
        return smsService.sendLoginCode(phone);
    }

    public MsgCheckDTO checkLoginCode(String phone, Integer code) {

        return smsService.checkLoginCode(phone, code);
    }

    public void insertOne(String phone, Integer code) {

    }
}
