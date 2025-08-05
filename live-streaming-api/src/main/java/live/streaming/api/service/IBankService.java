package live.streaming.api.service;

import live.streaming.api.vo.req.PayProductReqVO;
import live.streaming.api.vo.resp.PayProductRespVO;
import live.streaming.api.vo.resp.PayProductVO;

public interface IBankService {

    // 查询相关产品信息
    PayProductVO products(Integer type);

    // 发起支付
    PayProductRespVO payProduct(PayProductReqVO payProductReqVO);

}
