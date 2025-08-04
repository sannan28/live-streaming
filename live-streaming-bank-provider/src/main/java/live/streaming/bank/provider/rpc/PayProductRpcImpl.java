package live.streaming.bank.provider.rpc;

import live.streaming.bank.interfaces.dto.PayProductDTO;
import live.streaming.bank.interfaces.rpc.IPayProductRpc;
import live.streaming.bank.provider.service.IPayProductService;
import org.apache.dubbo.config.annotation.DubboService;
import javax.annotation.Resource;
import java.util.List;

@DubboService
public class PayProductRpcImpl implements IPayProductRpc {

    @Resource
    private IPayProductService payProductService;

    @Override
    public List<PayProductDTO> products(Integer type) {
        return payProductService.products(type);
    }

    @Override
    public PayProductDTO getByProductId(Integer productId) {
        return payProductService.getByProductId(productId);
    }
}
