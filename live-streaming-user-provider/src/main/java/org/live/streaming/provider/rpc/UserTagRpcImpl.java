package org.live.streaming.provider.rpc;

import org.apache.dubbo.config.annotation.DubboService;
import org.live.streaming.interfaces.constants.UserTagsEnum;
import org.live.streaming.interfaces.rpc.IUserTagRpc;
import org.live.streaming.provider.service.IUserTagService;

import javax.annotation.Resource;

@DubboService
public class UserTagRpcImpl implements IUserTagRpc {

    @Resource
    private IUserTagService userTagService;

    public boolean setTag(Long userId, UserTagsEnum userTagsEnum) {
        return userTagService.setTag(userId, userTagsEnum);
    }

    public boolean cancelTag(Long userId, UserTagsEnum userTagsEnum) {
        return userTagService.cancelTag(userId, userTagsEnum);
    }

    public boolean containTag(Long userId, UserTagsEnum userTagsEnum) {
        return userTagService.containTag(userId, userTagsEnum);
    }

}
