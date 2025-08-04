package live.streaming.api.service.impl;

import live.streaming.api.service.IHomePageService;
import live.streaming.api.vo.HomePageVO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.live.streaming.interfaces.constants.UserTagsEnum;
import org.live.streaming.interfaces.dto.UserDTO;
import org.live.streaming.interfaces.rpc.IUserRpc;
import org.live.streaming.interfaces.rpc.IUserTagRpc;
import org.springframework.stereotype.Service;


@Service
public class HomePageServiceImpl implements IHomePageService {

    @DubboReference
    private IUserRpc userRpc;

    @DubboReference
    private IUserTagRpc userTagRpc;

    public HomePageVO initPage(Long userId) {
        UserDTO userDTO = userRpc.getUserById(userId);
        HomePageVO homePageVO = new HomePageVO();
        if (userDTO != null) {
            homePageVO.setAvatar(userDTO.getAvatar());
            homePageVO.setUserId(userId);
            homePageVO.setNickName(userDTO.getNickName());
            // vip用户有权利开播
            homePageVO.setShowStartLivingBtn(userTagRpc.containTag(userId, UserTagsEnum.IS_VIP));
        }
        return homePageVO;
    }
}
