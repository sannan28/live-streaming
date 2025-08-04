package live.streaming.gift.provider.rpc;

import live.streaming.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import live.streaming.gift.interfaces.dto.SkuDetailInfoDTO;
import live.streaming.gift.interfaces.dto.SkuInfoDTO;
import live.streaming.gift.interfaces.rpc.ISkuInfoRpc;
import live.streaming.gift.provider.service.IAnchorShopInfoService;
import live.streaming.gift.provider.service.ISkuInfoService;
import live.streaming.interfaces.utils.ConvertBeanUtils;
import org.apache.dubbo.config.annotation.DubboService;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@DubboService
public class SkuInfoRpcImpl implements ISkuInfoRpc {

    @Resource
    private ISkuInfoService skuInfoService;

    @Resource
    private IAnchorShopInfoService anchorShopInfoService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;

    @Override
    public List<SkuInfoDTO> queryByAnchorId(Long anchorId) {
        String cacheKey = cacheKeyBuilder.buildSkuDetailInfoMap(anchorId);
        List<SkuInfoDTO> skuInfoDTOS = redisTemplate.opsForHash().values(cacheKey).stream().map(x -> (SkuInfoDTO) x).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(skuInfoDTOS)) {
            if (skuInfoDTOS.get(0).getSkuId() == null) {
                return Collections.emptyList();
            }
            return skuInfoDTOS;
        }
        List<Long> skuIdList = anchorShopInfoService.querySkuIdsByAnchorId(anchorId);
        if (CollectionUtils.isEmpty(skuIdList)) {
            return Collections.emptyList();
        }
        skuInfoDTOS = ConvertBeanUtils.convertList(skuInfoService.queryBySkuIds(skuIdList), SkuInfoDTO.class);
        if (CollectionUtils.isEmpty(skuInfoDTOS)) {
//            redisTemplate.opsForHash().put(cacheKey, -1, new PayProductDTO());
            redisTemplate.expire(cacheKey, 1L, TimeUnit.MINUTES);
            return Collections.emptyList();
        }
        // 使用Redis进行缓存
        Map<String, SkuInfoDTO> skuInfoMap = skuInfoDTOS.stream().collect(Collectors.toMap(x -> String.valueOf(x.getSkuId()), x -> x));
        redisTemplate.opsForHash().putAll(cacheKey, skuInfoMap);
        redisTemplate.expire(cacheKey, 30L, TimeUnit.MINUTES);
        return skuInfoDTOS;
    }

    @Override
    public SkuDetailInfoDTO queryBySkuId(Long skuId, Long anchorId) {
        String cacheKey = cacheKeyBuilder.buildSkuDetailInfoMap(anchorId);
        SkuInfoDTO skuInfoDTO = (SkuInfoDTO) redisTemplate.opsForHash().get(cacheKey, String.valueOf(skuId));
        if (skuInfoDTO != null) {
            return ConvertBeanUtils.convert(skuInfoDTO, SkuDetailInfoDTO.class);
        }
        skuInfoDTO = ConvertBeanUtils.convert(skuInfoService.queryBySkuId(skuId), SkuInfoDTO.class);
        if (skuInfoDTO != null) {
            redisTemplate.opsForHash().put(cacheKey, String.valueOf(skuId), skuInfoDTO);
        }
        return ConvertBeanUtils.convert(skuInfoDTO, SkuDetailInfoDTO.class);
    }
}
