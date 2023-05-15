package com.chua.common.support.bean;

import com.chua.common.support.context.environment.Environment;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.value.Value;
import com.google.common.base.Strings;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 属性绑定器
 *
 * @author CH
 */
public class BeanBinder {

    private final ProfileHandler[] handler;

    private BeanBinder(ProfileHandler[] handler) {
        this.handler = handler;
    }

    private BeanBinder(ProfileHandler handler) {
        this.handler = new ProfileHandler[]{handler};
    }

    /**
     * 初始化
     *
     * @param handler 处理器
     * @return 结果
     */
    public static BeanBinder of(ProfileHandler... handler) {
        return new BeanBinder(handler);
    }

    /**
     * 初始化
     *
     * @param environment 环境
     * @return 结果
     */
    public static BeanBinder of(Environment environment) {
        return new BeanBinder(new ProfileHandler() {
            @Override
            public Object getProperty(String name) {

                return environment.getProperty(name);
            }


        });
    }

    /**
     * 绑定属性
     *
     * @param target 目标类型
     * @param <T>    类型
     * @return 结果
     */
    public <T> Value<T> bind(Class<T> target) {
        BeanProperty beanProperty = target.getDeclaredAnnotation(BeanProperty.class);
        return bind(null == beanProperty ? "" : beanProperty.value(), target);
    }

    /**
     * 绑定属性
     *
     * @param pre    前缀
     * @param target 目标类型
     * @param <T>    类型
     * @return 结果
     */
    public <T> Value<T> bind(String pre, Class<T> target) {
        BeanProperty beanProperty = target.getDeclaredAnnotation(BeanProperty.class);
        T forObject = ClassUtils.forObject(target);
        if (null == forObject || null == handler) {
            return Value.of(forObject);
        }

        bind(pre, target, forObject);

        return Value.of(forObject);
    }

    /**
     * 绑定属性
     *
     * @param pre       前缀
     * @param target    目标类型
     * @param forObject 对象
     * @param <T>       类型
     */
    private <T> void bind(String pre, Class<T> target, T forObject) {
        ClassUtils.doWithFields(target, field -> {
            String name = getName(field);
            String key = Strings.isNullOrEmpty(pre) ? name : StringUtils.endWithAppend(pre, ".").concat(name);
            Class<?> type = field.getType();
            Object value = getValue(key, type);
            if (null == value) {
                return;
            }

            ClassUtils.setAllFieldValue(field, value, target, forObject);
        });
    }

    /**
     * 获取结果
     *
     * @param key  key
     * @param type 类型
     * @return 结果
     */
    private Object getValue(String key, Class<?> type) {
        Object property = null;
        try {
            property = getHandlerValue(key);
        } catch (Exception ignored) {
        }

        if (ClassUtils.isJavaType(type)) {
            return property;
        }

        if (null == property && hasChildren(key)) {
            property = getChildren(key);
        }

        if (property instanceof Map) {
            BeanBinder beanBinder = new BeanBinder(new ProfileMapHandler((Map<? extends Object, ? extends Object>) property));
            return beanBinder.bind(key, type).getValue();
        }

        if (property instanceof Collection) {

        }
        return bind(key, type);
    }

    /**
     * 是否存在下级节点
     *
     * @param key key
     * @return 是否存在下级节点
     */
    private boolean hasChildren(String key) {
        String newKey = key + ".*";
        for (ProfileHandler profileHandler : handler) {
            if (profileHandler.isMatcher(newKey)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否存在下级节点
     *
     * @param key key
     * @return 是否存在下级节点
     */
    private Map<String, Object> getChildren(String key) {
        Map<String, Object> rs = new LinkedHashMap<>();
        String newKey = key + ".*";
        for (ProfileHandler profileHandler : handler) {
            if (profileHandler.isMatcher(newKey)) {
                rs.putAll(profileHandler.getChildren(newKey));
            }
        }
        return rs;
    }


    /**
     * 獲取數據
     *
     * @param key key
     * @return 结果
     */
    private Object getHandlerValue(String key) {
        for (ProfileHandler profileHandler : handler) {
            Object property = profileHandler.getProperty(key);
            if (null != property) {
                return property;
            }
        }
        return null;
    }

    /**
     * 获取属性名称
     *
     * @param field 属性名称
     * @return 结果
     */
    private String getName(Field field) {
        BeanProperty beanProperty = field.getDeclaredAnnotation(BeanProperty.class);
        return null == beanProperty ? Converter.toCamelHyphen(field.getName()) : beanProperty.value();
    }
}
