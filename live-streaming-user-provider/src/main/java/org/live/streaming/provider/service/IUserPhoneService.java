package org.live.streaming.provider.service;

import org.live.streaming.interfaces.dto.UserLoginDTO;
import org.live.streaming.interfaces.dto.UserPhoneDTO;

import java.util.List;

public interface IUserPhoneService {

    // 用户登录（底层会进行手机号的注册）
    UserLoginDTO login(String phone);

    // 根据手机信息查询相关用户信息
    UserPhoneDTO queryByPhone(String phone);

    // 根据用户id查询手机相关信息
    List<UserPhoneDTO> queryByUserId(Long userId);
}
