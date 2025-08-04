package live.streaming.im.router.provider.rpc;

import live.streaming.im.interfaces.dto.ImMsgBody;
import live.streaming.im.router.interfaces.rpc.ImRouterRpc;
import live.streaming.im.router.provider.service.ImRouterService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

@DubboService
public class ImRouterRpcImpl implements ImRouterRpc {

    @Resource
    private ImRouterService routerService;

    public boolean sendMsg(ImMsgBody imMsgBody) {
        return routerService.sendMsg(imMsgBody);
    }

    // 假设我们有100个immsgbody，调用100次im-core-server  2ms,200ms
    public void batchSendMsg(List<ImMsgBody> imMsgBodyList) {
        routerService.batchSendMsg(imMsgBodyList);
    }


}
