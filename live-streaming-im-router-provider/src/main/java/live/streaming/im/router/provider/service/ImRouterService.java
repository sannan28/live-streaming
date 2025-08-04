package live.streaming.im.router.provider.service;

import live.streaming.im.interfaces.dto.ImMsgBody;

import java.util.List;

public interface ImRouterService {


    // 发送消息
    boolean sendMsg(ImMsgBody imMsgBody);

    // 批量发送消息，群聊场景
    void batchSendMsg(List<ImMsgBody> imMsgBody);
}
