package live.streaming.msg.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

// 发送消息的内容
@Data
@ToString
public class MessageDTO implements Serializable {


    private static final long serialVersionUID = 9009929998347233965L;

    private Long userId;

    private Integer roomId;
    // 发送人名称
    private String senderName;
    // 发送人头像
    private String senderAvtar;

    // 消息类型
    private Integer type;
    // 消息内容
    private String content;

    private Date createTime;

    private Date updateTime;

//    private Long objectId;
}
