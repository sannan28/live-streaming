package org.live.streaming.provider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.live.streaming.interfaces.dto.UserDTO;
import org.live.streaming.provider.dao.po.UserPO;

import java.util.List;
import java.util.Map;

public interface IUserService extends IService<UserPO> {

    Map<Long,UserDTO> batchQueryUserInfo(List<Long> userIdList);

    UserDTO getUserById(Long userId);

    boolean updateUserInfo(UserDTO userDTO);

    boolean insertOne(UserDTO userDTO);

}
