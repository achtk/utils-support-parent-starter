package com.chua.common.support.lang.treenode;

import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.MapUtils;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 编排树
 * 顶层PID必须为 0
 *
 * @author CH
 */
@Data
public class ArrangeTreeNode<T, R> {

    private static final String CHILDREN_FIELD = "children";
    private final Function<T, R> idFunction;
    private final Function<T, R> pidFunction;

    private final R root;

    private Consumer<List<T>> children;
    private final List<T> data = new LinkedList<>();

    private Field field;

    @SuppressWarnings("ALL")
    public ArrangeTreeNode(String id, String pid, R root) {
        this(t -> (R) BeanMap.of(t, false).get(id), t -> (R) BeanMap.of(t, false).get(pid), root);
    }

    public ArrangeTreeNode(Function<T, R> idFunction, Function<T, R> pidFunction, R root) {
        this(idFunction, pidFunction, null, root);
    }

    public ArrangeTreeNode(Function<T, R> idFunction, Function<T, R> pidFunction, Consumer<List<T>> children, R root) {
        this.idFunction = idFunction;
        this.pidFunction = pidFunction;
        this.children = children;
        this.root = root;
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

    public void start(BiConsumer<Set<String>, String> consumer) {
        //父节点下有多少子节点
        Map<String, Set<String>> idPid = new LinkedHashMap<>();
        //子节点依赖多少父节点
        Map<String, Set<String>> depends = new LinkedHashMap<>();

        Set<String> ids = new HashSet<>();

        data.forEach(it -> {
            Map<String, Object> item = BeanMap.of(it, false);
            String id = MapUtils.getString(item, "from");
            idPid.computeIfAbsent("0", it1 -> new LinkedHashSet<>()).add(id);
            String id1 = MapUtils.getString(item, "to");
            idPid.computeIfAbsent(id, it1 -> new LinkedHashSet<>()).add(id1);

            depends.computeIfAbsent(id1, it1 -> new LinkedHashSet<>()).add(id);
        });

        ids.addAll(idPid.keySet());
        ids.addAll(depends.keySet());
        ids.remove("0");

        Set<String> root = new HashSet<>(idPid.get("0"));
        Set<String> less = new LinkedHashSet<>();
        for (String string : root) {
            if(!CollectionUtils.isEmpty(depends.get(string))) {
                less.add(string);
            }
        }
        root.removeAll(less);
        for (String s : root) {
            consumer.accept(Collections.emptySet(), s);
            ids.remove(s);
        }

        for (String id : ids) {
            Set<String> strings = depends.get(id);
            consumer.accept(strings, id);
        }

    }
}
