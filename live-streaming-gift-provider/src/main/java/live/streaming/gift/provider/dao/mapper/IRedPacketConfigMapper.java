package live.streaming.gift.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import live.streaming.gift.provider.dao.po.RedPacketConfigPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface IRedPacketConfigMapper extends BaseMapper<RedPacketConfigPO> {

    @Update("update t_red_packet_config set total_get_price = total_get_price + #{price} where config_code = #{code}")
    void incrTotalGetPrice(@Param("code") String code, @Param("price") Integer price);

    @Update("update t_red_packet_config set total_get = total_get + 1 where config_code = #{code}")
    void incrTotalGetCount(String code);
}
