package org.live.streaming.provider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.live.streaming.interfaces.constants.UserTagsEnum;
import org.live.streaming.provider.dao.po.UserTagPo;

public interface IUserTagService extends IService<UserTagPo> {

    // 设置标签
    boolean setTag(Long userId, UserTagsEnum userTagsEnum);

    // 取消标签
    boolean cancelTag(Long userId, UserTagsEnum userTagsEnum);

    // 是否包含某个标签
    boolean containTag(Long userId, UserTagsEnum userTagsEnum);

}
