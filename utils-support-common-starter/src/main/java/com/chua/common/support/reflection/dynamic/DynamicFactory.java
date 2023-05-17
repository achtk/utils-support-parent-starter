package com.chua.common.support.reflection.dynamic;

import com.chua.common.support.lang.proxy.MethodIntercept;
import com.chua.common.support.utils.JavassistUtils;
import com.chua.common.support.utils.StringUtils;
import javassist.*;

import java.util.Map;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_RIGHT_BRACKETS;
import static com.chua.common.support.constant.CommonConstant.SYMBOL_SEMICOLON;

/**
 * 动态工厂
 *
 * @author CH
 */
public interface DynamicFactory {

    /**
     * 表达式
     *
     * @param express 表达式
     * @param args    方法工厂
     * @return this
     */
    default DynamicFactory express(String express, Object... args) {
        if (express.endsWith(SYMBOL_SEMICOLON)) {
            express = express.substring(0, express.length() - 1);
        }

        if (express.endsWith(SYMBOL_RIGHT_BRACKETS)) {
            if (args.length > 0) {
                return methodForExpress(express, (MethodFactory) args[0]);
            }
            return methodForExpress(express, it -> "return;");
        }
        return fieldForExpress(express);
    }

    /**
     * 添加属性
     *
     * @param field 字段
     * @return this
     */
    default DynamicFactory fieldForExpress(String field) {
        return fieldForExpress(field, null);
    }

    /**
     * 添加属性
     *
     * @param field 字段
     * @param value 值
     * @return this
     */
    default DynamicFactory fieldForExpress(String field, Object value) {
        ClassPool classPool = ClassPool.getDefault();
        try {
            CtField make = CtField.make(StringUtils.endWithAppend(field, ";"), classPool.get(this.getClass().getName()));
            return field(make.getName(), make.getType().getName(), value);
        } catch (CannotCompileException | NotFoundException ignored) {
        }
        return this;
    }

    /**
     * 设置名称
     *
     * @param name 名称
     * @return this
     */
    DynamicFactory name(String name);

    /**
     * 添加属性
     *
     * @param name  名称
     * @param type  类型
     * @param value 值
     * @return this
     */
    DynamicFactory field(String name, String type, Object value);

    /**
     * 添加属性
     *
     * @param name 名称
     * @param type 类型
     * @return this
     */
    default DynamicFactory field(String name, String type) {
        if (null == type) {
            return this;
        }
        return field(name, type, null);
    }

    /**
     * 添加属性
     *
     * @param name  名称
     * @param value 值
     * @return this
     */
    default DynamicFactory fieldValue(String name, String value) {
        if (null == value) {
            return this;
        }
        return field(name, null, value);
    }

    /**
     * 添加属性
     *
     * @param name  名称
     * @param value 值
     * @return this
     */
    default DynamicFactory field(String name, Object value) {
        if (null == value) {
            return this;
        }
        return field(name, value.getClass().getTypeName(), value);
    }

    /**
     * 添加属性
     *
     * @param params 参数
     * @return this
     */
    default DynamicFactory field(Map<String, Object> params) {
        params.forEach(this::field);
        return this;
    }

    /**
     * 添加方法
     *
     * @param methodExpress 方法表达式
     * @param methodFactory 方法工厂
     * @return this
     */
    default DynamicFactory methodForExpress(String methodExpress, MethodFactory methodFactory) {
        ClassPool classPool = ClassPool.getDefault();
        CtMethod ctMethod = null;
        try {
            ctMethod = CtMethod.make(StringUtils.endWithAppend(methodExpress, ";"), classPool.get(this.getClass().getName()));
            return method(ctMethod.getName(), ctMethod.getReturnType().getName(), JavassistUtils.toTypeName(ctMethod.getParameterTypes()), methodFactory);
        } catch (CannotCompileException | NotFoundException ignored) {
        }
        return this;
    }

    /**
     * 添加方法
     *
     * @param name          名称
     * @param returnType    返回类型
     * @param paramTypes    参数类型
     * @param methodFactory 方法工厂
     * @return this
     */
    DynamicFactory method(String name, String returnType, String[] paramTypes, MethodFactory methodFactory);

    /**
     * 添加方法
     *
     * @param name       名称
     * @param returnType 返回类型
     * @param paramTypes 参数类型
     * @return this
     */
    default DynamicFactory method(String name, String returnType, String... paramTypes) {
        return method(name, returnType, paramTypes, methodName -> "{}");
    }

    /**
     * 添加注解
     *
     * @param annotationFactory 名称
     * @return this
     */
    DynamicFactory methodAnnotation(AnnotationFactory annotationFactory);

    /**
     * 添加属性
     *
     * @param annotationFactory 名称
     * @return this
     */
    DynamicFactory fieldAnnotation(AnnotationFactory annotationFactory);

    /**
     * 添加注解
     *
     * @param name   注解名称
     * @param values 参数
     * @return this
     */
    DynamicFactory typeAnnotation(String name, Map<String, Object> values);

    /**
     * 添加注解
     *
     * @param types 类型
     * @return this
     */
    DynamicFactory construct(String... types);

    /**
     * 父类
     *
     * @param superType 父类
     * @return this
     */
    DynamicFactory superType(String superType);

    /**
     * 接口
     *
     * @param superType 父类
     * @return this
     */
    DynamicFactory interfaces(String... superType);

    /**
     * 包
     *
     * @param packages 包
     * @return this
     */
    DynamicFactory packages(String... packages);

    /**
     * 构建bean
     *
     * @param type            類型
     * @param methodIntercept 攔截器
     * @return bean
     * @throws Exception 异常
     */
    <T> T toBean(Class<T> type, MethodIntercept methodIntercept) throws Exception;

    /**
     * 构建bean
     *
     * @param type 類型
     * @return bean
     * @throws Exception 异常
     */
    default <T> T toBean(Class<T> type) throws Exception {
        return toBean(type, null);
    }

    /**
     * 构建bean
     *
     * @return bean
     * @throws Exception 异常
     */
    default <T> T toBean() throws Exception {
        return toBean(null);
    }
}
