package com.chua.common.support.collection;

import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.constant.CommonConstant;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.StringUtils;

import java.util.*;

import static com.chua.common.support.constant.NumberConstant.DEFAULT_INITIAL_CAPACITY;

/**
 * 层级展开
 *
 * @author CH
 * @version 1.0.0
 */
public class LevelsOpen implements Levels {

    @Override
    public Map<String, Object> apply(Map<String, Object> stringObjectMap) {
        if (MapUtils.isEmpty(stringObjectMap)) {
            return null;
        }
        List<Map<String, Object>> result = new ArrayList<>();
        Set<String> keySet = stringObjectMap.keySet();
        List<String> list = new ArrayList<>(keySet);
        Collections.sort(list);
        for (String key : list) {
            Map<String, Object> map = new HashMap<>(DEFAULT_INITIAL_CAPACITY);
            String tempKey = key;
            int index = tempKey.indexOf(".");
            if (index > -1) {
                tempKey = tempKey.substring(0, index);
            }
            int newIndex = tempKey.indexOf(CommonConstant.SYMBOL_LEFT_SQUARE_BRACKET);
            int newEndIndex = tempKey.indexOf(CommonConstant.SYMBOL_RIGHT_SQUARE_BRACKET);
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
                    Map<String, Object> asMap = BeanMap.create(o);
                    merge(asMap, BeanMap.create(o1));
                    ((Map) o).putAll(asMap);
                } else if (CollectionUtils.isList(o)) {
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
        List<Object> temp = CollectionUtils.ifList(leftList);
        if (temp.isEmpty()) {
            if (CollectionUtils.isList(rightList)) {
                temp.addAll(CollectionUtils.ifList(rightList));
            } else {
                temp.add(rightList);
            }
            return;
        }

        if (!CollectionUtils.isList(rightList)) {
            temp.add(rightList);
            return;
        }
        List<Object> list = CollectionUtils.ifList(rightList);
        intoTemp(temp.size() - 1, temp, list);
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
            Map<String, Object> o2Temp = BeanMap.create(o2);
            Map<String, Object> o3Temp = BeanMap.create(o3);
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
        int newIndex = tempKey.indexOf(CommonConstant.SYMBOL_LEFT_SQUARE_BRACKET);
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
        int newIndex = tempKey.indexOf(CommonConstant.SYMBOL_LEFT_SQUARE_BRACKET);
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
