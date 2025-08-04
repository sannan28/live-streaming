package live.streaming.bank.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_live_currency_trade")
public class LiveCurrencyTradePO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer num;

    private Integer type;

    private Integer status;

    private Date createTime
            ;
    private Date updateTime;
}
