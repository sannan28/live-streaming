package live.streaming.gift.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkuOrderInfoReqDTO implements Serializable {


    private static final long serialVersionUID = 1505217407896770386L;

    private Long id;

    private Long userId;

    private Integer roomId;

    private Integer status;

    private List<Long> skuIdList;
}
