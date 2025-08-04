package org.live.streaming.provider.rpc;

import org.apache.dubbo.config.annotation.DubboService;
import org.live.streaming.interfaces.dto.UserLoginDTO;
import org.live.streaming.interfaces.dto.UserPhoneDTO;
import org.live.streaming.interfaces.rpc.IUserPhoneRPC;
import org.live.streaming.provider.service.IUserPhoneService;

import javax.annotation.Resource;
import java.util.List;

@DubboService
public class UserPhoneRPCImpl implements IUserPhoneRPC {

    @Resource
    private IUserPhoneService userPhoneService;

    @Override
    public UserLoginDTO login(String phone) {
        return userPhoneService.login(phone);
    }

    @Override
    public UserPhoneDTO queryByPhone(String phone) {
        return userPhoneService.queryByPhone(phone);
    }

    @Override
    public List<UserPhoneDTO> queryByUserId(Long userId) {
        return userPhoneService.queryByUserId(userId);
    }

}
