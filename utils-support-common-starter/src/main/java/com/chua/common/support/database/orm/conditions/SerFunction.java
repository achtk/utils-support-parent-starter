package com.chua.common.support.database.orm.conditions;


import java.io.Serializable;
import java.util.function.Function;

/**
 * 支持序列化的 Function
 *
 * @author miemie
 * @since 2018-05-12
 */
@FunctionalInterface
public interface SerFunction<T, R> extends Function<T, R>, Serializable {
}
