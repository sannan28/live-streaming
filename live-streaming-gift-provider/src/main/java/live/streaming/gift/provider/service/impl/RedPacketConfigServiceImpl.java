package live.streaming.gift.provider.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import live.streaming.bank.interfaces.rpc.LiveCurrencyAccountRpc;
import live.streaming.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import live.streaming.gift.interfaces.bo.SendRedPacketBO;
import live.streaming.gift.interfaces.constants.RedPacketStatusEnum;
import live.streaming.gift.interfaces.dto.RedPacketConfigReqDTO;
import live.streaming.gift.interfaces.dto.RedPacketReceiveDTO;
import live.streaming.gift.provider.dao.mapper.IRedPacketConfigMapper;
import live.streaming.gift.provider.dao.po.RedPacketConfigPO;
import live.streaming.gift.provider.service.IRedPacketConfigService;
import live.streaming.im.interfaces.dto.ImMsgBody;
import live.streaming.im.interfaces.enums.AppIdEnum;
import live.streaming.im.router.interfaces.contants.ImMsgBizCodeEnum;
import live.streaming.im.router.interfaces.rpc.ImRouterRpc;
import live.streaming.interfaces.topic.GiftProviderTopicNames;
import live.streaming.interfaces.utils.ListUtils;
import live.streaming.living.inerfaces.dto.LivingRoomReqDTO;
import live.streaming.living.inerfaces.rpc.ILivingRoomRpc;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedPacketConfigServiceImpl implements IRedPacketConfigService {

    @Resource
    private IRedPacketConfigMapper redPacketConfigMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @DubboReference
    private ImRouterRpc routerRpc;

    @DubboReference
    private ILivingRoomRpc livingRoomRpc;

    @DubboReference
    private LiveCurrencyAccountRpc liveCurrencyAccountRpc;


    @Override
    public RedPacketConfigPO queryByAnchorId(Long anchorId) {
        LambdaQueryWrapper<RedPacketConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RedPacketConfigPO::getAnchorId, anchorId);
        queryWrapper.eq(RedPacketConfigPO::getStatus, RedPacketStatusEnum.WAIT.getCode());
        queryWrapper.orderByDesc(RedPacketConfigPO::getCreateTime);
        queryWrapper.last("limit 1");
        return redPacketConfigMapper.selectOne(queryWrapper);
    }

    @Override
    public RedPacketConfigPO queryByConfigCode(String code) {
        LambdaQueryWrapper<RedPacketConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RedPacketConfigPO::getConfigCode, code);
        queryWrapper.eq(RedPacketConfigPO::getStatus, RedPacketStatusEnum.IS_PREPARED.getCode());
        queryWrapper.orderByDesc(RedPacketConfigPO::getCreateTime);
        queryWrapper.last("limit 1");
        return redPacketConfigMapper.selectOne(queryWrapper);
    }

    @Override
    public boolean addOne(RedPacketConfigPO redPacketConfigPO) {
        redPacketConfigPO.setConfigCode(UUID.randomUUID().toString());
        return redPacketConfigMapper.insert(redPacketConfigPO) > 0;
    }

    @Override
    public boolean updateById(RedPacketConfigPO redPacketConfigPO) {
        return redPacketConfigMapper.updateById(redPacketConfigPO) > 0;
    }

    @Override
    public boolean prepareRedPacket(Long anchorId) {
        // 防止重复生成，以及错误参数传递情况
        RedPacketConfigPO redPacketConfigPO = this.queryByAnchorId(anchorId);
        if (redPacketConfigPO == null) {
            return false;
        }
        // 加锁保证原子性：仿重
        Boolean isLock = redisTemplate.opsForValue().setIfAbsent(cacheKeyBuilder.buildRedPacketInitLock(redPacketConfigPO.getConfigCode()), 1, 3L, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(isLock)) {
            return false;
        }
        Integer totalPrice = redPacketConfigPO.getTotalPrice();
        Integer totalCount = redPacketConfigPO.getTotalCount();
        List<Integer> priceList = this.createRedPacketPriceList(totalPrice, totalCount);
        String cacheKey = cacheKeyBuilder.buildRedPacketList(redPacketConfigPO.getConfigCode());
        // 将红包数据拆分为子集合进行插入到Redis，避免 Redis输入输出缓冲区 被填满
        List<List<Integer>> splitPriceList = ListUtils.splistList(priceList, 100);
        for (List<Integer> priceItemList : splitPriceList) {
            redisTemplate.opsForList().leftPushAll(cacheKey, priceItemList.toArray());
        }
        redisTemplate.expire(cacheKey, 1L, TimeUnit.DAYS);
        // 更改红包雨配置状态，防止重发
        redPacketConfigPO.setStatus(RedPacketStatusEnum.IS_PREPARED.getCode());
        this.updateById(redPacketConfigPO);
        // Redis中设置该红包雨已经准备好的标记
        redisTemplate.opsForValue().set(cacheKeyBuilder.buildRedPacketPrepareSuccess(redPacketConfigPO.getConfigCode()), 1, 1L, TimeUnit.DAYS);
        return true;
    }

    @Override
    public Boolean startRedPacket(RedPacketConfigReqDTO reqDTO) {
        String code = reqDTO.getRedPacketConfigCode();
        // 红包没有准备好，则返回false
        if (Boolean.FALSE.equals(redisTemplate.hasKey(cacheKeyBuilder.buildRedPacketPrepareSuccess(code)))) {
            return false;
        }
        // 红包已经开始过（有别的线程正在通知用户中），返回false
        String notifySuccessCacheKey = cacheKeyBuilder.buildRedPacketNotify(code);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(notifySuccessCacheKey))) {
            return false;
        }
        redisTemplate.opsForValue().set(notifySuccessCacheKey, 1, 1L, TimeUnit.DAYS);
        // 广播通知直播间所有用户开始抢红包了
        RedPacketConfigPO redPacketConfigPO = this.queryByConfigCode(code);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("redPacketConfig", JSON.toJSONString(redPacketConfigPO));
        LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
        livingRoomReqDTO.setRoomId(reqDTO.getRoomId());
        livingRoomReqDTO.setAppId(AppIdEnum.LIVE_BIZ.getCode());
        List<Long> userIdList = livingRoomRpc.queryUserIdByRoomId(livingRoomReqDTO);
        if (CollectionUtils.isEmpty(userIdList)) return false;
        this.batchSendImMsg(userIdList, ImMsgBizCodeEnum.RED_PACKET_CONFIG.getCode(), jsonObject);
        // 更改红包雨配置的状态为已发送
        redPacketConfigPO.setStatus(RedPacketStatusEnum.IS_SEND.getCode());
        this.updateById(redPacketConfigPO);
        return true;
    }

    @Override
    public RedPacketReceiveDTO receiveRedPacket(RedPacketConfigReqDTO redPacketConfigReqDTO) {
        String code = redPacketConfigReqDTO.getRedPacketConfigCode();
        // 从Redis中领取一个红包金额
        String cacheKey = cacheKeyBuilder.buildRedPacketList(code);
        Object priceObj = redisTemplate.opsForList().rightPop(cacheKey);
        if (priceObj == null) {
            return null;
        }
        Integer price = (Integer) priceObj;
        // 发送mq消息进行异步信息的统计，以及用户余额的增加
        SendRedPacketBO sendRedPacketBO = new SendRedPacketBO();
        sendRedPacketBO.setPrice(price);
        sendRedPacketBO.setReqDTO(redPacketConfigReqDTO);
        ListenableFuture<SendResult<String, String>> sendResultListenableFuture = kafkaTemplate.send(GiftProviderTopicNames.RECEIVE_RED_PACKET, JSON.toJSONString(sendRedPacketBO));
        try {
            sendResultListenableFuture.addCallback(result -> {
                log.info("[RedPacketConfigServiceImpl] user {} receive a redPacket, send success", redPacketConfigReqDTO.getUserId());
            }, ex -> {
                log.error("[RedPacketConfigServiceImpl] send error, userId is {}, price is {}", redPacketConfigReqDTO.getUserId(), price);
                throw new RuntimeException(ex);
            });
        } catch (Exception e) {
            return new RedPacketReceiveDTO(null, "抱歉，红包被人抢走了，再试试");
        }

        return new RedPacketReceiveDTO(price, "恭喜领取到红包：" + price + "旗鱼币！");
    }

    @Override
    public void receiveRedPacketHandler(RedPacketConfigReqDTO reqDTO, Integer price) {
        String code = reqDTO.getRedPacketConfigCode();
        String totalGetCountCacheKey = cacheKeyBuilder.buildRedPacketTotalGetCount(code);
        String totalGetPriceCacheKey = cacheKeyBuilder.buildRedPacketTotalGetPrice(code);
        // 记录该用户总共领取了多少金额的红包
        redisTemplate.opsForValue().increment(cacheKeyBuilder.buildUserTotalGetPrice(reqDTO.getUserId()), price);
        redisTemplate.opsForHash().increment(totalGetCountCacheKey, code, 1);
        redisTemplate.expire(totalGetCountCacheKey, 1L, TimeUnit.DAYS);
        redisTemplate.opsForHash().increment(totalGetPriceCacheKey, code, price);
        redisTemplate.expire(totalGetPriceCacheKey, 1L, TimeUnit.DAYS);
        // 往用户的余额里增加金额
        liveCurrencyAccountRpc.incr(reqDTO.getUserId(), price);
        // 持久化红包雨的totalGetCount和totalGetPrice
        redPacketConfigMapper.incrTotalGetPrice(code, price);
        redPacketConfigMapper.incrTotalGetCount(code);
    }

    //  二倍均值法：
    //     * 创建红包雨的每个红包金额数据
    private List<Integer> createRedPacketPriceList(Integer totalPrice, Integer totalCount) {
        List<Integer> redPacketPriceList = new ArrayList<>();
        for (int i = 0; i < totalCount; i++) {
            if (i + 1 == totalCount) {
                // 如果是最后一个红包
                redPacketPriceList.add(totalPrice);
                break;
            }
            int maxLimit = (totalPrice / (totalCount - i)) * 2;// 最大限额为平均值的两倍
            int currentPrice = ThreadLocalRandom.current().nextInt(1, maxLimit);
            totalPrice -= currentPrice;
            redPacketPriceList.add(currentPrice);
        }
        return redPacketPriceList;
    }

    // 批量发送im消息
    private void batchSendImMsg(List<Long> userIdList, Integer bizCode, JSONObject jsonObject) {
        List<ImMsgBody> imMsgBodies = new ArrayList<>();

        userIdList.forEach(userId -> {
            ImMsgBody imMsgBody = new ImMsgBody();
            imMsgBody.setAppId(AppIdEnum.LIVE_BIZ.getCode());
            imMsgBody.setBizCode(bizCode);
            imMsgBody.setData(jsonObject.toJSONString());
            imMsgBody.setUserId(userId);
            imMsgBodies.add(imMsgBody);
        });
        routerRpc.batchSendMsg(imMsgBodies);
    }
}
