package com.chua.common.support.bean;


import com.chua.common.support.annotations.ExportProperty;
import com.chua.common.support.collection.ConcurrentReferenceHashMap;
import com.chua.common.support.json.Json;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.value.Value;

import java.beans.BeanInfo;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 处理器
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class BeanMap extends LinkedHashMap<String, Object> {

    private static final Map<String, BeanMap> CACHE = new ConcurrentReferenceHashMap<>(64);

    private static final BeanMap EMPTY = new BeanMap(null, false);

    private static final Map<Class<?>, Value<BeanInfo>> CACHE_BEAN = new ConcurrentReferenceHashMap<>(512);

    private BeanMap(Object source, boolean shallow) {
        if (null == source) {
            return;
        }

        if (source instanceof String) {
            source = Json.fromJson(source.toString(), HashMap.class);
        }

        if (shallow) {
            this.putAll(this.analysisSimple(source));
            return;
        }
        this.putAll(this.analysis(source));
    }

    /**
     * 初始化
     *
     * @param source 对象
     * @return map;
     */
    public static BeanMap of(Object source) {
        if (null == source) {
            return EMPTY;
        }
        return new BeanMap(source, false);
    }

    /**
     * 初始化
     *
     * @param source 对象
     * @return map;
     */
    public static BeanMap create(Object source) {
        if (null == source) {
            return EMPTY;
        }
        return new BeanMap(source, false);
    }

    /**
     * 浅度赋值
     *
     * @param source  元数据
     * @param shallow 是否浅度解析数据
     * @return beanMap
     */
    public static BeanMap of(Object source, boolean shallow) {
        return new BeanMap(source, shallow);
    }

    /**
     * 分析参数
     *
     * @param source 数据源
     */
    @SuppressWarnings("all")
    private Collection analysis(Collection source) {
        Collection rs = null;
        if (source instanceof List) {
            rs = new ArrayList<>(source.size());
        } else {
            rs = new HashSet(source.size());
        }

        Collection finalRs = rs;
        source.forEach(item -> {
            if (item instanceof Collection) {
                finalRs.add(analysis((Collection) item));
                return;
            }
            finalRs.add(analysis(item));
        });

        return rs;

    }

    /**
     * 分析参数
     *
     * @param source 数据源
     */
    @SuppressWarnings("all")
    private Map analysisSimple(Object source) {
        Map rs = new HashMap();
        if (null == source) {
            return rs;
        }

        if (source instanceof Map) {
            rs.putAll(openMap((Map<? extends String, ?>) source));
            return rs;
        }

        Class<?> aClass = source.getClass();
        ClassUtils.doWithFields(aClass, field -> {
            if(Modifier.isStatic(field.getModifiers())) {
                return;
            }
            field.setAccessible(true);
            Object o = null;
            try {
                o = field.get(source);
            } catch (IllegalAccessException ignore) {
            }

            if (null == o) {
                return;
            }
            rs.put(field.getName(), o);
        });

        return rs;
    }

    /**
     * 分析参数
     *
     * @param source 数据源
     */
    @SuppressWarnings("all")
    private Map analysis(Object source) {
        Map rs = new HashMap();
        if (source instanceof Map) {
            rs.putAll(openMap((Map<? extends String, ?>) source));
            Class<? extends Map> aClass = ((Map<?, ?>) source).getClass();
            CustomMap customMap = aClass.getDeclaredAnnotation(CustomMap.class);
            if (null == customMap) {
                return rs;
            }
        }

        Map<String, Object> beanMap = asMap(source);
        rs.putAll(beanMap);
        beanMap.forEach((k, v) -> {
            if (null == v) {
                return;
            }

            if (v instanceof Collection) {
                rs.put(k, analysis((Collection) v));
                return;
            }

            if (v.getClass().getTypeName().contains("java")) {
                return;
            }

            rs.put(k, analysis(v));
        });


        return rs;
    }

    private Map<String, Object> asMap(Object source) {
        Map<String, Object> rs = new LinkedHashMap<>();
        Class<?> aClass = source.getClass();
        ClassUtils.doWithFields(aClass, field -> {
            if(Modifier.isStatic(field.getModifiers())) {
                return;
            }
            Object fieldValue = ClassUtils.getFieldValue(field, aClass, source);
            ExportProperty exportProperty = field.getDeclaredAnnotation(ExportProperty.class);
            if(null != exportProperty) {
                String[] split = exportProperty.value().split(",");
                for (String s : split) {
                    rs.put(s, fieldValue);
                }
            }
            rs.put(field.getName(), fieldValue);
        });
        return rs;
    }

    @SuppressWarnings("all")
    private Map openMap(Map source) {
        Map rs = new HashMap();
        rs.putAll(source);
        rs.putAll(new OpenMap().apply(source));
        return rs;
    }


    /**
     * 打开map
     *
     * @see com.chua.common.support.function.LevelsOpen
     */
    class OpenMap {
        private static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;

        /**
         * 展开
         *
         * @param stringObjectMap 集合
         * @return 展开后的集合
         */
        public Map<String, Object> apply(Map<String, Object> stringObjectMap) {
            if (null == stringObjectMap) {
                return null;
            }

            if (stringObjectMap.isEmpty()) {
                return Collections.emptyMap();
            }

            List<Map<String, Object>> result = new ArrayList<>();
            Set<String> keySet = stringObjectMap.keySet();
            List<String> list = new ArrayList<>(keySet.stream().filter(Objects::nonNull).collect(Collectors.toList()));
            Collections.sort(list);
            for (String key : list) {
                Map<String, Object> map = new HashMap<>(DEFAULT_INITIAL_CAPACITY);
                String tempKey = key;
                int index = tempKey.indexOf(".");
                if (index > -1) {
                    tempKey = tempKey.substring(0, index);
                }
                int newIndex = tempKey.indexOf('[');
                int newEndIndex = tempKey.indexOf(']');
                Object value = stringObjectMap.get(key);

                if (index == -1) {
                    if (newIndex == -1) {
                        map.put(key, value);
                    } else {
                        map.put(key.substring(0, newIndex), levelOpenList(key.substring(newEndIndex + 2), value));
                    }
                }
                if (newIndex == -1) {
                    String newKey = key;
                    String splitKey = "";
                    if (index != -1) {
                        newKey = key.substring(0, index);
                        splitKey = key.substring(index + 1);
                    }
                    if (!StringUtils.isNullOrEmpty(splitKey)) {
                        map.put(newKey, levelOpenMap(splitKey, value));
                    } else {
                        map.put(newKey, value);
                    }
                } else {
                    map.put(key.substring(0, newIndex), levelOpenListMap(key.substring(newEndIndex + 2), value));
                }
                result.add(map);
            }
            return this.merge(result);
        }

        /**
         * 合并
         *
         * @param toArray map集合
         * @return 单集合
         */
        private Map<String, Object> merge(List<Map<String, Object>> toArray) {
            Map<String, Object> result = new HashMap<>(DEFAULT_INITIAL_CAPACITY);
            for (Map<String, Object> objectMap : toArray) {
                merge(result, objectMap);
            }
            return result;
        }

        /**
         * 合并集合
         *
         * @param mapLeft  左侧集合
         * @param mapRight 右侧集合
         */
        public void merge(Map<String, Object> mapLeft, Map<String, Object> mapRight) {
            for (Map.Entry<String, Object> entry : mapRight.entrySet()) {
                if (mapLeft.containsKey(entry.getKey())) {
                    Object o = mapLeft.get(entry.getKey());
                    Object o1 = entry.getValue();
                    if (isAllMap(o, o1)) {
                        Map<String, Object> asMap = BeanMap.of(o);
                        merge(asMap, BeanMap.of(o1));
                        ((Map) o).putAll(asMap);
                    } else if (o instanceof List) {
                        mergeList(o, o1);
                    }
                } else {
                    mapLeft.put(entry.getKey(), entry.getValue());
                }
            }
        }

        /**
         * 合并集合
         *
         * @param leftList  左侧数据
         * @param rightList 右侧数据
         */
        private void mergeList(Object leftList, Object rightList) {
            List<Object> temp = ifList(leftList);
            if (temp.isEmpty()) {
                if (isList(rightList)) {
                    temp.addAll(ifList(rightList));
                } else {
                    temp.add(rightList);
                }
                return;
            }

            if (!isList(rightList)) {
                temp.add(rightList);
                return;
            }
            List<Object> list = ifList(rightList);
            intoTemp(temp.size() - 1, temp, list);
        }

        /**
         * 是否是集合
         *
         * @param source 数据
         * @return 集合返回true
         */
        private boolean isList(Object source) {
            return source instanceof List;
        }

        /**
         * 两个都是Map
         *
         * @param left  左侧数据
         * @param right 右侧数据
         * @return 都是Map返回true
         */
        private boolean isAllMap(Object left, Object right) {
            return left instanceof Map && right instanceof Map;
        }

        /**
         * 对象转集合
         *
         * @param value 对象
         * @return 集合
         */
        private List<Object> ifList(Object value) {
            return value instanceof List ? (List<Object>) value : Collections.emptyList();
        }

        /**
         * 合并数据
         *
         * @param offset 索引
         * @param temp   左侧源数据
         * @param list   右侧数据
         */
        private void intoTemp(int offset, List<Object> temp, List<Object> list) {
            Object o3 = list.get(0);
            Object o2 = temp.get(offset);
            if (isAllMap(o2, o3)) {
                Map<String, Object> o2Temp = BeanMap.of(o2);
                Map<String, Object> o3Temp = BeanMap.of(o3);
                boolean isAll = allIn(o2Temp, o3Temp);
                if (!isAll) {
                    o2Temp.putAll(o3Temp);
                    temp.remove(offset);
                    temp.add(o2Temp);
                } else {
                    if (offset > 0 && (offset - 1) < 0) {
                        intoTemp(--offset, temp, list);
                        return;
                    }
                    temp.add(o3Temp);
                }
            } else {
                temp.addAll(list);
            }
        }

        /**
         * 是否元素全部包含
         *
         * @param leftMap  左侧集合
         * @param rightMap 右侧集合
         * @return 全部包含返回true
         */
        private boolean allIn(Map<String, Object> leftMap, Map<String, Object> rightMap) {
            boolean isAll = true;
            for (String s : rightMap.keySet()) {
                if (!leftMap.containsKey(s)) {
                    isAll = false;
                    break;
                }
            }
            return isAll;
        }

        /**
         * map
         *
         * @param key   key
         * @param value value
         * @return map
         */
        private Map<String, Object> levelOpenMap(String key, Object value) {
            String tempKey = key;
            int index = tempKey.indexOf(".");
            if (index > -1) {
                tempKey = tempKey.substring(0, index);
            }
            int newIndex = tempKey.indexOf('[');
            Map<String, Object> item = new HashMap<>(DEFAULT_INITIAL_CAPACITY);

            if (index == -1) {
                if (newIndex == -1) {
                    item.put(key, value);
                } else {
                    item.put(key.substring(0, newIndex), levelOpenList(key.substring(newIndex), value));
                }
            } else {
                if (newIndex == -1) {
                    item.put(key.substring(0, index), levelOpenMap(key.substring(index + 1), value));
                } else {
                    item.put(key.substring(0, newIndex), levelOpenListMap(key.substring(index + 1), value));
                }
            }
            return item;
        }

        private List<Map<String, Object>> levelOpenListMap(String key, Object value) {
            String tempKey = key;
            int index = tempKey.indexOf(".");
            if (index > -1) {
                tempKey = tempKey.substring(0, index);
            }
            int newIndex = tempKey.indexOf('[');
            List<Map<String, Object>> result = new ArrayList<>();
            Map<String, Object> item = new HashMap<>(DEFAULT_INITIAL_CAPACITY);

            if (index == -1) {
                if (newIndex == -1) {
                    item.put(key, value);
                } else {
                    item.put(key.substring(0, newIndex), levelOpenList(key.substring(newIndex + 1), value));
                }
            } else {
                if (newIndex == -1) {
                    item.put(key.substring(0, index), levelOpenMap(key.substring(index + 1), value));
                } else {
                    item.put(key.substring(0, newIndex), levelOpenListMap(key.substring(index + 1), value));
                }
            }
            result.add(item);
            return result;
        }

        private List<Object> levelOpenList(String substring, Object o) {
            if (o instanceof Collection) {
                return new ArrayList<Object>((Collection<?>) o);
            }
            return new ArrayList<>(Collections.singletonList(o));
        }
    }
}
