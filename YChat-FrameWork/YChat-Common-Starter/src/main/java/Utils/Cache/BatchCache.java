package Utils.Cache;

import java.util.List;
import java.util.Map;

/**
 * 批量缓存框架，用来解决 Cache-Aside 旁路缓存 策略下无法解决的批量查找缓存最终循环查询数据库的弊端
 * @param <IN>
 * @param <OUT>
 */
public interface BatchCache<IN, OUT> {
    /**
     * 获取单个
     */
    OUT get(IN req);

    /**
     * 获取批量
     */
    Map<IN, OUT> getBatch(List<IN> req);

    /**
     * 修改删除单个
     */
    void delete(IN req);

    /**
     * 修改删除多个
     */
    void deleteBatch(List<IN> req);
}
