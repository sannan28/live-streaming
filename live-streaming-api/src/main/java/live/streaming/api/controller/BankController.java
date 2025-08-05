package live.streaming.api.controller;

import live.streaming.api.service.IBankService;
import live.streaming.api.vo.req.PayProductReqVO;
import live.streaming.framework.web.starter.error.BizBaseErrorEnum;
import live.streaming.framework.web.starter.error.ErrorAssert;
import live.streaming.interfaces.vo.WebResponseVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/bank")
public class BankController {

    @Resource
    private IBankService bankService;

    @PostMapping("/products")
    public WebResponseVO products(Integer type) {
        ErrorAssert.isNotNull(type, BizBaseErrorEnum.PARAM_ERROR);
        return WebResponseVO.success(bankService.products(type));
    }

    @PostMapping("/payProduct")
    public WebResponseVO payProduct(PayProductReqVO payProductReqVO) {
        return WebResponseVO.success(bankService.payProduct(payProductReqVO));
    }
}
