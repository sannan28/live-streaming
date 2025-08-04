package live.streaming.gift.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkuOrderInfoRespDTO implements Serializable {

    private static final long serialVersionUID = 4088590644930299396L;

    private Long Id;

    private String skuIdList;

    private Long userId;

    private Integer roomId;

    private Integer status;

    private String extra;
}
