package live.streaming.api.vo.resp;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class GiftConfigVO {
    private Integer giftId;

    private Integer price;

    private String giftName;

    private Integer status;

    private String coverImgUrl;

    private String svgaUrl;

    private Date createTime;

    private Date updateTime;

}
