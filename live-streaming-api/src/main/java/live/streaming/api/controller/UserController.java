package live.streaming.api.controller;

import org.apache.dubbo.config.annotation.DubboReference;
import org.live.streaming.interfaces.dto.UserDTO;
import org.live.streaming.interfaces.rpc.IUserRpc;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @DubboReference
    private IUserRpc userRpc;

    @GetMapping
    public Map<Long, UserDTO> batchUseInfo(String userIdsStr) {

        return userRpc.batchUserIds(Arrays.asList(userIdsStr.split(","))
                .stream().map(Long::valueOf).collect(Collectors.toList()));
    }

    @GetMapping("/getUserInfo/{userId}")
    public UserDTO getUserInfo(@PathVariable Long userId) {
        return userRpc.getUserById(userId);
    }

    @GetMapping("/updateUserInfo")
    public boolean updateUserInfo(@RequestBody UserDTO userDTO) {
        return userRpc.updateUserInfo(userDTO);
    }

    @GetMapping("/insertUserInfo")
    public boolean insertUserInfo(@RequestBody UserDTO userDTO) {
        return userRpc.insertOne(userDTO);
    }

}
