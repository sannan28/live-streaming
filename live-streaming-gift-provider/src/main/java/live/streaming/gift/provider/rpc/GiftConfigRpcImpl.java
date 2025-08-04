package live.streaming.gift.provider.rpc;

import live.streaming.gift.interfaces.dto.GiftConfigDTO;
import live.streaming.gift.interfaces.rpc.IGiftConfigRpc;
import live.streaming.gift.provider.service.IGiftConfigService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

@DubboService
public class GiftConfigRpcImpl implements IGiftConfigRpc {

    @Resource
    private IGiftConfigService giftService;

    @Override
    public GiftConfigDTO getByGiftId(Integer giftId) {
        return giftService.getByGiftId(giftId);
    }

    @Override
    public List<GiftConfigDTO> queryGiftList() {
        return giftService.queryGiftList();
    }

    @Override
    public void insertOne(GiftConfigDTO giftConfigDTO) {
        giftService.insertOne(giftConfigDTO);
    }

    @Override
    public void updateOne(GiftConfigDTO giftConfigDTO) {
        giftService.updateOne(giftConfigDTO);
    }
}
