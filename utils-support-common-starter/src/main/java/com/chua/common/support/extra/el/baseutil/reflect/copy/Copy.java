package com.chua.common.support.extra.el.baseutil.reflect.copy;

import com.chua.common.support.extra.el.baseutil.reflect.ReflectUtil;
import com.chua.common.support.extra.el.baseutil.reflect.ValueAccessor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基础类
 *
 * @author CH
 */
public class Copy<S, D> {

    private static ConcurrentHashMap<MatchKey, Copy> map = new ConcurrentHashMap<>();
    private final Entry[] entries;

    @SuppressWarnings("unchecked")
    public Copy(Class<S> source, Class<D> des) {
        Field[] desFields = getAllFields(des);
        Map<String, Field> sourceFields = generateSourceFields(source, des);
        List<Entry> list = new LinkedList<Entry>();
        for (Field toField : desFields) {
            String fromFieldName = calcuteFromFieldName(toField, source);
            if (fromFieldName == null) {
                continue;
            }
            Field fromField = sourceFields.get(fromFieldName);
            if (fromField == null) {
                continue;
            }
            Entry entry = buildEntry(fromField, toField);
            if (entry != null) {
                list.add(entry);
            }
        }
        entries = list.toArray(new Entry[0]);
    }

    public static <E> E fastCopy(Object src, E des) {
        if (src == null || des == null) {
            return des;
        }
        MatchKey matchKey = new MatchKey();
        matchKey.src = src.getClass();
        matchKey.des = des.getClass();
        Copy copy = map.get(matchKey);
        if (copy == null) {
            copy = new Copy(matchKey.src, matchKey.des);
            map.putIfAbsent(matchKey, copy);
        }
        return (E) copy.copy(src, des);
    }

    private String calcuteFromFieldName(Field toField, Class source) {
        if (Modifier.isFinal(toField.getModifiers()) || Modifier.isStatic(toField.getModifiers())) {
            return null;
        }
        if (toField.isAnnotationPresent(CopyIgnore.class)) {
            CopyIgnore copyIgnore = toField.getAnnotation(CopyIgnore.class);
            if (copyIgnore.from() == Object.class || copyIgnore.from() == source) {
                return null;
            }
        }
        if (toField.isAnnotationPresent(CopyIgnore.List.class)) {
            CopyIgnore.List copyIgnoreList = toField.getAnnotation(CopyIgnore.List.class);
            boolean ignore = false;
            for (CopyIgnore copyIgnore : copyIgnoreList.value()) {
                if (copyIgnore.from() == Object.class || copyIgnore.from() == source) {
                    ignore = true;
                    break;
                }
            }
            if (ignore) {
                return null;
            }
        }
        String fromFieldName = null;
        if (toField.isAnnotationPresent(CopyFrom.List.class)) {
            CopyFrom.List copyFroms = toField.getAnnotation(CopyFrom.List.class);
            for (CopyFrom copyFrom : copyFroms.value()) {
                if (copyFrom.from() == source) {
                    fromFieldName = copyFrom.name();
                    break;
                }
            }
        } else if (toField.isAnnotationPresent(CopyFrom.class) && toField.getAnnotation(CopyFrom.class).from() == source) {
            fromFieldName = toField.getAnnotation(CopyFrom.class).name();
        }
        if (fromFieldName == null) {
            fromFieldName = toField.getName();
        }
        return fromFieldName;
    }

    private Entry buildEntry(final Field fromProperty, final Field toProperty) {
        boolean hasTransfer = hasTransfer(fromProperty, toProperty);
        boolean isEnum = canEnumCopy(fromProperty.getType(), toProperty.getType());
        if (fromProperty.getType() != toProperty.getType() && hasTransfer == false && isEnum == false) {
            return null;
        }
        Entry entry = new Entry();
        entry.from = new ValueAccessor(fromProperty);
        entry.to = new ValueAccessor(toProperty);
        if (isEnum) {
            entry.isEnum = true;
            entry.toEnumType = (Class<? extends Enum>) toProperty.getType();
        }
        return entry;
    }

    private boolean hasTransfer(Field fromProperty, Field toProperty) {
        if (fromProperty.getType().isPrimitive()) {
            if (ReflectUtil.wrapPrimitive(fromProperty.getType()) == toProperty.getType()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean canEnumCopy(Class<?> srcType, Class<?> desType) {
        if (Enum.class.isAssignableFrom(srcType) == false || Enum.class.isAssignableFrom(desType) == false) {
            return false;
        }
        Optional<?> any = Arrays.stream(srcType.getEnumConstants()).filter(enumConstant -> {
            try {
                Enum.valueOf((Class<Enum>) desType, ((Enum<?>) enumConstant).name());
                return false;
            } catch (Exception e) {
                return true;
            }
        }).findAny();
        return any.isPresent() ? false : true;
//        for (Object enumConstant : enumConstants)
//        {
//
//        }
//        Map<String, ? extends Enum<?>> allEnumInstances = ReflectUtil.getAllEnumInstances((Class<? extends Enum<?>>) srcType);
//        boolean                        miss             = false;
//        for (Map.Entry<String, ? extends Enum<?>> entry : allEnumInstances.entrySet())
//        {
//            try
//            {
//                Enum.valueOf((Class<Enum>) desType, entry.getKey());
//            }
//            catch (Exception e)
//            {
//                miss = true;
//                break;
//            }
//        }
//        return !miss;
    }

    private Map<String, Field> generateSourceFields(Class source, Class des) {
        Field[] fields = getAllFields(source);
        Map<String, Field> map = new HashMap<>(1 << 4);
        for (Field fromField : fields) {
            if (Modifier.isFinal(fromField.getModifiers()) || Modifier.isStatic(fromField.getModifiers())) {
                continue;
            }
            if (fromField.isAnnotationPresent(CopyIgnore.class)) {
                CopyIgnore copyIgnore = fromField.getAnnotation(CopyIgnore.class);
                if (copyIgnore.to() == Object.class || copyIgnore.to() == des) {
                    continue;
                }
            }
            if (fromField.isAnnotationPresent(CopyIgnore.List.class)) {
                CopyIgnore.List copyIgnoreList = fromField.getAnnotation(CopyIgnore.List.class);
                boolean ignore = false;
                for (CopyIgnore copyIgnore : copyIgnoreList.value()) {
                    if (copyIgnore.to() == Object.class || copyIgnore.to() == source) {
                        ignore = true;
                        break;
                    }
                }
                if (ignore) {
                    continue;
                }
            }
            if (fromField.isAnnotationPresent(CopyTo.List.class)) {
                CopyTo.List copyTos = fromField.getAnnotation(CopyTo.List.class);
                for (CopyTo copyTo : copyTos.value()) {
                    if (copyTo.to() == des) {
                        map.put(copyTo.name(), fromField);
                    }
                }
            }
            if (fromField.isAnnotationPresent(CopyTo.class) && fromField.getAnnotation(CopyTo.class).to() == des) {
                map.put(fromField.getAnnotation(CopyTo.class).name(), fromField);
            }
            map.put(fromField.getName(), fromField);
        }
        return map;
    }

    public D copy(S src, D desc) {
        if (src == null || desc == null) {
            return desc;
        }
        try {
            for (Entry each : entries) {
                each.copy(src, desc);
            }
        } catch (Exception e) {
            ReflectUtil.throwException(e);
            return null;
        }
        return desc;
    }

    /**
     * 获取该类的所有field对象，如果子类重写了父类的field，则只包含子类的field
     *
     * @param entityClass
     * @return
     */
    Field[] getAllFields(Class<?> entityClass) {
        Set<Field> set = new TreeSet<Field>(new Comparator<Field>() {
            // 只需要去重，并且希望父类的field在返回数组中排在后面，所以比较全部返回1
            @Override
            public int compare(Field o1, Field o2) {
                if (o1.getName().equals(o2.getName())) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
        while (entityClass != Object.class && entityClass != null) {
            for (Field each : entityClass.getDeclaredFields()) {
                set.add(each);
            }
            entityClass = entityClass.getSuperclass();
        }
        return set.toArray(new Field[set.size()]);
    }

    static class Entry {
        ValueAccessor from;
        ValueAccessor to;
        boolean isEnum = false;
        Class<? extends Enum> toEnumType;

        void copy(Object src, Object des) {
            if (isEnum) {
                Enum enumInstance = (Enum) from.get(src);
                if (enumInstance == null) {
                    return;
                }
                @SuppressWarnings({"rawtypes", "unchecked"}) Enum desEnumInstance = Enum.valueOf((Class<Enum>) toEnumType, enumInstance.name());
                to.setObject(des, desEnumInstance);
            } else {
                Object o = from.get(src);
                if (o == null) {
                    return;
                }
                to.setObject(des, o);
            }
        }
    }

    static class MatchKey {
        Class src;
        Class des;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MatchKey matchKey = (MatchKey) o;
            if (src == matchKey.src && des == matchKey.des) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = 31 * result + (src == null ? 0 : src.hashCode());
            result = 31 * result + (des == null ? 0 : des.hashCode());
            return result;
        }
    }
}
