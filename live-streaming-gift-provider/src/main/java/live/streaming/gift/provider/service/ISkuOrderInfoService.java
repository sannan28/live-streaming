package live.streaming.gift.provider.service;

import live.streaming.gift.interfaces.dto.SkuOrderInfoReqDTO;
import live.streaming.gift.interfaces.dto.SkuOrderInfoRespDTO;
import live.streaming.gift.provider.dao.po.SkuOrderInfoPO;

public interface ISkuOrderInfoService {

    // 根据userId和roomId查询订单信息
    SkuOrderInfoRespDTO queryByUserIdAndRoomId(Long userId, Integer roomId);

    // 插入一条订单
    SkuOrderInfoPO insertOne(SkuOrderInfoReqDTO skuOrderInfoReqDTO);

    // 更新订单状态
    boolean updateOrderStatus(SkuOrderInfoReqDTO skuOrderInfoReqDTO);


    SkuOrderInfoRespDTO queryByOrderId(Long orderId);
}
