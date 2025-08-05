package live.streaming.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import live.streaming.api.service.IBankService;
import live.streaming.api.vo.req.PayProductReqVO;
import live.streaming.api.vo.resp.PayProductItemVO;
import live.streaming.api.vo.resp.PayProductRespVO;
import live.streaming.api.vo.resp.PayProductVO;
import live.streaming.bank.interfaces.constants.OrderStatusEnum;
import live.streaming.bank.interfaces.constants.PaySourceEnum;
import live.streaming.bank.interfaces.dto.PayOrderDTO;
import live.streaming.bank.interfaces.dto.PayProductDTO;
import live.streaming.bank.interfaces.rpc.IPayOrderRpc;
import live.streaming.bank.interfaces.rpc.IPayProductRpc;
import live.streaming.bank.interfaces.rpc.LiveCurrencyAccountRpc;
import live.streaming.framework.web.starter.context.LiveRequestContext;
import live.streaming.framework.web.starter.error.BizBaseErrorEnum;
import live.streaming.framework.web.starter.error.ErrorAssert;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class BankServiceImpl implements IBankService {

    @DubboReference
    private IPayProductRpc payProductRpc;

    @DubboReference
    private LiveCurrencyAccountRpc liveCurrencyAccountRpc;

    @DubboReference
    private IPayOrderRpc payOrderRpc;

    @Resource
    private RestTemplate restTemplate;


    public PayProductVO products(Integer type) {
        List<PayProductDTO> payProductDTOS = payProductRpc.products(type);
        List<PayProductItemVO> payProductItemVOS = new ArrayList<>();
        for (PayProductDTO payProductDTO : payProductDTOS) {
            PayProductItemVO payProductItemVO = new PayProductItemVO();
            payProductItemVO.setId(payProductDTO.getId());
            payProductItemVO.setName(payProductDTO.getName());
            payProductItemVO.setCoinNum(JSON.parseObject(payProductDTO.getExtra()).getInteger("coin"));
            payProductItemVOS.add(payProductItemVO);
        }
        PayProductVO payProductVO = new PayProductVO();
        payProductVO.setCurrentBalance(liveCurrencyAccountRpc.getBalance(LiveRequestContext.getUserId()));
        payProductVO.setPayProductItemVOList(payProductItemVOS);
        return payProductVO;
    }


    public PayProductRespVO payProduct(PayProductReqVO payProductReqVO) {
        // 参数校验
        ErrorAssert.isTure(payProductReqVO != null && payProductReqVO.getProductId() != null && payProductReqVO.getPaySource() != null, BizBaseErrorEnum.PARAM_ERROR);
        ErrorAssert.isNotNull(PaySourceEnum.find(payProductReqVO.getPaySource()), BizBaseErrorEnum.PARAM_ERROR);
        // 查询payProductDTO
        PayProductDTO payProductDTO = payProductRpc.getByProductId(payProductReqVO.getProductId());
        ErrorAssert.isNotNull(payProductDTO, BizBaseErrorEnum.PARAM_ERROR);

        // 生成一条订单（待支付状态）
        PayOrderDTO payOrderDTO = new PayOrderDTO();
        payOrderDTO.setProductId(payProductReqVO.getProductId());
        payOrderDTO.setUserId(LiveRequestContext.getUserId());
        payOrderDTO.setPayTime(new Date());
        payOrderDTO.setSource(payProductReqVO.getPaySource());
        payOrderDTO.setPayChannel(payProductReqVO.getPayChannel());
        String orderId = payOrderRpc.insertOne(payOrderDTO);
        // 模拟点击 去支付 按钮，更新订单状态为 支付中
        payOrderRpc.updateOrderStatus(orderId, OrderStatusEnum.PAYING.getCode());
        PayProductRespVO payProductRespVO = new PayProductRespVO();
        payProductRespVO.setOrderId(orderId);

        // TODO 这里应该是支付成功后吗，由第三方支付所做的事情，因为我们是模拟支付，所以我们直接发起支付成功后的回调请求：
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("orderId", orderId);
        jsonObject.put("userId", LiveRequestContext.getUserId());
        jsonObject.put("bizCode", 10001);
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("param", jsonObject.toJSONString());
        // 使用RestTemplate进行HTTP的发送
        ResponseEntity<String> resultEntity = restTemplate.postForEntity("http://localhost:8201/live/bank/payNotify/wxNotify?param={param}", null, String.class, paramMap);
        System.out.println(resultEntity.getBody());

        return payProductRespVO;
    }


}
