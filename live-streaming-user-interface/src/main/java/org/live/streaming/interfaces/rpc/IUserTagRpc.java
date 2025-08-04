package org.live.streaming.interfaces.rpc;

import org.live.streaming.interfaces.constants.UserTagsEnum;

// 用户标签RPC服务
public interface IUserTagRpc {

    // 设置标签
    boolean setTag(Long userId, UserTagsEnum userTagsEnum);

    // 取消标签
    boolean cancelTag(Long userId, UserTagsEnum userTagsEnum);

    // 是否包含某个标签
    boolean containTag(Long userId, UserTagsEnum userTagsEnum);

}
