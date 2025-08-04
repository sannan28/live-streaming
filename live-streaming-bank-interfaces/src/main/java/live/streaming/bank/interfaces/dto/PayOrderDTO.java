package live.streaming.bank.interfaces.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PayOrderDTO implements Serializable {


    private static final long serialVersionUID = 6745736925760814207L;

    private Long id;

    private String orderId;

    private Integer productId;

    private Integer bizCode;

    private Long userId;

    private Integer source;

    private Integer payChannel;

    private Integer status;

    private Date payTime;
}
