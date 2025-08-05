package live.streaming.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import live.streaming.api.error.ApiErrorEnum;
import live.streaming.api.service.IGiftService;
import live.streaming.api.vo.req.GiftReqVO;
import live.streaming.api.vo.resp.GiftConfigVO;
import live.streaming.bank.interfaces.rpc.LiveCurrencyAccountRpc;
import live.streaming.framework.web.starter.context.LiveRequestContext;
import live.streaming.framework.web.starter.error.ErrorAssert;
import live.streaming.gift.interfaces.dto.GiftConfigDTO;
import live.streaming.gift.interfaces.rpc.IGiftConfigRpc;
import live.streaming.interfaces.dto.SendGiftMq;
import live.streaming.interfaces.topic.GiftProviderTopicNames;
import live.streaming.interfaces.utils.ConvertBeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class GiftServiceImpl implements IGiftService {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @DubboReference
    private IGiftConfigRpc giftConfigRpc;

    @DubboReference
    private LiveCurrencyAccountRpc liveCurrencyAccountRpc;

    private Cache<Integer, GiftConfigDTO> giftConfigDTOCache =
            Caffeine.newBuilder().maximumSize(1000).expireAfterWrite(90, TimeUnit.SECONDS).build();


    @Override
    public List<GiftConfigVO> listGift() {

        List<GiftConfigDTO> giftConfigDTOList = giftConfigRpc.queryGiftList();

        return ConvertBeanUtils.convertList(giftConfigDTOList, GiftConfigVO.class);
    }

    @Override
    public boolean send(GiftReqVO giftReqVO) {
        int giftId = giftReqVO.getGiftId();
        // 查询本地缓存
        GiftConfigDTO giftConfigDTO = giftConfigDTOCache.get(giftId, id -> giftConfigRpc.getByGiftId(giftId));
        ErrorAssert.isNotNull(giftConfigDTO, ApiErrorEnum.GIFT_CONFIG_ERROR);
        ErrorAssert.isTure(!giftReqVO.getReceiverId().equals(giftReqVO.getSenderUserId()), ApiErrorEnum.NOT_SEND_TO_YOURSELF);
        // 进行异步消费
        SendGiftMq sendGiftMq = new SendGiftMq();
        sendGiftMq.setUserId(LiveRequestContext.getUserId());
        sendGiftMq.setGiftId(giftId);
        sendGiftMq.setRoomId(giftReqVO.getRoomId());
        sendGiftMq.setReceiverId(giftReqVO.getReceiverId());
        sendGiftMq.setPrice(giftConfigDTO.getPrice());
        sendGiftMq.setUrl(giftConfigDTO.getSvgaUrl());
        sendGiftMq.setType(giftReqVO.getType());
        // 设置唯一标识UUID，防止重复消费
        sendGiftMq.setUuid(UUID.randomUUID().toString());
        try {
            ListenableFuture<SendResult<String, String>> sendResultListenableFuture = kafkaTemplate.send(
                    GiftProviderTopicNames.SEND_GIFT,
                    // giftReqVO.getRoomId().toString(), //指定key，将相同roomId的送礼消息发送到一个分区，避免PK送礼消息出现乱序
                    JSON.toJSONString(sendGiftMq)
            );
            sendResultListenableFuture.addCallback(result -> {
                log.info(" [MsgAckCheckServiceImpl] msg is {},sendResult is {}", result);
            }, ex -> {
                log.error(" [MsgAckCheckServiceImpl] error is  ", ex);
                throw new RuntimeException(ex);
            });

        } catch (Exception e) {
            log.error(" [MsgAckCheckServiceImpl] error is  ", e);
            return false;
        }
        // 同步消费逻辑
        // AccountTradeReqDTO accountTradeReqDTO = new AccountTradeReqDTO();
        // accountTradeReqDTO.setUserId(QiyuRequestContext.getUserId());
        // accountTradeReqDTO.setNum(giftConfigDTO.getPrice());
        // AccountTradeRespDTO tradeRespDTO = qiyuCurrencyAccountRpc.consumeForSendGift(accountTradeReqDTO);
        // ErrorAssert.isTure(tradeRespDTO != null && tradeRespDTO.isSuccess(), ApiErrorEnum.SEND_GIFT_ERROR);
        return true;
    }
}
