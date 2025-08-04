package live.streaming.gift.interfaces.rpc;

import live.streaming.gift.interfaces.dto.SkuDetailInfoDTO;
import live.streaming.gift.interfaces.dto.SkuInfoDTO;

import java.util.List;

public interface ISkuInfoRpc {

    // 根据anchorId查询skuInfoList
    List<SkuInfoDTO> queryByAnchorId(Long anchorId);

    SkuDetailInfoDTO queryBySkuId(Long skuId, Long anchorId);
}
