package com.chua.common.support.value;

import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.metadata.Metadata;
import com.chua.common.support.function.strategy.resolver.NamePair;
import com.chua.common.support.function.strategy.resolver.NamedResolver;
import com.chua.common.support.function.strategy.resolver.SimpleNamedResolver;
import com.chua.common.support.utils.StringUtils;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 数据映射
 *
 * @author CH
 */
@Builder
@Data
public class DataMapping {

    @Singular("addMapping")
    private Map<String, Pair> mapping;

    @Builder.Default
    private NamedResolver resolver = new SimpleNamedResolver();

    /**
     * 是否为空
     *
     * @return 是否为空
     */
    public boolean isEmpty() {
        return mapping.isEmpty();
    }
    /**
     * value
     *
     * @return value
     */
    public Pair[] getValuePair() {
        return mapping.values().toArray(new Pair[0]);
    }
    /**
     * value
     *
     * @return value
     */
    public String[] getValueMapping() {
        List<String> tpl = new LinkedList<>();
        for (Map.Entry<String, Pair> entry : mapping.entrySet()) {
            Pair value = entry.getValue();
            if(StringUtils.isNullOrEmpty(value.getLabel())) {
                tpl.add(value.getName());
            } else {
                tpl.add(value.getLabel());
            }
        }
        return tpl.toArray(new String[0]);
    }

    /**
     * key
     *
     * @return key
     */
    public String[] getKeyMapping() {
        List<String> tpl = new LinkedList<>();
        for (Map.Entry<String, Pair> entry : mapping.entrySet()) {
            Pair value = entry.getValue();
            String name = StringUtils.isNullOrEmpty(value.getName()) ?  value.getLabel() : value.getName();
            tpl.add(entry.getKey() + " " + name);
        }
        return tpl.toArray(new String[0]);
    }
    /**
     * 解析映射
     *
     * @param metadata 待解析
     */
    public void analysis(Metadata<?> metadata) {
        List<Column> column = metadata.getColumn();
        for (Column column1 : column) {
            this.mapping.put(column1.getName(), new Pair(column1.getName()));
        }
    }

    /**
     * 解析映射
     *
     * @param metadata 待解析
     */
    public void analysisComment(Metadata<?> metadata) {
        List<Column> column = metadata.getColumn();
        for (Column column1 : column) {
            this.mapping.put(column1.getName(), new Pair(column1.getComment()));
        }
    }
    /**
     * 解析映射
     *
     * @param namePair 待解析的注解
     * @param type     类型
     */
    public void analysis(Class<?> type, NamePair... namePair) {
        for (NamePair pair : namePair) {
            Annotation annotation = type.getDeclaredAnnotation(pair.getAnnotationType());
            if(null == annotation) {
                continue;
            }

            String[] name = resolver.resolve(pair.setType(type));
            for (String s : name) {
                this.mapping.put(s, new Pair(s));
            }
        }
    }
    /**
     * 转化
     * @param bean 对象
     * @return map
     */
    public Object[] transferFromValue(Object bean) {
        List<Object> rs = new LinkedList<>();
        BeanMap beanMap1 = BeanMap.create(bean);
        for (Map.Entry<String, Pair> entry : mapping.entrySet()) {
            String key = entry.getKey();
            Object o = beanMap1.get(key);
            Pair pair = entry.getValue();
            if(o == null) {
                o = beanMap1.get(pair.getName());

            }
            if(o == null) {
                o = beanMap1.get(pair.getLabel());
            }
            rs.add(pair.resolve(o));
        }

        return rs.toArray();
    }
    /**
     * 转化
     * @param bean 对象
     * @return map
     */
    public Map<String, Object> transferFrom(Object bean) {
        Map<String, Object> rs = new LinkedHashMap<>();
        BeanMap beanMap1 = BeanMap.create(bean);
        boolean isEmpty = true;
        for (Map.Entry<String, Pair> entry : mapping.entrySet()) {
            String key = entry.getKey();
            Pair pair = entry.getValue();
            Object o = beanMap1.get(key);
            rs.put(pair.getName(), pair.resolve(o));
            if(null != o) {
                isEmpty = false;
            }
        }

        if(isEmpty) {
            return null;
        }
        return rs;
    }

    /**
     * 获取映射
     * @param value  索引
     * @return 映射
     */
    public Pair getPair(String value) {
        Pair pair = mapping.get(value);
//        if(null != pair) {
//            return pair;
//        }
//
//        for (Pair item : mapping.values()) {
//            if(item.getName().equals(value) || item.getLabel().equals(value)) {
//                return item;
//            }
//        }

        return pair;
    }
}
