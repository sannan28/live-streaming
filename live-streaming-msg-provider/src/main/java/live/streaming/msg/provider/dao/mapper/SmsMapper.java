package live.streaming.msg.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import live.streaming.msg.provider.dao.po.SmsPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SmsMapper extends BaseMapper<SmsPO> {

}
