package live.streaming.api.service.impl;

import live.streaming.api.error.ApiErrorEnum;
import live.streaming.api.service.ILivingRoomService;
import live.streaming.api.vo.LivingRoomInitVO;
import live.streaming.api.vo.req.LivingRoomReqVO;
import live.streaming.api.vo.req.OnlinePkReqVO;
import live.streaming.api.vo.resp.LivingRoomPageRespVO;
import live.streaming.api.vo.resp.LivingRoomRespVO;
import live.streaming.framework.web.starter.context.LiveRequestContext;
import live.streaming.framework.web.starter.error.ErrorAssert;
import live.streaming.im.interfaces.enums.AppIdEnum;
import live.streaming.interfaces.dto.PageWrapper;
import live.streaming.interfaces.utils.ConvertBeanUtils;
import live.streaming.living.inerfaces.dto.LivingPkRespDTO;
import live.streaming.living.inerfaces.dto.LivingRoomReqDTO;
import live.streaming.living.inerfaces.dto.LivingRoomRespDTO;
import live.streaming.living.inerfaces.rpc.ILivingRoomRpc;
import org.apache.dubbo.config.annotation.DubboReference;
import org.live.streaming.interfaces.dto.UserDTO;
import org.live.streaming.interfaces.rpc.IUserRpc;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LivingRoomServiceImpl implements ILivingRoomService {

    @DubboReference
    private IUserRpc userRpc;

    @DubboReference
    private ILivingRoomRpc livingRoomRpc;


    public LivingRoomPageRespVO list(LivingRoomReqVO livingRoomReqVO) {
        PageWrapper<LivingRoomRespDTO> resultPage = livingRoomRpc.list(ConvertBeanUtils.convert(livingRoomReqVO, LivingRoomReqDTO.class));
        LivingRoomPageRespVO livingRoomPageRespVO = new LivingRoomPageRespVO();
        livingRoomPageRespVO.setList(ConvertBeanUtils.convertList(resultPage.getList(), LivingRoomRespVO.class));
        livingRoomPageRespVO.setHasNext(resultPage.isHasNext());
        return livingRoomPageRespVO;
    }

    public Integer startingLiving(Integer type) {
        Long userId = LiveRequestContext.getUserId();
        UserDTO userDTO = userRpc.getUserById(userId);
        LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
        livingRoomReqDTO.setAnchorId(userId);
        livingRoomReqDTO.setRoomName("主播-" + LiveRequestContext.getUserId() + "的直播间");
        livingRoomReqDTO.setCovertImg(userDTO.getAvatar());
        livingRoomReqDTO.setType(type);
        return livingRoomRpc.startLivingRoom(livingRoomReqDTO);
    }

    public boolean onlinePk(OnlinePkReqVO onlinePkReqVO) {
        LivingRoomReqDTO reqDTO = ConvertBeanUtils.convert(onlinePkReqVO, LivingRoomReqDTO.class);
        reqDTO.setAppId(AppIdEnum.LIVE_BIZ.getCode());
        reqDTO.setPkObjId(LiveRequestContext.getUserId());
        LivingPkRespDTO tryOnlineStatus = livingRoomRpc.onlinePk(reqDTO);
//        ErrorAssert.isTure(tryOnlineStatus.isOnlineStatus(), new LiveErrorException(-1,tryOnlineStatus.getMsg()));
        return true;
    }

    public boolean closeLiving(Integer roomId) {
        LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
        livingRoomReqDTO.setRoomId(roomId);
        livingRoomReqDTO.setAnchorId(LiveRequestContext.getUserId());
        return livingRoomRpc.closeLiving(livingRoomReqDTO);
    }

    public LivingRoomInitVO anchorConfig(Long userId, Integer roomId) {
        LivingRoomRespDTO respDTO = livingRoomRpc.queryByRoomId(roomId);
        ErrorAssert.isNotNull(respDTO, ApiErrorEnum.LIVING_ROOM_END);
        Map<Long, UserDTO> userDTOMap = userRpc.batchQueryUserInfo(Arrays.asList(respDTO.getAnchorId(), userId).stream().distinct().collect(Collectors.toList()));
        UserDTO anchor = userDTOMap.get(respDTO.getAnchorId());
        UserDTO watcher = userDTOMap.get(userId);
        LivingRoomInitVO respVO = new LivingRoomInitVO();
        respVO.setAnchorNickName(anchor.getNickName());
        respVO.setWatcherNickName(watcher.getNickName());
        respVO.setUserId(userId);
        // 给定一个默认的头像
        respVO.setAvatar(StringUtils.isEmpty(anchor.getAvatar()) ? "https://s1.ax1x.com/2022/12/18/zb6q6f.png" : anchor.getAvatar());
        respVO.setWatcherAvatar(watcher.getAvatar());
        if (respDTO == null || respDTO.getAnchorId() == null || userId == null) {
            // 这种就是属于直播间已经不存在的情况了
            respVO.setRoomId(-1);
        } else {
            respVO.setRoomId(respDTO.getId());
            respVO.setAnchorId(respDTO.getAnchorId());
            respVO.setAnchor(respDTO.getAnchorId().equals(userId));
        }
        respVO.setDefaultBgImg("https://picst.sunbangyan.cn/2023/08/29/waxzj0.png");
        return respVO;
    }


}
