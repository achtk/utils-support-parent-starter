package com.chua.common.support.extra.quickio.core;


import com.chua.common.support.extra.quickio.api.FindOptions;
import com.chua.common.support.extra.quickio.exception.QuException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 * 配置
 * @author CH
 */
final class QuFindOptions implements FindOptions {

    private String sortFieldName;
    private long sortValue;
    private long skipSize;
    private long limitSize;

    String indexName;
    Object indexValue;


    @Override
    public FindOptions sort(String fieldName, int value) {
        if (fieldName == null || fieldName.isEmpty()) {
            throw new QuException(Constants.SORTING_FIELD_NAME_ILLEGAL);
        }
        if (value < -1 || value > 1) {
            throw new QuException(Constants.SORTING_PARAMETER_VALUE_ILLEGAL);
        }
        sortFieldName = fieldName;
        sortValue = value;
        return this;
    }


    @Override
    public FindOptions skip(long size) {
        skipSize = size;
        return this;
    }


    @Override
    public FindOptions limit(long size) {
        limitSize = size;
        return this;
    }


    @Override
    public void index(String fieldName, Object fieldValue) {
        indexName = Optional.ofNullable(fieldName).orElseThrow(NullPointerException::new);
        indexValue = Optional.ofNullable(fieldValue).orElseThrow(NullPointerException::new);
    }


    <T extends IoEntity> List<T> get(List<T> list) {
        Stream<T> stream = (list == null || list.isEmpty()) ? null : list.stream();
        if (stream == null) {
            return list;
        }
        if (sortValue != 0) {
            Comparator<T> comparator = createComparator(list.get(0));
            comparator = (sortValue == 1) ? comparator : comparator.reversed();
            stream = stream.parallel().sorted(comparator);
            stream = stream.sequential();
        }
        if (skipSize > 0) {
            stream = stream.skip(skipSize);
        }
        if (limitSize > 0) {
            stream = stream.limit(limitSize);
        }
        return stream.collect(Collectors.toList());
    }


    private <K extends IoEntity, T extends IoEntity> Comparator<K> createComparator(T object) {
        ReflectObject<T> reflectObject = new ReflectObject<>(object);
        if (!reflectObject.contains(sortFieldName)) {
            throw new QuException(Constants.FIELD_DOES_NOT_EXIST);
        }
        switch (reflectObject.getType(sortFieldName).getSimpleName().toLowerCase()) {
            case "byte":
            case "short":
            case "int":
            case "integer":
                return Comparator.comparingInt(t -> (int) new ReflectObject<>(t).getValue(sortFieldName));
            case "long":
                return Comparator.comparingLong(t -> (long) new ReflectObject<>(t).getValue(sortFieldName));
            case "float":
            case "double":
                return Comparator.comparingDouble(t -> (double) new ReflectObject<>(t).getValue(sortFieldName));
            default:
                throw new QuException(Constants.FIELD_DOES_NOT_SUPPORT_SORTING);
        }
    }

}