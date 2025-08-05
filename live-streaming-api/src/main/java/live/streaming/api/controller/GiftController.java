package live.streaming.api.controller;

import live.streaming.api.service.IGiftService;
import live.streaming.api.vo.req.GiftReqVO;
import live.streaming.api.vo.resp.GiftConfigVO;
import live.streaming.interfaces.vo.WebResponseVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/gift")
public class GiftController {

    @Resource
    private IGiftService giftService;


    // 获取礼物列表
    @PostMapping("/listGift")
    public WebResponseVO listGift() {
        List<GiftConfigVO> giftConfigVOS = giftService.listGift();
        return WebResponseVO.success(giftConfigVOS);
    }

    // 发送礼物方法
    // 具体实现在后边的章节会深入讲解
    @PostMapping("/send")
    public WebResponseVO send(GiftReqVO giftReqVO) {
        return WebResponseVO.success(giftService.send(giftReqVO));
    }


}
