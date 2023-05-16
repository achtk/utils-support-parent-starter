package com.chua.common.support.context.resolver.factory;

import com.chua.common.support.context.resolver.NamePair;
import com.chua.common.support.context.resolver.OrderResolver;
import com.chua.common.support.function.SafeConsumer;
import com.chua.common.support.utils.AnnotationUtils;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.MapUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 优先级解析器
 * @author CH
 */
public class SimpleOrderResolver implements OrderResolver {
    @Override
    public int resolve(NamePair namePair) {
        List<Integer> rs = new LinkedList<>();
        Map<String, Object> stringObjectMap = AnnotationUtils.asMap(namePair.getAnnotation());
        MapUtils.filterNone(stringObjectMap, (SafeConsumer<Object>) o -> rs.add(Integer.valueOf(o.toString())), "value", "order");
        Integer first = CollectionUtils.findFirst(rs);
        return null == first ? 0 : first;
    }
}
