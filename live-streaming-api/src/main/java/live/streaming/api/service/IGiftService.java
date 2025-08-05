package live.streaming.api.service;

import live.streaming.api.vo.req.GiftReqVO;
import live.streaming.api.vo.resp.GiftConfigVO;

import java.util.List;

public interface IGiftService {

    List<GiftConfigVO> listGift();

    boolean send(GiftReqVO giftReqVO);

}
