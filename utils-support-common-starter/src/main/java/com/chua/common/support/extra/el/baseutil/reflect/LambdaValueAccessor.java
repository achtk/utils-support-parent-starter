package com.chua.common.support.extra.el.baseutil.reflect;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 基础类
 *
 * @author CH
 */
public class LambdaValueAccessor extends ValueAccessor {
    @FunctionalInterface
    public interface GetBoolean {
        /**
         * 调用
         *
         * @param obj object
         * @return result
         */
        boolean get(Object obj);
    }

    @FunctionalInterface
    public interface SetBoolean {
        /**
         * 调用
         *
         * @param obj object
         * @param z   value
         */
        void set(Object obj, boolean z);
    }

    @FunctionalInterface
    public interface GetByte {
        /**
         * 调用
         *
         * @param obj object
         * @return result
         */
        byte get(Object obj);
    }

    @FunctionalInterface
    public interface SetByte {
        /**
         * 调用
         *
         * @param obj object
         * @param b   value
         */
        void set(Object obj, byte b);
    }

    @FunctionalInterface
    public interface GetChar {
        /**
         * 调用
         *
         * @param obj object
         * @return result
         */
        char get(Object obj);
    }

    @FunctionalInterface
    public interface SetChar {
        /**
         * 调用
         *
         * @param obj object
         * @param c   value
         */
        void set(Object obj, char c);
    }

    @FunctionalInterface
    public interface GetShort {
        /**
         * 调用
         *
         * @param obj object
         * @return result
         */
        short get(Object obj);
    }

    @FunctionalInterface
    public interface SetShort {
        /**
         * 调用
         *
         * @param obj object
         * @param s   value
         */
        void set(Object obj, short s);
    }

    @FunctionalInterface
    public interface GetInt {
        /**
         * 调用
         *
         * @param obj object
         * @return result
         */
        int get(Object obj);
    }

    @FunctionalInterface
    public interface SetInt {
        /**
         * 调用
         *
         * @param obj object
         * @param i   value
         */
        void set(Object obj, int i);
    }

    @FunctionalInterface
    public interface GetLong {
        /**
         * 调用
         *
         * @param obj object
         * @return result
         */
        long get(Object obj);
    }

    @FunctionalInterface
    public interface SetLong {
        /**
         * 调用
         *
         * @param obj object
         * @param l   value
         */
        void set(Object obj, long l);
    }

    @FunctionalInterface
    public interface GetFloat {
        /**
         * 调用
         *
         * @param obj object
         * @return result
         */
        float get(Object obj);
    }

    @FunctionalInterface
    public interface SetFloat {
        /**
         * 调用
         *
         * @param obj object
         * @param f   value
         */
        void set(Object obj, float f);
    }

    @FunctionalInterface
    public interface GetDouble {
        /**
         * 调用
         *
         * @param obj object
         * @return result
         */
        double get(Object obj);
    }

    @FunctionalInterface
    public interface SetDouble {
        /**
         * 调用
         *
         * @param obj object
         * @param d   value
         */
        void set(Object obj, double d);
    }

    @FunctionalInterface
    public interface GetBooleanObj {
        /**
         * 调用
         *
         * @param obj object
         * @return result
         */
        Boolean get(Object obj);
    }

    @FunctionalInterface
    public interface SetBooleanObj {
        /**
         * 调用
         *
         * @param obj object
         * @param z   value
         */
        void set(Object obj, Boolean z);
    }

    @FunctionalInterface
    public interface GetByteObj {
        /**
         * 调用
         *
         * @param obj object
         * @return result
         */
        Byte get(Object obj);
    }

    @FunctionalInterface
    public interface SetByteObj {
        /**
         * 调用
         *
         * @param obj object
         * @param b   value
         */
        void set(Object obj, Byte b);
    }

    @FunctionalInterface
    public interface GetCharacter {
        /**
         * 调用
         *
         * @param obj object
         * @return result
         */
        Character get(Object obj);
    }

    @FunctionalInterface
    public interface SetCharacter {
        /**
         * 调用
         *
         * @param obj object
         * @param c   value
         */
        void set(Object obj, Character c);
    }

    @FunctionalInterface
    public interface GetShortObj {
        /**
         * 调用
         *
         * @param obj object
         * @return result
         */
        Short get(Object obj);
    }

    @FunctionalInterface
    public interface SetShortObj {
        /**
         * 调用
         *
         * @param obj object
         * @param s   value
         */
        void set(Object obj, Short s);
    }

    @FunctionalInterface
    public interface GetInteger {
        /**
         * 调用
         *
         * @param obj object
         * @return result
         */
        Integer get(Object obj);
    }

    @FunctionalInterface
    public interface SetInteger {
        /**
         * 调用
         *
         * @param obj object
         * @param i   value
         */
        void set(Object obj, Integer i);
    }

    @FunctionalInterface
    public interface GetLongObj {
        /**
         * 调用
         *
         * @param obj object
         * @return result
         */
        Long get(Object obj);
    }

    @FunctionalInterface
    public interface SetLongObj {
        /**
         * 调用
         *
         * @param obj object
         * @param l   value
         */
        void set(Object obj, Long l);
    }

    @FunctionalInterface
    public interface GetFloatObj {
        /**
         * 调用
         *
         * @param obj object
         * @return result
         */
        Float get(Object obj);
    }

    @FunctionalInterface
    public interface SetFloatObj {
        /**
         * 调用
         *
         * @param obj object
         * @param f   value
         */
        void set(Object obj, Float f);
    }

    @FunctionalInterface
    public interface GetDoubleObj {
        /**
         * 调用
         *
         * @param obj object
         * @return result
         */
        Double get(Object obj);
    }

    @FunctionalInterface
    public interface SetDoubleObj {
        /**
         * 调用
         *
         * @param obj object
         * @param d   value
         */
        void set(Object obj, Double d);
    }

    private Field field;
    private GetBoolean getBoolean;
    private SetBoolean setBoolean;
    private GetByte getByte;
    private SetByte setByte;
    private GetChar getChar;
    private SetChar setChar;
    private GetShort getShort;
    private SetShort setShort;
    private GetInt getInt;
    private SetInt setInt;
    private GetLong getLong;
    private SetLong setLong;
    private GetFloat getFloat;
    private SetFloat setFloat;
    private GetDouble getDouble;
    private SetDouble setDouble;
    private GetBooleanObj getBooleanObj;
    private SetBooleanObj setBooleanObj;
    private GetByteObj getByteObj;
    private SetByteObj setByteObj;
    private GetCharacter getCharacter;
    private SetCharacter setCharacter;
    private GetShortObj getShortObj;
    private SetShortObj setShortObj;
    private GetInteger getInteger;
    private SetInteger setInteger;
    private GetLongObj getLongObj;
    private SetLongObj setLongObj;
    private GetFloatObj getFloatObj;
    private SetFloatObj setFloatObj;
    private GetDoubleObj getDoubleObj;
    private SetDoubleObj setDoubleObj;
    private Function<Object, Object> getObj;
    private BiConsumer<Object, Object> setObj;

    public LambdaValueAccessor(Field field) {
        super(field);
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            String getMethodName = field.getType() == boolean.class ? "is" + field.getName().toUpperCase().charAt(0) + field.getName().substring(1) : "get" + field.getName().toUpperCase().charAt(0) + field.getName().substring(1);
            MethodHandle getMethodHandler = lookup.findVirtual(field.getDeclaringClass(), getMethodName, MethodType.methodType(field.getType()));
            String setMethodName = "set" + field.getName().toUpperCase().charAt(0) + field.getName().substring(1);
            MethodHandle setMethodHandler = lookup.findVirtual(field.getDeclaringClass(), setMethodName, MethodType.methodType(void.class, field.getType()));
            switch (primitiveType) {
                case INT: {
                    //固定参数
                    getInt = (GetInt) LambdaMetafactory.metafactory(lookup,
                            //需要实现的函数式接口的方法名
                            "get",
                            ////固定写法，中间参数是需要实现的函数接口类
                            MethodType.methodType(GetInt.class),
                            // 函数式接口的方法签名
                            MethodType.methodType(int.class, Object.class),
                            //这个函数接口需要引用的类的实例方法
                            getMethodHandler,
                            //实际运行的时候，这个函数式接口的方法签名。也就是将泛型的信息补充上
                            MethodType.methodType(int.class, field.getDeclaringClass())
                    ).getTarget().invoke();
                    //固定参数
                    getInteger = (GetInteger) LambdaMetafactory.metafactory(lookup,
                            //需要实现的函数式接口的方法名
                            "get",
                            ////固定写法，中间参数是需要实现的函数接口类
                            MethodType.methodType(GetInteger.class),
                            // 函数式接口的方法签名
                            MethodType.methodType(Integer.class, Object.class),
                            //这个函数接口需要引用的类的实例方法
                            getMethodHandler,
                            //实际运行的时候，这个函数式接口的方法签名。也就是将泛型的信息补充上
                            MethodType.methodType(Integer.class, field.getDeclaringClass())
                    ).getTarget().invoke();
                    //固定参数
                    setInt = (SetInt) LambdaMetafactory.metafactory(lookup,
                            //需要实现的函数式接口的方法名
                            "set",
                            ////固定写法，中间参数是需要实现的函数接口类
                            MethodType.methodType(SetInt.class),
                            // 函数式接口的方法签名
                            MethodType.methodType(void.class, Object.class, int.class),
                            //这个函数接口需要引用的类的实例方法
                            setMethodHandler,
                            //实际运行的时候，这个函数式接口的方法签名。也就是将泛型的信息补充上
                            MethodType.methodType(void.class, field.getDeclaringClass(), int.class)
                    ).getTarget().invoke();
                    //固定参数
                    setInteger = (SetInteger) LambdaMetafactory.metafactory(lookup,
                            //需要实现的函数式接口的方法名
                            "set",
                            ////固定写法，中间参数是需要实现的函数接口类
                            MethodType.methodType(SetInteger.class),
                            // 函数式接口的方法签名
                            MethodType.methodType(void.class, Object.class, Integer.class),
                            //这个函数接口需要引用的类的实例方法
                            setMethodHandler,
                            //实际运行的时候，这个函数式接口的方法签名。也就是将泛型的信息补充上
                            MethodType.methodType(void.class, field.getDeclaringClass(), Integer.class)
                    ).getTarget().invoke();
                    return;
                }
                case BOOLEAN: {
                    getBoolean = (GetBoolean) LambdaMetafactory.metafactory(lookup, "get", MethodType.methodType(GetBoolean.class), MethodType.methodType(boolean.class, Object.class), getMethodHandler, MethodType.methodType(boolean.class, field.getDeclaringClass())).getTarget().invoke();
                    setBoolean = (SetBoolean) LambdaMetafactory.metafactory(lookup, "set", MethodType.methodType(SetBoolean.class), MethodType.methodType(void.class, Object.class, boolean.class), setMethodHandler, MethodType.methodType(void.class, field.getDeclaringClass(), boolean.class)).getTarget().invoke();
                    getBooleanObj = (GetBooleanObj) LambdaMetafactory.metafactory(lookup, "get", MethodType.methodType(GetBooleanObj.class), MethodType.methodType(Boolean.class, Object.class), getMethodHandler, MethodType.methodType(Boolean.class, field.getDeclaringClass())).getTarget().invoke();
                    setBooleanObj = (SetBooleanObj) LambdaMetafactory.metafactory(lookup, "set", MethodType.methodType(SetBooleanObj.class), MethodType.methodType(void.class, Object.class, Boolean.class), setMethodHandler, MethodType.methodType(void.class, field.getDeclaringClass(), Boolean.class)).getTarget().invoke();
                    return;
                }
                case CHAR: {
                    getChar = (GetChar) LambdaMetafactory.metafactory(lookup, "get", MethodType.methodType(GetChar.class), MethodType.methodType(char.class, Object.class), getMethodHandler, MethodType.methodType(char.class, field.getDeclaringClass())).getTarget().invoke();
                    setChar = (SetChar) LambdaMetafactory.metafactory(lookup, "set", MethodType.methodType(SetChar.class), MethodType.methodType(void.class, Object.class, char.class), setMethodHandler, MethodType.methodType(void.class, field.getDeclaringClass(), char.class)).getTarget().invoke();
                    getCharacter = (GetCharacter) LambdaMetafactory.metafactory(lookup, "get", MethodType.methodType(GetCharacter.class), MethodType.methodType(Character.class, Object.class), getMethodHandler, MethodType.methodType(Character.class, field.getDeclaringClass())).getTarget().invoke();
                    setCharacter = (SetCharacter) LambdaMetafactory.metafactory(lookup, "set", MethodType.methodType(SetCharacter.class), MethodType.methodType(void.class, Object.class, Character.class), setMethodHandler, MethodType.methodType(void.class, field.getDeclaringClass(), Character.class)).getTarget().invoke();
                    return;
                }
                case BYTE: {
                    getByte = (GetByte) LambdaMetafactory.metafactory(lookup, "get", MethodType.methodType(GetByte.class), MethodType.methodType(byte.class, Object.class), getMethodHandler, MethodType.methodType(byte.class, field.getDeclaringClass())).getTarget().invoke();
                    setByte = (SetByte) LambdaMetafactory.metafactory(lookup, "set", MethodType.methodType(SetByte.class), MethodType.methodType(void.class, Object.class, byte.class), setMethodHandler, MethodType.methodType(void.class, field.getDeclaringClass(), byte.class)).getTarget().invoke();
                    getByteObj = (GetByteObj) LambdaMetafactory.metafactory(lookup, "get", MethodType.methodType(GetByteObj.class), MethodType.methodType(Byte.class, Object.class), getMethodHandler, MethodType.methodType(Byte.class, field.getDeclaringClass())).getTarget().invoke();
                    setByteObj = (SetByteObj) LambdaMetafactory.metafactory(lookup, "set", MethodType.methodType(SetByteObj.class), MethodType.methodType(void.class, Object.class, Byte.class), setMethodHandler, MethodType.methodType(void.class, field.getDeclaringClass(), Byte.class)).getTarget().invoke();
                    return;
                }
                case SHORT: {
                    getShort = (GetShort) LambdaMetafactory.metafactory(lookup, "get", MethodType.methodType(GetShort.class), MethodType.methodType(short.class, Object.class), getMethodHandler, MethodType.methodType(short.class, field.getDeclaringClass())).getTarget().invoke();
                    setShort = (SetShort) LambdaMetafactory.metafactory(lookup, "set", MethodType.methodType(SetShort.class), MethodType.methodType(void.class, Object.class, short.class), setMethodHandler, MethodType.methodType(void.class, field.getDeclaringClass(), short.class)).getTarget().invoke();
                    getShortObj = (GetShortObj) LambdaMetafactory.metafactory(lookup, "get", MethodType.methodType(GetShortObj.class), MethodType.methodType(Short.class, Object.class), getMethodHandler, MethodType.methodType(Short.class, field.getDeclaringClass())).getTarget().invoke();
                    setShortObj = (SetShortObj) LambdaMetafactory.metafactory(lookup, "set", MethodType.methodType(SetShortObj.class), MethodType.methodType(void.class, Object.class, Short.class), setMethodHandler, MethodType.methodType(void.class, field.getDeclaringClass(), Short.class)).getTarget().invoke();
                    return;
                }
                case LONG: {
                    getLong = (GetLong) LambdaMetafactory.metafactory(lookup, "get", MethodType.methodType(GetLong.class), MethodType.methodType(long.class, Object.class), getMethodHandler, MethodType.methodType(long.class, field.getDeclaringClass())).getTarget().invoke();
                    setLong = (SetLong) LambdaMetafactory.metafactory(lookup, "set", MethodType.methodType(SetLong.class), MethodType.methodType(void.class, Object.class, long.class), setMethodHandler, MethodType.methodType(void.class, field.getDeclaringClass(), long.class)).getTarget().invoke();
                    getLongObj = (GetLongObj) LambdaMetafactory.metafactory(lookup, "get", MethodType.methodType(GetLongObj.class), MethodType.methodType(Long.class, Object.class), getMethodHandler, MethodType.methodType(Long.class, field.getDeclaringClass())).getTarget().invoke();
                    setLongObj = (SetLongObj) LambdaMetafactory.metafactory(lookup, "set", MethodType.methodType(SetLongObj.class), MethodType.methodType(void.class, Object.class, Long.class), setMethodHandler, MethodType.methodType(void.class, field.getDeclaringClass(), Long.class)).getTarget().invoke();
                    return;
                }
                case FLOAT: {
                    getFloat = (GetFloat) LambdaMetafactory.metafactory(lookup, "get", MethodType.methodType(GetFloat.class), MethodType.methodType(float.class, Object.class), getMethodHandler, MethodType.methodType(float.class, field.getDeclaringClass())).getTarget().invoke();
                    setFloat = (SetFloat) LambdaMetafactory.metafactory(lookup, "set", MethodType.methodType(SetFloat.class), MethodType.methodType(void.class, Object.class, float.class), setMethodHandler, MethodType.methodType(void.class, field.getDeclaringClass(), float.class)).getTarget().invoke();
                    getFloatObj = (GetFloatObj) LambdaMetafactory.metafactory(lookup, "get", MethodType.methodType(GetFloatObj.class), MethodType.methodType(Float.class, Object.class), getMethodHandler, MethodType.methodType(Float.class, field.getDeclaringClass())).getTarget().invoke();
                    setFloatObj = (SetFloatObj) LambdaMetafactory.metafactory(lookup, "set", MethodType.methodType(SetFloatObj.class), MethodType.methodType(void.class, Object.class, Float.class), setMethodHandler, MethodType.methodType(void.class, field.getDeclaringClass(), Float.class)).getTarget().invoke();
                    return;
                }
                case DOUBLE: {
                    getDouble = (GetDouble) LambdaMetafactory.metafactory(lookup, "get", MethodType.methodType(GetDouble.class), MethodType.methodType(double.class, Object.class), getMethodHandler, MethodType.methodType(double.class, field.getDeclaringClass())).getTarget().invoke();
                    setDouble = (SetDouble) LambdaMetafactory.metafactory(lookup, "set", MethodType.methodType(SetDouble.class), MethodType.methodType(void.class, Object.class, double.class), setMethodHandler, MethodType.methodType(void.class, field.getDeclaringClass(), double.class)).getTarget().invoke();
                    getDoubleObj = (GetDoubleObj) LambdaMetafactory.metafactory(lookup, "get", MethodType.methodType(GetDoubleObj.class), MethodType.methodType(Double.class, Object.class), getMethodHandler, MethodType.methodType(Double.class, field.getDeclaringClass())).getTarget().invoke();
                    setDoubleObj = (SetDoubleObj) LambdaMetafactory.metafactory(lookup, "set", MethodType.methodType(SetDoubleObj.class), MethodType.methodType(void.class, Object.class, Double.class), setMethodHandler, MethodType.methodType(void.class, field.getDeclaringClass(), Double.class)).getTarget().invoke();
                    return;
                }
                case STRING:
                case UNKNOWN: {
                    getObj = (Function<Object, Object>) LambdaMetafactory.metafactory(lookup, "apply", MethodType.methodType(Function.class), MethodType.methodType(Object.class, Object.class), getMethodHandler, MethodType.methodType(field.getType(), field.getDeclaringClass())).getTarget().invoke();
                    setObj = (BiConsumer<Object, Object>) LambdaMetafactory.metafactory(lookup, "accept", MethodType.methodType(BiConsumer.class), MethodType.methodType(void.class, Object.class, Object.class), setMethodHandler, MethodType.methodType(void.class, field.getDeclaringClass(), field.getType())).getTarget().invoke();
                    return;
                }
                default:
                    break;
            }
        } catch (Throwable e) {
            ReflectUtil.throwException(e);
        }
    }

    @Override
    public int getInt(Object entity) {
        return getInt.get(entity);
    }

    @Override
    public Integer getIntObject(Object entity) {
        return getInteger.get(entity);
    }

    @Override
    public short getShort(Object entity) {
        return getShort.get(entity);
    }

    @Override
    public Short getShortObject(Object entity) {
        return getShortObj.get(entity);
    }

    @Override
    public boolean getBoolean(Object entity) {
        return getBoolean.get(entity);
    }

    @Override
    public Boolean getBooleanObject(Object entity) {
        return getBooleanObj.get(entity);
    }

    @Override
    public long getLong(Object entity) {
        return getLong.get(entity);
    }

    @Override
    public Long getLongObject(Object entity) {
        return getLongObj.get(entity);
    }

    @Override
    public byte getByte(Object entity) {
        return getByte.get(entity);
    }

    @Override
    public Byte getByteObject(Object entity) {
        return getByteObj.get(entity);
    }

    @Override
    public char getChar(Object entity) {
        return getChar.get(entity);
    }

    @Override
    public Character getCharObject(Object entity) {
        return getCharacter.get(entity);
    }

    @Override
    public float getFloat(Object entity) {
        return getFloat.get(entity);
    }

    @Override
    public Float getFloatObject(Object entity) {
        return getFloatObj.get(entity);
    }

    @Override
    public double getDouble(Object entity) {
        return getDouble.get(entity);
    }

    @Override
    public Double getDoubleObject(Object entity) {
        return getDoubleObj.get(entity);
    }

    @Override
    public void set(Object entity, int value) {
        setInt.set(entity, value);
    }

    @Override
    public void set(Object entity, Integer value) {
        setInteger.set(entity, value);
    }

    @Override
    public void set(Object entity, short value) {
        setShort.set(entity, value);
    }

    @Override
    public void set(Object entity, Short value) {
        setShortObj.set(entity, value);
    }

    @Override
    public void set(Object entity, long value) {
        setLong.set(entity, value);
    }

    @Override
    public void set(Object entity, Long value) {
        setLongObj.set(entity, value);
    }

    @Override
    public void set(Object entity, char value) {
        setChar.set(entity, value);
    }

    @Override
    public void set(Object entity, Character value) {
        setCharacter.set(entity, value);
    }

    @Override
    public void set(Object entity, byte value) {
        setByte.set(entity, value);
    }

    @Override
    public void set(Object entity, Byte value) {
        setByteObj.set(entity, value);
    }

    @Override
    public void set(Object entity, boolean value) {
        setBoolean.set(entity, value);
    }

    @Override
    public void set(Object entity, Boolean value) {
        setBooleanObj.set(entity, value);
    }

    @Override
    public void set(Object entity, float value) {
        setFloat.set(entity, value);
    }

    @Override
    public void set(Object entity, Float value) {
        setFloatObj.set(entity, value);
    }

    @Override
    public void set(Object entity, double value) {
        setDouble.set(entity, value);
    }

    @Override
    public void set(Object entity, Double value) {
        setDoubleObj.set(entity, value);
    }

    @Override
    public void setObject(Object entity, Object value) {
        setObj.accept(entity, value);
    }

    @Override
    public Object get(Object entity) {
        return getObj.apply(entity);
    }
}
