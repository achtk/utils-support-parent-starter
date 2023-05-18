package com.chua.common.support.lang.pool;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * 配置
 *
 * @author CH
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Accessors(fluent = true)
public class PoolConfig<T> extends GenericObjectPoolConfig<T> {

    private int core;

    public PoolConfig(int core) {
        this.core = core;
        setMaxTotal(core);
    }

    /**
     * 最大数量
     *
     * @param maxTotal 最大数量
     * @return this
     */
    public PoolConfig<T> maxTotal(int maxTotal) {
        setMaxTotal(maxTotal);
        return this;
    }


    /**
     * 最大等待数量
     *
     * @param maxIdle 最大等待数量
     * @return this
     */
    public PoolConfig<T> maxIdle(int maxIdle) {
        setMaxIdle(maxIdle);
        return this;
    }

    /**
     * 最小等待数量
     *
     * @param minIdle 最小等待数量
     * @return this
     */
    public PoolConfig<T> minIdle(int minIdle) {
        setMinIdle(minIdle);
        return this;
    }
}
