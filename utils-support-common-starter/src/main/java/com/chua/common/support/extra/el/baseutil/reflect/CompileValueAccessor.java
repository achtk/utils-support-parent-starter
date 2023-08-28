package com.chua.common.support.extra.el.baseutil.reflect;

import com.chua.common.support.extra.el.baseutil.smc.SmcHelper;
import com.chua.common.support.extra.el.baseutil.smc.compiler.CompileHelper;
import com.chua.common.support.extra.el.baseutil.smc.model.ClassModel;
import com.chua.common.support.extra.el.baseutil.smc.model.MethodModel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * 基础类
 * @author CH
 */
public class CompileValueAccessor extends ValueAccessor
{
    protected static final AtomicInteger COUNT = new AtomicInteger();

    static String toMethodName(Field field)
    {
        return field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
    }

    public static ValueAccessor create(Field field, CompileHelper compileHelper)
    {
        ClassModel classModel = new ClassModel("ValueAccessor_" + field.getName() + "_" + COUNT.getAndIncrement(), CompileValueAccessor.class);
        Class<?>   type       = field.getType();
        if (type == int.class || type == Integer.class)
        {
            return build(field, compileHelper, classModel, "getInt", int.class, Integer.class);
        }
        else if (type == short.class || type == Short.class)
        {
            return build(field, compileHelper, classModel, "getShort", short.class, Short.class);
        }
        else if (type == long.class || type == Long.class)
        {
            return build(field, compileHelper, classModel, "getLong", long.class, Long.class);
        }
        else if (type == float.class || type == Float.class)
        {
            return build(field, compileHelper, classModel, "getFloat", float.class, Float.class);
        }
        else if (type == double.class || type == Double.class)
        {
            return build(field, compileHelper, classModel, "getDouble", double.class, Double.class);
        }
        else if (type == boolean.class || type == Boolean.class)
        {
            return build(field, compileHelper, classModel, "getBoolean", boolean.class, Boolean.class);
        }
        else if (type == byte.class || type == Byte.class)
        {
            return build(field, compileHelper, classModel, "getByte", byte.class, Byte.class);
        }
        else if (type == char.class || type == Character.class)
        {
            return build(field, compileHelper, classModel, "getChar", char.class, Character.class);
        }
        else
        {
            try
            {
                Method      method      = ValueAccessor.class.getDeclaredMethod("get", Object.class);
                MethodModel methodModel = new MethodModel(method, classModel);
                methodModel.setBody("return ((" + SmcHelper.getReferenceName(field.getDeclaringClass(), classModel) + ")$0).get" + toMethodName(field) + "();");
                classModel.putMethodModel(methodModel);
                method = ValueAccessor.class.getDeclaredMethod("setObject", Object.class, Object.class);
                methodModel = new MethodModel(method, classModel);
                methodModel.setBody("((" + SmcHelper.getReferenceName(field.getDeclaringClass(), classModel) + ")$0).set" + toMethodName(field) + "((" + SmcHelper.getReferenceName(field.getType(), classModel) + ")$1);");
                classModel.putMethodModel(methodModel);
                return (ValueAccessor) compileHelper.compile(classModel).getDeclaredConstructor().newInstance();
            }
            catch (Exception e)
            {
                ReflectUtil.throwException(e);
                return null;
            }
        }
    }

    private static ValueAccessor build(Field field, CompileHelper compileHelper, ClassModel classModel, String getMethodName, Class<?> c1, Class<?> c2)
    {
        try
        {
            overrideGetMethod(field, classModel, getMethodName);
            overrideGetMethod(field, classModel, getMethodName + "Object");
            overrideGetMethod(field, classModel, "get");
            overrideSetMethod(field, classModel, "set", c1);
            overrideSetMethod(field, classModel, "set", c2);
            overrideSetMethod(field, classModel, "setObject", Object.class);
            return (ValueAccessor) compileHelper.compile(classModel).getDeclaredConstructor().newInstance();
        }
        catch (Exception e)
        {
            ReflectUtil.throwException(e);
        }
        return null;
    }

    private static void overrideSetMethod(Field field, ClassModel classModel, String setMethodName, Class paramType) throws NoSuchMethodException
    {
        Method      method      = ValueAccessor.class.getDeclaredMethod(setMethodName, Object.class, paramType);
        MethodModel methodModel = new MethodModel(method, classModel);
        if (paramType == Object.class)
        {
            methodModel.setBody("((" + SmcHelper.getReferenceName(field.getDeclaringClass(), classModel) + ")$0).set" + toMethodName(field) + "((" + SmcHelper.getReferenceName(field.getType(), classModel) + ")$1);");
        }
        else
        {
            methodModel.setBody("((" + SmcHelper.getReferenceName(field.getDeclaringClass(), classModel) + ")$0).set" + toMethodName(field) + "($1);");
        }
        classModel.putMethodModel(methodModel);
    }

    private static void overrideGetMethod(Field field, ClassModel classModel, String getMethodName) throws NoSuchMethodException
    {
        Method      method      = ValueAccessor.class.getDeclaredMethod(getMethodName, Object.class);
        MethodModel methodModel = new MethodModel(method, classModel);
        if (field.getType() != boolean.class)
        {
            methodModel.setBody("return ((" + SmcHelper.getReferenceName(field.getDeclaringClass(), classModel) + ")$0).get" + toMethodName(field) + "();");
        }
        else
        {
            methodModel.setBody("return ((" + SmcHelper.getReferenceName(field.getDeclaringClass(), classModel) + ")$0).is" + toMethodName(field) + "();");
        }
        classModel.putMethodModel(methodModel);
    }

    @Override
    public void set(Object entity, int value)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(Object entity, Integer value)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(Object entity, short value)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(Object entity, Short value)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(Object entity, long value)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(Object entity, Long value)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(Object entity, char value)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(Object entity, Character value)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(Object entity, byte value)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(Object entity, Byte value)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(Object entity, boolean value)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(Object entity, Boolean value)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(Object entity, float value)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(Object entity, Float value)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(Object entity, double value)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(Object entity, Double value)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setObject(Object entity, Object value)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getInt(Object entity)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getIntObject(Object entity)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public short getShort(Object entity)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Short getShortObject(Object entity)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getBoolean(Object entity)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean getBooleanObject(Object entity)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getLong(Object entity)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long getLongObject(Object entity)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte getByte(Object entity)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Byte getByteObject(Object entity)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public char getChar(Object entity)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Character getCharObject(Object entity)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getFloat(Object entity)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Float getFloatObject(Object entity)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getDouble(Object entity)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Double getDoubleObject(Object entity)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object get(Object entity)
    {
        throw new UnsupportedOperationException();
    }
}
