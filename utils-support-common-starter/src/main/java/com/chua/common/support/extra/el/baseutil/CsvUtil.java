package com.chua.common.support.extra.el.baseutil;

import com.chua.common.support.constant.ConstantType;
import com.chua.common.support.extra.el.baseutil.reflect.ReflectUtil;
import com.chua.common.support.extra.el.baseutil.reflect.ValueAccessor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class CsvUtil {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface CsvHeaderName {
        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface CsvHeaderNameStrategy {
        Class<? extends HeaderName> value();
    }

    @FunctionalInterface
    public interface HeaderName {
        String name(String fieldName);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(fluent = true)
    static class CsvEntity {
        int index;
        ValueAccessor valueAccessor;
        ConstantType primitive;
    }

    public static <T> List<T> read(BufferedReader reader, Class<T> type) throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<T> constructor = type.getConstructor();
        return read(reader, type, () -> {
            try {
                return constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static <T> List<T> read(BufferedReader reader, Class<T> type, Supplier<T> supplier) throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Function<String, String> headerName;
        if (type.isAnnotationPresent(CsvHeaderNameStrategy.class)) {
            try {
                headerName = (Function<String, String>) type.getAnnotation(CsvHeaderNameStrategy.class).value().getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        } else {
            headerName = name -> name;
        }
        return read(reader, type, supplier, headerName);
    }

    public static <T> List<T> read(BufferedReader reader, Class<T> type, Supplier<T> supplier, Function<String, String> headerNameTransfer) throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<T> list = new LinkedList<>();
        String header = reader.readLine();
        if (StringUtil.isBlank(header)) {
            return list;
        }
        List<String> content = new ArrayList<>();
        getContent(header, content);
        int headerCount = content.size();
        CsvEntity[] csvEntities = defineCsvHeader(type, content, headerNameTransfer);
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            if (StringUtil.isNotBlank(line)) {
                content.clear();
                builder.append(line);
                getContent(builder.toString(), content);
                if (content.size() != headerCount) {
                    continue;
                }
                builder.setLength(0);
                T t = supplier.get();
                for (CsvEntity csvEntity : csvEntities) {
                    switch (csvEntity.primitive) {
                        case INT:
                            csvEntity.valueAccessor.setObject(t, Integer.valueOf(content.get(csvEntity.index())));
                            break;
                        case BOOLEAN:
                            csvEntity.valueAccessor.setObject(t, Boolean.valueOf(content.get(csvEntity.index())));
                            break;
                        case BYTE:
                            csvEntity.valueAccessor.setObject(t, Byte.valueOf(content.get(csvEntity.index())));
                            break;
                        case SHORT:
                            csvEntity.valueAccessor.setObject(t, Short.valueOf(content.get(csvEntity.index())));
                            break;
                        case LONG:
                            csvEntity.valueAccessor.setObject(t, Long.valueOf(content.get(csvEntity.index())));
                            break;
                        case CHAR:
                            csvEntity.valueAccessor.setObject(t, content.get(csvEntity.index()).charAt(0));
                            break;
                        case FLOAT:
                            csvEntity.valueAccessor.setObject(t, Float.valueOf(content.get(csvEntity.index())));
                            break;
                        case DOUBLE:
                            csvEntity.valueAccessor.setObject(t, Double.valueOf(content.get(csvEntity.index())));
                            break;
                        case STRING:
                            csvEntity.valueAccessor.setObject(t, content.get(csvEntity.index()));
                            break;
                        case UNKNOWN: {
                            throw new IllegalArgumentException("csv文件映射不支持字段:" + csvEntity.valueAccessor.getField().getName() + "的类型，请使用8种基本类型或包装类或String");
                        }
                        default:
                            throw new IllegalStateException("Unexpected value: " + csvEntity.primitive);
                    }
                }
                list.add(t);
            }
        }
        return list;
    }

    private static <T> CsvEntity[] defineCsvHeader(Class<T> type, List<String> content, Function<String, String> headerName) {
        List<CsvEntity> csvEntities = new ArrayList<>();
        Map<String, ValueAccessor> map = new HashMap<>();
        Arrays.stream(type.getDeclaredFields()).forEach(field -> {
            if (field.isAnnotationPresent(CsvHeaderName.class)) {
                map.put(field.getAnnotation(CsvHeaderName.class).value().equals("") ? field.getName() : field.getAnnotation(CsvHeaderName.class).value(), new ValueAccessor(field));
            } else {
                map.put(headerName.apply(field.getName()), new ValueAccessor(field));
            }
        });
        for (int i = 0; i < content.size(); i++) {
            String name = content.get(i);
            if (map.containsKey(name)) {
                ValueAccessor valueAccessor = map.get(name);
                csvEntities.add(new CsvEntity(i, valueAccessor, ReflectUtil.ofPrimitive(valueAccessor.getField().getType())));
            }
        }
        return csvEntities.toArray(new CsvEntity[0]);
    }

    private static void getContent(String line, List<String> list) {
        int end = line.length();
        int index = 0;
        //0代表正常，1代表遇到文本
        int state = 0;
        int lastContentIndex = 0;
        char c;
        while (index < end) {
            c = line.charAt(index);
            switch (c) {
                case ',': {
                    if (state == 0) {
                        String content = line.substring(lastContentIndex, index);
                        if (content.length() == 0) {
                            list.add("");
                        } else {
                            list.add(content.charAt(0) == '"' ? content.substring(1, content.length() - 1) : content);
                        }
                        lastContentIndex = index + 1;
                    } else {
                        ;
                    }
                }
                case '"': state = state == 0 ? 1 : 0;
            }
            index += 1;
        }
        String content = line.substring(lastContentIndex, index);
        if (content.length() == 0) {
            list.add("");
        } else {
            list.add(content.charAt(0) == '"' ? content.substring(1, content.length() - 1) : content);
        }
    }
}
