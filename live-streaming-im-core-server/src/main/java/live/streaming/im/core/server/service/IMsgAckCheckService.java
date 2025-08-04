package live.streaming.im.core.server.service;

import live.streaming.im.interfaces.dto.ImMsgBody;


public interface IMsgAckCheckService {

    // 主要是客户端发送ack包给到服务端后，调用进行ack记录的移除
    void doMsgAck(ImMsgBody imMsgBody);

    // 记录下消息的ack和times
    void recordMsgAck(ImMsgBody imMsgBody, int times);

    // 发送延迟消息，用于进行消息重试功能
    void sendDelayMsg(ImMsgBody imMsgBody);

    // 获取ack消息的重试次数
    int getMsgAckTimes(String msgId, long userId, int appId);

}
