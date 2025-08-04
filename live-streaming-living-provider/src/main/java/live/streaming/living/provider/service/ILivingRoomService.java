package live.streaming.living.provider.service;

import live.streaming.im.core.server.interfaces.dto.ImOfflineDTO;
import live.streaming.im.core.server.interfaces.dto.ImOnlineDTO;
import live.streaming.interfaces.dto.PageWrapper;
import live.streaming.living.inerfaces.dto.LivingPkRespDTO;
import live.streaming.living.inerfaces.dto.LivingRoomReqDTO;
import live.streaming.living.inerfaces.dto.LivingRoomRespDTO;

import java.util.List;

public interface ILivingRoomService {

    // 支持根据roomId查询出批量的userId（set）存储，3000个人，元素非常多，O(n)
    List<Long> queryUserIdByRoomId(LivingRoomReqDTO livingRoomReqDTO);

    //用户下线处理
    void userOfflineHandler(ImOfflineDTO imOfflineDTO);

    // 用户上线处理
    void userOnlineHandler(ImOnlineDTO imOnlineDTO);

    // 查询所有的直播间类型
    List<LivingRoomRespDTO> listAllLivingRoomFromDB(Integer type);

    // 直播间列表的分页查询
    PageWrapper<LivingRoomRespDTO> list(LivingRoomReqDTO livingRoomReqDTO);

    // 根据roomId查询直播间
    LivingRoomRespDTO queryByRoomId(Integer roomId);

    // 开启直播间
    Integer startLivingRoom(LivingRoomReqDTO livingRoomReqDTO);

    // 根据roomId查询当前pk人是谁
    Long queryOnlinePkUserId(Integer roomId);

    // 用户在pk直播间中，连上线请求
    LivingPkRespDTO onlinePk(LivingRoomReqDTO livingRoomReqDTO);

    // 用户在pk直播间中，下线请求
    boolean offlinePk(LivingRoomReqDTO livingRoomReqDTO);

    boolean closeLiving(LivingRoomReqDTO livingRoomReqDTO);
}
