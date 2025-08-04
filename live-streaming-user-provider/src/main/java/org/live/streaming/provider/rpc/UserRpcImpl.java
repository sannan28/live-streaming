package org.live.streaming.provider.rpc;

import org.apache.dubbo.config.annotation.DubboService;
import org.live.streaming.interfaces.dto.UserDTO;
import org.live.streaming.interfaces.rpc.IUserRpc;
import org.live.streaming.provider.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;


@DubboService
public class UserRpcImpl implements IUserRpc {

    private static final Logger log = LoggerFactory.getLogger(UserRpcImpl.class);

    @Autowired
    private IUserService userService;


    @Override
    public Map<Long, UserDTO> batchUserIds(List<Long> userIdList) {

        return userService.batchQueryUserInfo(userIdList);
    }

    public UserDTO getUserById(Long userId) {
        return userService.getUserById(userId);
    }

    public boolean updateUserInfo(UserDTO userDTO) {

        return userService.updateUserInfo(userDTO);
    }

    public boolean insertOne(UserDTO userDTO) {
        return userService.insertOne(userDTO);
    }

    public Map<Long, UserDTO> batchQueryUserInfo(List<Long> userIdList) {
        return userService.batchQueryUserInfo(userIdList);
    }
}
