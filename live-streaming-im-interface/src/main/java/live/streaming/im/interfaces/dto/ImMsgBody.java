package live.streaming.im.interfaces.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class ImMsgBody implements Serializable {

    private static final long serialVersionUID = 5352424279595733146L;

    // 接入im服务的各个业务线id
    private int appId;

    // 用户id
    private long userId;

    // 从业务服务中获取，用于在im服务建立连接的时候使用
    private String token;

    // 业务标识
    private int bizCode;

    // 唯一的消息id
    private String msgId;

    // 和业务服务进行消息传递
    private String data;


}
