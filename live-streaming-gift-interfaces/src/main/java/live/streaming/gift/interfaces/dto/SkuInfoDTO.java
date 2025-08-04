package live.streaming.gift.interfaces.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SkuInfoDTO implements Serializable {

    private static final long serialVersionUID = 5307182939235676125L;

    private Long id;

    private Long skuId;

    private Integer skuPrice;

    private String skuCode;

    private String name;

    private String iconUrl;

    private String originalIconUrl;

    private Integer status;

    private String remark;
}
