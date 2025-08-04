package live.streaming.bank.interfaces.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class LiveCurrencyAccountDTO implements Serializable {

    private static final long serialVersionUID = 2494443806955793856L;

    private Long userId;

    private int currentBalance;

    private int totalCharged;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}
