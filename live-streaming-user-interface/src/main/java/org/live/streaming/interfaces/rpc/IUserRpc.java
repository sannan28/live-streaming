package org.live.streaming.interfaces.rpc;


import org.live.streaming.interfaces.dto.UserDTO;

import java.util.List;
import java.util.Map;


public interface IUserRpc {

    Map<Long, UserDTO> batchUserIds(List<Long> userIdList);

    UserDTO getUserById(Long userId);

    boolean updateUserInfo(UserDTO userDTO);

    boolean insertOne(UserDTO userDTO);

    Map<Long,UserDTO> batchQueryUserInfo(List<Long> userIdList);

}
