package com.chua.common.support.wrapper;

import com.chua.common.support.file.export.ExportFileBuilder;
import com.chua.common.support.file.export.ExportType;
import com.chua.common.support.utils.CollectionUtils;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * wrapper
 *
 * @author CH
 */
public class CollectionWrapper<T> {

    private final List<T> rs;

    public CollectionWrapper(Collection<T> rs) {
        this.rs = null == rs ? new LinkedList<>() : new ArrayList<>(rs);
    }


    /**
     * 添加元素
     *
     * @param ele 元素
     * @return this
     */
    public CollectionWrapper<T> add(T ele) {
        this.rs.add(ele);
        return this;
    }

    /**
     * 添加元素
     *
     * @param index 位置
     * @param ele   元素
     * @return this
     */
    public CollectionWrapper<T> add(int index, T ele) {
        this.rs.add(index, ele);
        return this;
    }

    /**
     * 添加元素
     *
     * @param collection 集合
     * @return this
     */
    public CollectionWrapper<T> addAll(T[] collection) {
        addAll(Arrays.asList(collection));
        return this;
    }

    /**
     * 添加元素
     *
     * @param collection 集合
     * @return this
     */
    public CollectionWrapper<T> addAll(Collection<T> collection) {
        this.rs.addAll(collection);
        return this;
    }

    /**
     * 添加元素
     *
     * @param index      位置
     * @param collection 集合
     * @return this
     */
    public CollectionWrapper<T> addAll(int index, Collection<T> collection) {
        this.rs.addAll(index, collection);
        return this;
    }

    /**
     * 添加元素
     *
     * @param index 位置
     * @param ele   元素
     * @return this
     */
    public CollectionWrapper<T> set(int index, T ele) {
        this.rs.set(index, ele);
        return this;
    }

    /**
     * 获取元素位置
     *
     * @param ele 元素
     * @return 位置
     */
    public int indexOf(T ele) {
        return rs.indexOf(ele);
    }

    /**
     * 获取元素位置
     *
     * @param ele 元素
     * @return 位置
     */
    public int lastIndexOf(T ele) {
        return rs.lastIndexOf(ele);
    }

    /**
     * 数量
     *
     * @return 数量
     */
    public int size() {
        return rs.size();
    }

    /**
     * 是否为空
     *
     * @return 是否空
     */
    public boolean isEmpty() {
        return rs.isEmpty();
    }

    /**
     * 清空
     */
    public void clear() {
        rs.clear();
    }

    /**
     * 删除元素
     *
     * @param ele 元素
     * @return 结果
     */
    public boolean remove(T ele) {
        return rs.remove(ele);
    }

    /**
     * 删除索引
     *
     * @param index 索引
     * @return 元素
     */
    public T remove(int index) {
        return rs.remove(index);
    }

    /**
     * 获取子集合
     *
     * @param fromIndex 开始位置
     * @param toIndex   结束位置
     * @return 结果
     */
    public List<T> subList(int fromIndex, int toIndex) {
        return rs.subList(fromIndex, toIndex);
    }

    /**
     * 拆分集合
     * 2 -> [1,2,3,4,5] -> [[1,2],[3,4],[5]]
     *
     * @param i 数量
     * @return 结果
     */
    public List<List<T>> split(int i) {
        return CollectionUtils.averageAssign(rs, i);
    }

    /**
     * 添加元素
     *
     * @return this
     */
    public T fisrt() {
        return get(0);
    }

    /**
     * 添加元素
     *
     * @return this
     */
    public T last() {
        return get(size() - 1);
    }

    /**
     * 获取值
     *
     * @param index 所有
     * @return 结果
     */
    public T get(int index) {
        int size = size();
        if (index >= 0) {
            index = index % size;
            return rs.get(index);
        }
        index = -1 * index % size;

        if (rs instanceof RandomAccess) {
            for (int i = size; i > 0; i--) {
                if (i == index) {
                    return rs.get(i);
                }
            }
        } else {
            int i = 0;
            for (T r : rs) {
                if (i++ == index) {
                    return r;
                }
            }
        }

        return null;
    }

    /**
     * 遍历
     *
     * @param consumer 回调
     */
    public void forEach(Consumer<T> consumer) {
        rs.forEach(consumer);
    }

    /**
     * 遍历
     *
     * @param predicate 回调
     */
    public List<T> forEach(Predicate<T> predicate) {
        List<T> rs = new ArrayList<>(size());
        for (T r : this.rs) {
            if (predicate.test(r)) {
                rs.add(r);
            }
        }
        return rs;
    }

    /**
     * 输出
     *
     * @param stream     输出
     * @param exportType 类型
     */
    public void writeTo(OutputStream stream, ExportType exportType) {
        if(isEmpty()) {
            throw new RuntimeException("当前数据为空");
        }

        try (ExportFileBuilder builder = ExportFileBuilder
                .read(stream)
                .header(rs.get(0).getClass())
                .type(exportType)
                .charset(StandardCharsets.UTF_8)) {
            builder.doRead(rs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
