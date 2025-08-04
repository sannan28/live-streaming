package live.streaming.gift.interfaces.rpc;

import live.streaming.gift.interfaces.dto.GiftRecordDTO;

public interface IGiftRecordRpc {

    // 插入一条送礼记录
    void insertOne(GiftRecordDTO giftRecordDTO);
}
