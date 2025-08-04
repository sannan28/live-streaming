package live.streaming.bank.provider.service.impl;

import live.streaming.bank.provider.dao.mapper.LiveCurrencyTradeMapper;
import live.streaming.bank.provider.dao.po.LiveCurrencyTradePO;
import live.streaming.bank.provider.service.ILiveCurrencyTradeService;
import live.streaming.interfaces.enums.CommonStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class LiveCurrencyTradeServiceImpl implements ILiveCurrencyTradeService {

    @Resource
    private LiveCurrencyTradeMapper liveCurrencyTradeMapper;

    @Override
    public boolean insertOne(Long userId, int num, int type) {
        try {
            LiveCurrencyTradePO tradePO = new LiveCurrencyTradePO();
            tradePO.setUserId(userId);
            tradePO.setNum(num);
            tradePO.setType(type);
            tradePO.setStatus(CommonStatusEnum.VALID_STATUS.getCode());
            liveCurrencyTradeMapper.insert(tradePO);
            return true;
        } catch (Exception e) {
            log.error("[liveCurrencyTradeServiceImpl] insert error, error is:", e);
        }
        return false;
    }
}
