package live.streaming.bank.interfaces.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PayProductDTO implements Serializable {


    private static final long serialVersionUID = -7242184808428819558L;

    private Long id;

    private String name;

    private Integer price;

    private String extra;

    private Integer type;

    private Integer validStatus;

    private Date createTime;

    private Date updateTime;
}
