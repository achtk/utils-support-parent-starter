package com.chua.datasource.support.rule;

import org.apache.calcite.plan.Convention;
import org.apache.calcite.rel.RelNode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Administrator
 */
public class RelConvention {

    static final Map<String, Convention> CONVENTION_MAP = new ConcurrentHashMap<>();

    /**
     * 實例化
     *
     * @param name name
     * @param type rel
     * @return Convention
     */
    public static Convention createConvention(String name, Class<? extends RelNode> type) {
        return CONVENTION_MAP.computeIfAbsent(name, it -> new Convention.Impl(name, type));
    }

}
