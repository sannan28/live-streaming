package live.streaming.gift.provider.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import live.streaming.gift.interfaces.dto.SkuOrderInfoReqDTO;
import live.streaming.gift.interfaces.dto.SkuOrderInfoRespDTO;
import live.streaming.gift.provider.dao.mapper.ISkuOrderInfoMapper;
import live.streaming.gift.provider.dao.po.SkuOrderInfoPO;
import live.streaming.gift.provider.service.ISkuOrderInfoService;
import live.streaming.interfaces.utils.ConvertBeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class SkuOrderInfoServiceImpl implements ISkuOrderInfoService {

    @Resource
    private ISkuOrderInfoMapper skuOrderInfoMapper;

    @Override
    public SkuOrderInfoRespDTO queryByUserIdAndRoomId(Long userId, Integer roomId) {
        LambdaQueryWrapper<SkuOrderInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuOrderInfoPO::getUserId, userId);
        queryWrapper.eq(SkuOrderInfoPO::getRoomId, roomId);
        queryWrapper.orderByDesc(SkuOrderInfoPO::getId);
        queryWrapper.last("limit 1");
        return ConvertBeanUtils.convert(skuOrderInfoMapper.selectOne(queryWrapper), SkuOrderInfoRespDTO.class);
    }

    @Override
    public SkuOrderInfoRespDTO queryByOrderId(Long orderId) {
        return ConvertBeanUtils.convert(skuOrderInfoMapper.selectById(orderId), SkuOrderInfoRespDTO.class);
    }

    @Override
    public SkuOrderInfoPO insertOne(SkuOrderInfoReqDTO skuOrderInfoReqDTO) {
        // hutool工具包的StrUtil
        String skuIdListStr = StrUtil.join(",", skuOrderInfoReqDTO.getSkuIdList());
        SkuOrderInfoPO skuOrderInfoPO = ConvertBeanUtils.convert(skuOrderInfoReqDTO, SkuOrderInfoPO.class);
        skuOrderInfoPO.setSkuIdList(skuIdListStr);
        skuOrderInfoMapper.insert(skuOrderInfoPO);
        return skuOrderInfoPO;
    }

    @Override
    public boolean updateOrderStatus(SkuOrderInfoReqDTO skuOrderInfoReqDTO) {
        SkuOrderInfoPO skuOrderInfoPO = new SkuOrderInfoPO();
        skuOrderInfoPO.setStatus(skuOrderInfoReqDTO.getStatus());
        skuOrderInfoPO.setId(skuOrderInfoReqDTO.getId());
        skuOrderInfoMapper.updateById(skuOrderInfoPO);
        return false;
    }
}
