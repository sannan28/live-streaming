package live.streaming.gift.provider.rpc;

import live.streaming.gift.interfaces.dto.ShopCarReqDTO;
import live.streaming.gift.interfaces.dto.ShopCarRespDTO;
import live.streaming.gift.interfaces.rpc.IShopCarRpc;
import live.streaming.gift.provider.service.IShopCarService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class ShopCarRpcImpl implements IShopCarRpc {

    @Resource
    private IShopCarService shopCarService;

    @Override
    public Boolean addCar(ShopCarReqDTO shopCarReqDTO) {
        return shopCarService.addCar(shopCarReqDTO);
    }

    @Override
    public Boolean removeFromCar(ShopCarReqDTO shopCarReqDTO) {
        return shopCarService.removeFromCar(shopCarReqDTO);
    }

    @Override
    public Boolean clearShopCar(ShopCarReqDTO shopCarReqDTO) {
        return shopCarService.clearShopCar(shopCarReqDTO);
    }

    @Override
    public Boolean addCarItemNum(ShopCarReqDTO shopCarReqDTO) {
        return shopCarService.addCarItemNum(shopCarReqDTO);
    }

    @Override
    public ShopCarRespDTO getCarInfo(ShopCarReqDTO shopCarReqDTO) {
        return shopCarService.getCarInfo(shopCarReqDTO);
    }
}
