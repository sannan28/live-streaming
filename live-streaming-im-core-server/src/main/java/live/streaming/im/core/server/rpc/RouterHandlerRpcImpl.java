package live.streaming.im.core.server.rpc;

import live.streaming.im.core.server.interfaces.rpc.IRouterHandlerRpc;
import live.streaming.im.core.server.service.IRouterHandlerService;
import live.streaming.im.interfaces.dto.ImMsgBody;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

@DubboService
public class RouterHandlerRpcImpl implements IRouterHandlerRpc {

    @Resource
    private IRouterHandlerService routerHandlerService;


    public void sendMsg(ImMsgBody imMsgBody) {
        routerHandlerService.onReceive(imMsgBody);
    }

    public void batchSendMsg(List<ImMsgBody> imMsgBodyList) {
        imMsgBodyList.forEach(imMsgBody -> {
            routerHandlerService.onReceive(imMsgBody);
        });
    }
}
