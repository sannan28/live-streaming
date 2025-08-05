package live.streaming.api.vo.req;

import lombok.Data;

@Data
public class PayProductReqVO {

    // 产品id
    private Integer productId;

    // 支付来源（直播间内，用户中心），用于统计支付页面来源 PaySourceEnum
    private Integer paySource;

    // 支付渠道 PayChannelEnum
    private Integer payChannel;
}
