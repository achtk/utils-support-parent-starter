package com.chua.common.support.collection;

import java.util.List;

/**
 * 提供者
 *
 * @author Administrator
 * @param <T> 类型
 */
public interface CollectionProvider<T, R> extends List<T>, Chain<T, R> {


}
