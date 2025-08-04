package live.streaming.im.core.server.service;

import live.streaming.im.interfaces.dto.ImMsgBody;

public interface IRouterHandlerService {

    // 当收到业务服务的请求，进行处理
    void onReceive(ImMsgBody imMsgBody);

    // 发送消息给客户端
    boolean sendMsgToClient(ImMsgBody imMsgBody);
}
