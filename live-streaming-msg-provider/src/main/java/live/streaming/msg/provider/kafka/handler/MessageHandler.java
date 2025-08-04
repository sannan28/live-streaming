package live.streaming.msg.provider.kafka.handler;

import live.streaming.im.interfaces.dto.ImMsgBody;

public interface MessageHandler {

    void onMsgReceive(ImMsgBody imMsgBody);
}
