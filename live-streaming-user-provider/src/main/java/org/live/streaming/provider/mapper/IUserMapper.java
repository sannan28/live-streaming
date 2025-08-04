package org.live.streaming.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.live.streaming.provider.dao.po.UserPO;

@Mapper
public interface IUserMapper extends BaseMapper<UserPO> {
}
