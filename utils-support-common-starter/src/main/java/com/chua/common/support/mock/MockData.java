package com.chua.common.support.mock;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.lang.date.DateTime;
import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.mock.resolver.MockResolver;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.unit.name.NamingCase;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.RandomUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.chua.common.support.constant.CommonConstant.METHOD_SETTER;

/**
 * mock
 *
 * @author CH
 */

public class MockData<T> {


    private static final MockData INSTANCE = new MockData();
    private MockDataConfiguration configuration;

    private MockData(MockDataConfiguration configuration) {
        this.configuration = configuration;
    }

    private MockData() {
    }

    /**
     * 初始化实体
     *
     * @param target 类型
     * @param <T>    类型
     * @return 实体
     */
    public static <T> T createBean(Class<T> target) {
        return (T) INSTANCE.create(target);
    }

    /**
     * 获取数据
     *
     * @param target 类型
     * @param <T>    类型
     * @return 实体
     */
    public static <T> List<T> createListBean(Class<T> target) {
        int anInt = RandomUtils.randomInt(0, 1000);
        return IntStream.range(0, anInt).mapToObj(it -> createBean(target)).collect(Collectors.toList());
    }

    /**
     * 类型
     *
     * @param target 类型
     * @return 对象
     */
    public T create(Class<T> target) {
        T forObject = ClassUtils.forObject(target);
        ExpressionParser expressionParser = ExpressionParser.create();
        expressionParser.setVariable(target.getSimpleName(), forObject);
        ClassUtils.doWithFields(target, field -> {
            if (Modifier.isStatic(field.getModifiers())) {
                return;
            }

            Mock mock = field.getDeclaredAnnotation(Mock.class);

            StringBuilder rs = new StringBuilder();
            if (null == mock) {
                analysis(rs, field);
            } else {
                MockValue mockValue = new MockValue(mock, field);
                Mock.Type[] value = mock.value();
                for (Mock.Type type : value) {
                    MockResolver mockResolver = ServiceProvider.of(MockResolver.class).getExtension(type);
                    try {
                        rs.append(mockResolver.resolve(mockValue, expressionParser));
                    } catch (Exception ignored) {
                    }
                }
            }

            String method = METHOD_SETTER.concat(NamingCase.toFirstUpperCase(field.getName()));

            try {
                Method declaredMethod = target.getDeclaredMethod(method, field.getType());
                ClassUtils.setAccessible(declaredMethod);
                Object necessary = Converter.convertIfNecessary(rs.toString(), field.getType());
                ClassUtils.invokeMethod(declaredMethod, forObject, necessary);
            } catch (NoSuchMethodException e) {
                ClassUtils.setAccessible(field);
                ClassUtils.setFieldValue(field, Converter.convertIfNecessary(rs.toString(), field.getType()), forObject);
            }

        });

        return forObject;
    }

    /**
     * 随机参数
     *
     * @param rs    结果
     * @param field 字段
     */
    private void analysis(StringBuilder rs, Field field) {
        if (String.class == field.getType()) {
            rs.append(RandomUtils.randomString(10));
            return;
        }

        Class<?> aClass = ClassUtils.fromPrimitive(field.getType());

        boolean b = Integer.class == aClass || Short.class == aClass || Byte.class == aClass || Long.class == aClass;
        if (b) {
            rs.append(RandomUtils.randomInt(Integer.MAX_VALUE));
            return;
        }

        if (Float.class == aClass || Double.class == aClass) {
            rs.append(RandomUtils.randomDouble());
            return;
        }

        b = Date.class == aClass || Time.class == aClass || LocalTime.class == aClass || LocalDate.class == aClass || LocalDateTime.class == aClass;

        if (b) {
            rs.append(DateTime.now().toStandard());
            return;
        }
    }
}
