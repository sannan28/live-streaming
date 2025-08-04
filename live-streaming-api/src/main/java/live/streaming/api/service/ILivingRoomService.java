package live.streaming.api.service;

import live.streaming.api.vo.LivingRoomInitVO;
import live.streaming.api.vo.req.LivingRoomReqVO;
import live.streaming.api.vo.req.OnlinePkReqVO;
import live.streaming.api.vo.resp.LivingRoomPageRespVO;

public interface ILivingRoomService {

    // 直播间列表展示
    LivingRoomPageRespVO list(LivingRoomReqVO livingRoomReqVO);

    // 开启直播间
    Integer startingLiving(Integer type);

    // 用户在pk直播间中，连上线请求
    boolean onlinePk(OnlinePkReqVO onlinePkReqVO);

    // 关闭直播间
    boolean closeLiving(Integer roomId);

    // 根据用户id返回当前直播间相关信息
    LivingRoomInitVO anchorConfig(Long userId, Integer roomId);
}
