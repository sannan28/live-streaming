package live.streaming.im.router.interfaces.rpc;

import live.streaming.im.interfaces.dto.ImMsgBody;

import java.util.List;

public interface ImRouterRpc {


    // 发送消息
    boolean sendMsg(ImMsgBody imMsgBody);


    // 批量发送消息，在直播间内
    void batchSendMsg(List<ImMsgBody> imMsgBody);
}
