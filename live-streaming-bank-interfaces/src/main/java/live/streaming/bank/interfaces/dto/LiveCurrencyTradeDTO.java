package live.streaming.bank.interfaces.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class LiveCurrencyTradeDTO implements Serializable {


    private static final long serialVersionUID = -1948886516533987591L;

    private Long id;

    private Long userId;

    private Integer num;

    private Integer type;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}
