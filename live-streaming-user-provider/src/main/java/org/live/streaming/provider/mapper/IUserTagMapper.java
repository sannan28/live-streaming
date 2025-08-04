package org.live.streaming.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.live.streaming.provider.dao.po.UserTagPo;

@Mapper
public interface IUserTagMapper extends BaseMapper<UserTagPo> {

    // and 后面是保证是不存在tag标签才设置，保证第一次设置返回的才是true
    @Update("update t_user_tag set ${fieldName} = ${fieldName} | #{tag} where user_id = #{userId} and ${fieldName} & #{tag} = 0")
    int setTag(Long userId, String fieldName, long tag);


    // and 后面是保证是存在tag标签才撤销，保证第一次撤销返回的才是true
    @Update("update t_user_tag set ${fieldName} = ${fieldName} &~ #{tag} where user_id = #{userId} and ${fieldName} & #{tag} = #{tag}")
    int cancelTag(Long userId, String fieldName, long tag);

}
