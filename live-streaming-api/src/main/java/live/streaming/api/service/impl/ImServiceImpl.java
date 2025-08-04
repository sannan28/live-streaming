package live.streaming.api.service.impl;

import live.streaming.api.service.ImService;
import live.streaming.api.vo.resp.ImConfigVO;
import live.streaming.framework.web.starter.context.LiveRequestContext;
import live.streaming.im.interfaces.enums.AppIdEnum;
import live.streaming.im.interfaces.interfaces.ImTokenRpc;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;


@Service
public class ImServiceImpl implements ImService {

    @DubboReference
    private ImTokenRpc imTokenRpc;

    @Resource
    private DiscoveryClient discoveryClient;

    @Override
    public ImConfigVO getImConfig() {
        ImConfigVO imConfigVO = new ImConfigVO();
        imConfigVO.setToken(imTokenRpc.createImLoginToken(LiveRequestContext.getUserId(), AppIdEnum.LIVE_BIZ.getCode()));
        buildImServerAddress(imConfigVO);
        return imConfigVO;
    }


    private void buildImServerAddress(ImConfigVO imConfigVO) {
        List<ServiceInstance> serviceInstanceList = discoveryClient.getInstances("live-streaming-im-core-server");
        Collections.shuffle(serviceInstanceList);
        ServiceInstance aimInstance = serviceInstanceList.get(0);
        imConfigVO.setWsImServerAddress(aimInstance.getHost() + ":8088");
        imConfigVO.setTcpImServerAddress(aimInstance.getHost() + ":8087");
    }
}
