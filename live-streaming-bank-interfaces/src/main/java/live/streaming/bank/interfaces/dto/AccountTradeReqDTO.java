package live.streaming.bank.interfaces.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AccountTradeReqDTO implements Serializable {


    private static final long serialVersionUID = -3420057211795855691L;

    private Long userId;

    private int num;
}
