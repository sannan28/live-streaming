package live.streaming.gift.interfaces.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;


@Data
@ToString
public class GiftConfigDTO implements Serializable {

    private static final long serialVersionUID = 3504790560190578916L;

    private Integer giftId;

    private Integer price;

    private String giftName;

    private Integer status;

    private String coverImgUrl;

    private String svgaUrl;

    private Date createTime;

    private Date updateTime;

}
