package live.streaming.gift.provider.service.impl;

import live.streaming.gift.interfaces.dto.GiftRecordDTO;
import live.streaming.gift.provider.dao.mapper.GiftRecordMapper;
import live.streaming.gift.provider.dao.po.GiftRecordPO;
import live.streaming.gift.provider.service.IGiftRecordService;
import live.streaming.interfaces.utils.ConvertBeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class GiftRecordServiceImpl implements IGiftRecordService {

    @Resource
    private GiftRecordMapper giftRecordMapper;

    @Override
    public void insertOne(GiftRecordDTO giftRecordDTO) {
        GiftRecordPO giftRecordPO = ConvertBeanUtils.convert(giftRecordDTO, GiftRecordPO.class);
        giftRecordMapper.insert(giftRecordPO);
    }
}
