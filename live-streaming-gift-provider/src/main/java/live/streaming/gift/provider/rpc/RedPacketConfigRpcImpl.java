package live.streaming.gift.provider.rpc;

import live.streaming.gift.interfaces.dto.RedPacketConfigReqDTO;
import live.streaming.gift.interfaces.dto.RedPacketConfigRespDTO;
import live.streaming.gift.interfaces.dto.RedPacketReceiveDTO;
import live.streaming.gift.interfaces.rpc.IRedPacketConfigRpc;
import live.streaming.gift.provider.dao.po.RedPacketConfigPO;
import live.streaming.gift.provider.service.IRedPacketConfigService;
import live.streaming.interfaces.utils.ConvertBeanUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class RedPacketConfigRpcImpl implements IRedPacketConfigRpc {

    @Resource
    private IRedPacketConfigService redPacketConfigService;

    @Override
    public RedPacketConfigRespDTO queryByAnchorId(Long anchorId) {
        return ConvertBeanUtils.convert(redPacketConfigService.queryByAnchorId(anchorId), RedPacketConfigRespDTO.class);
    }

    @Override
    public boolean addOne(RedPacketConfigReqDTO redPacketConfigReqDTO) {
        return redPacketConfigService.addOne(ConvertBeanUtils.convert(redPacketConfigReqDTO, RedPacketConfigPO.class));
    }

    @Override
    public boolean prepareRedPacket(Long anchorId) {
        return redPacketConfigService.prepareRedPacket(anchorId);
    }

    @Override
    public RedPacketReceiveDTO receiveRedPacket(RedPacketConfigReqDTO redPacketConfigReqDTO) {
        return redPacketConfigService.receiveRedPacket(redPacketConfigReqDTO);
    }

    @Override
    public Boolean startRedPacket(RedPacketConfigReqDTO reqDTO) {
        return redPacketConfigService.startRedPacket(reqDTO);
    }
}
