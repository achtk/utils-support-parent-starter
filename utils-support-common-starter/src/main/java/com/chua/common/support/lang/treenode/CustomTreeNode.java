package com.chua.common.support.lang.treenode;

import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.CollectionUtils;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 树
 * 顶层PID必须为 0
 *
 * @author CH
 */
@Data
public class CustomTreeNode<T, R> {

    private static final String CHILDREN_FIELD = "children";
    private final Function<T, R> idFunction;
    private final Function<T, R> pidFunction;

    private Consumer<List<T>> children;
    private final List<T> data = new LinkedList<>();

    private Field field;

    public CustomTreeNode(Function<T, R> idFunction, Function<T, R> pidFunction) {
        this(idFunction, pidFunction, null);
    }

    public CustomTreeNode(Function<T, R> idFunction, Function<T, R> pidFunction, Consumer<List<T>> children) {
        this.idFunction = idFunction;
        this.pidFunction = pidFunction;
        this.children = children;
    }

    /**
     * 添加数据
     * @param constructs 数据
     */
    public void add(List<T> constructs) {
        data.addAll(constructs);
    }

    /**
     * 添加数据
     * @param constructs 数据
     */
    public void add(T... constructs) {
        data.addAll(Arrays.asList(constructs));
    }

    /**
     * 转化
     * @return T
     */
    public T transfer() {
        return CollectionUtils.findFirst(transferAll());
    }
    /**
     * 转化
     * @return T
     */
    public List<T> transferAll() {
        Map<R, List<T>> tpl = new LinkedHashMap<>();
        for (T datum : data) {
            tpl.computeIfAbsent(pidFunction.apply(datum), it -> new LinkedList<>()).add(datum);
        }

        for (T datum : data) {
            List<T> ts = tpl.get(idFunction.apply(datum));
            if(null == ts) {
                continue;
            }
            doChildren(datum, ts);
        }

        return data.stream().filter(it -> pidFunction.apply(it).equals(0)).collect(Collectors.toList());
    }

    private void doChildren(T datum, List<T> ts) {
        if(null != children) {
            children.accept(ts);
            return;
        }


        if(null == field) {
            field = ClassUtils.findField(datum.getClass(), CHILDREN_FIELD);
        }

        if(null == field) {
            return;
        }

        ClassUtils.setAccessible(field);
        ClassUtils.setFieldValue(field, ts, datum);
    }

}
