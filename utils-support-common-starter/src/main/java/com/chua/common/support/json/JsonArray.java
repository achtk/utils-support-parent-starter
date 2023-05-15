package com.chua.common.support.json;

import com.alibaba.fastjson2.JSONArray;
import com.chua.common.support.converter.Converter;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.*;

/**
 * json array
 *
 * @author CH
 */
public class JsonArray implements List {

    private static final JsonArray EMPTY = new JsonArray(new JSONArray());
    private List list;

    public JsonArray() {
        this(new LinkedList());
    }

    public JsonArray(Collection jsonArray) {
        this.list = new LinkedList(jsonArray);
    }

    /**
     * 初始化
     *
     * @return this
     */
    public static JsonArray empty() {
        return EMPTY;
    }


    /**
     * 获取数据
     *
     * @param index 索引
     * @return 数据
     */
    public JsonArray getJsonArray(int index) {
        return new JsonArray((List) list.get(index));
    }

    /**
     * 获取数据
     *
     * @param index 索引
     * @return 数据
     */
    public JsonObject getJsonObject(int index) {
        return new JsonObject((Map) list.get(index));
    }

    /**
     * 获取数据
     *
     * @param index 索引
     * @return 数据
     */
    public String getString(int index) {
        return Converter.convertIfNecessary(list.get(index), String.class);
    }

    /**
     * 获取数据
     *
     * @param index 索引
     * @return 数据
     */
    public Double getDouble(int index) {
        return Converter.convertIfNecessary(list.get(index), Double.class);
    }

    /**
     * 获取数据
     *
     * @param index 索引
     * @return 数据
     */
    public double getDoubleValue(int index) {
        return Converter.convertIfNecessary(list.get(index), double.class);
    }

    /**
     * 获取数据
     *
     * @param index 索引
     * @return 数据
     */
    public Float getFloat(int index) {
        return Converter.convertIfNecessary(list.get(index), Float.class);
    }

    /**
     * 获取数据
     *
     * @param index 索引
     * @return 数据
     */
    public float getFloatValue(int index) {
        return Converter.convertIfNecessary(list.get(index), float.class);
    }

    /**
     * 获取数据
     *
     * @param index 索引
     * @return 数据
     */
    public Long getLong(int index) {
        return Converter.convertIfNecessary(list.get(index), Long.class);
    }

    /**
     * 获取数据
     *
     * @param index 索引
     * @return 数据
     */
    public long getLongValue(int index) {
        return Converter.convertIfNecessary(list.get(index), long.class);
    }

    /**
     * 获取数据
     *
     * @param index 索引
     * @return 数据
     */
    public Integer getInteger(int index) {
        return Converter.convertIfNecessary(list.get(index), Integer.class);
    }

    /**
     * 获取数据
     *
     * @param index 索引
     * @return 数据
     */
    public int getIntValue(int index) {
        return Converter.convertIfNecessary(list.get(index), int.class);
    }

    /**
     * 获取数据
     *
     * @param index 索引
     * @return 数据
     */
    public Short getShort(int index) {
        return Converter.convertIfNecessary(list.get(index), Short.class);
    }

    /**
     * 获取数据
     *
     * @param index 索引
     * @return 数据
     */
    public short getShortValue(int index) {
        return Converter.convertIfNecessary(list.get(index), short.class);
    }

    /**
     * 获取数据
     *
     * @param index 索引
     * @return 数据
     */
    public Byte getByte(int index) {
        return Converter.convertIfNecessary(list.get(index), Byte.class);
    }

    /**
     * 获取数据
     *
     * @param index 索引
     * @return 数据
     */
    public byte getByteValue(int index) {
        return Converter.convertIfNecessary(list.get(index), byte.class);
    }

    /**
     * 获取数据
     *
     * @param index 索引
     * @return 数据
     */
    public Boolean getBoolean(int index) {
        return Converter.convertIfNecessary(list.get(index), Boolean.class);
    }

    /**
     * 获取数据
     *
     * @param index 索引
     * @return 数据
     */
    public boolean getBooleanValue(int index) {
        return Converter.convertIfNecessary(list.get(index), boolean.class);
    }

    /**
     * 获取数据
     *
     * @param index 索引
     * @return 数据
     */
    public BigInteger getBigInteger(int index) {
        return Converter.convertIfNecessary(list.get(index), BigInteger.class);
    }

    /**
     * 获取数据
     *
     * @param index 索引
     * @return 数据
     */
    public BigDecimal getBigDecimal(int index) {
        return Converter.convertIfNecessary(list.get(index), BigDecimal.class);
    }

    /**
     * 获取数据
     *
     * @param index 索引
     * @return 数据
     */
    public Date getDate(int index) {
        return Converter.convertIfNecessary(list.get(index), Date.class);
    }

    /**
     * 获取数据
     *
     * @param index 索引
     * @return 数据
     */
    public Instant getInstant(int index) {
        return Converter.convertIfNecessary(list.get(index), Instant.class);
    }


    /**
     * 获取数据
     *
     * @param index 索引
     * @return 数据
     */
    public <T> T getObject(int index, Type type) {
        return (T) Converter.convertIfNecessary(list.get(index), type);
    }

    /**
     * 获取数据
     *
     * @param index 索引
     * @return 数据
     */
    public <T> T getObject(int index, Class<T> type) {
        return Converter.convertIfNecessary(list.get(index), type);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public boolean add(Object o) {
        return list.add(o);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean addAll(Collection c) {
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection c) {
        return list.addAll(index, c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public Object get(int index) {
        return list.get(index);
    }

    @Override
    public Object set(int index, Object element) {
        return list.set(index, element);
    }

    @Override
    public void add(int index, Object element) {
        list.add(index, element);
    }

    @Override
    public Object remove(int index) {
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public boolean retainAll(Collection c) {
        return list.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection c) {
        return list.removeAll(c);
    }

    @Override
    public boolean containsAll(Collection c) {
        return list.containsAll(c);
    }

    @Override
    public Object[] toArray(Object[] a) {
        return list.toArray(a);
    }
}
