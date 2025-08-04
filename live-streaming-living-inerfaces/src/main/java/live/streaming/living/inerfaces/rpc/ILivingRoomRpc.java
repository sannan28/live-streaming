package live.streaming.living.inerfaces.rpc;


import live.streaming.interfaces.dto.PageWrapper;
import live.streaming.living.inerfaces.dto.LivingPkRespDTO;
import live.streaming.living.inerfaces.dto.LivingRoomReqDTO;
import live.streaming.living.inerfaces.dto.LivingRoomRespDTO;

import java.util.List;

public interface ILivingRoomRpc {

    // 支持根据roomId查询出批量的userId（set）存储，3000个人，元素非常多，O(n)
    List<Long> queryUserIdByRoomId(LivingRoomReqDTO livingRoomReqDTO);

    // 直播间列表的分页查询
    PageWrapper<LivingRoomRespDTO> list(LivingRoomReqDTO livingRoomReqDTO);

    // 根据用户id查询是否正在开播
    LivingRoomRespDTO queryByRoomId(Integer roomId);

    // 开启直播间
    Integer startLivingRoom(LivingRoomReqDTO livingRoomReqDTO);

    // 关闭直播间
    boolean closeLiving(LivingRoomReqDTO livingRoomReqDTO);

    // 用户在pk直播间中，连上线请求
    LivingPkRespDTO onlinePk(LivingRoomReqDTO livingRoomReqDTO);

    // 根据roomId查询当前pk人是谁
    Long queryOnlinePkUserId(Integer roomId);

    //  用户在pk直播间下线
    boolean offlinePk(LivingRoomReqDTO livingRoomReqDTO);
}
