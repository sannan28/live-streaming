package live.streaming.gift.provider.rpc;

import live.streaming.gift.interfaces.dto.GiftRecordDTO;
import live.streaming.gift.interfaces.rpc.IGiftRecordRpc;
import live.streaming.gift.provider.service.IGiftConfigService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;


@DubboService
public class GiftRecordRpcImpl implements IGiftRecordRpc {

    @Resource
    private IGiftConfigService giftService;

    @Override
    public void insertOne(GiftRecordDTO giftRecordDTO) {

    }
}
