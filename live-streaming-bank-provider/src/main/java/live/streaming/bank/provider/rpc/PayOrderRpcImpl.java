package live.streaming.bank.provider.rpc;

import live.streaming.bank.interfaces.dto.PayOrderDTO;
import live.streaming.bank.interfaces.rpc.IPayOrderRpc;
import live.streaming.bank.provider.dao.po.PayOrderPO;
import live.streaming.bank.provider.service.IPayOrderService;
import live.streaming.interfaces.utils.ConvertBeanUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class PayOrderRpcImpl implements IPayOrderRpc {

    @Resource
    private IPayOrderService payOrderService;

    @Override
    public String insertOne(PayOrderDTO payOrderDTO) {
        return payOrderService.insertOne(ConvertBeanUtils.convert(payOrderDTO, PayOrderPO.class));
    }

    @Override
    public boolean updateOrderStatus(Long id, Integer status) {
        return payOrderService.updateOrderStatus(id, status);
    }

    @Override
    public boolean updateOrderStatus(String orderId, Integer status) {
        return payOrderService.updateOrderStatus(orderId, status);
    }

    @Override
    public boolean payNotify(PayOrderDTO payOrderDTO) {
        return payOrderService.payNotify(payOrderDTO);
    }
}
