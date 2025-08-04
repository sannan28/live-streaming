package live.streaming.interfaces.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

// 分页查询所用的包装类
@Data
@ToString
public class PageWrapper<T> implements Serializable {

    private List<T> list;

    private boolean hasNext;
}
