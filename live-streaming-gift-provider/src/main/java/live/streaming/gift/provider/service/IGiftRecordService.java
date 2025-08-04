package live.streaming.gift.provider.service;


import live.streaming.gift.interfaces.dto.GiftRecordDTO;

public interface IGiftRecordService {

    // 插入一条送礼记录
    void insertOne(GiftRecordDTO giftRecordDTO);
}
