package live.streaming.gift.interfaces.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SkuDetailInfoDTO implements Serializable {


    private static final long serialVersionUID = -2730901004380501165L;

    private Long skuId;

    private Integer skuPrice;

    private String skuCode;

    private String name;

    private String iconUrl;

    private String originalIconUrl;

    private String remark;

    //还有其它复杂数据
}
