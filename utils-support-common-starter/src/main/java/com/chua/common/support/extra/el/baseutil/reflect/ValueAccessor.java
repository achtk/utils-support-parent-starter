package com.chua.common.support.extra.el.baseutil.reflect;

import com.chua.common.support.utils.ClassUtils;
import io.github.karlatemp.unsafeaccessor.Unsafe;

import java.lang.reflect.Field;

public class ValueAccessor {
    protected Field field;
    private long offset;
    protected boolean primitive;
    protected ReflectUtil.Primitive primitiveType;
    private Unsafe unsafe = Unsafe.getUnsafe();

    public ValueAccessor() {
    }

    public ValueAccessor(Field field) {
        this.field = field;
        primitive = field.getType().isPrimitive();
        offset = unsafe.objectFieldOffset(field);
        primitiveType = ReflectUtil.ofPrimitive(field.getType());
    }
    public void set(Object entity, Object value) {

        if(value instanceof Integer ) {
            set(entity, (Integer) value);
            return;
        }

        if(value instanceof Byte ) {
            set(entity, (Byte) value);
            return;
        }

        if(value instanceof Short ) {
            set(entity, (Short) value);
            return;
        }

        if(value instanceof Character ) {
            set(entity, (Character) value);
            return;
        }

        if(value instanceof Long ) {
            set(entity, (Long) value);
            return;
        }

        if(value instanceof Float ) {
            set(entity, (Float) value);
            return;
        }

        if(value instanceof Double ) {
            set(entity, (Double) value);
            return;
        }

        if(value instanceof Boolean ) {
            set(entity, (Boolean) value);
            return;
        }
    }

    public void set(Object entity, int value) {
        if (primitive) {
            unsafe.putInt(entity, offset, value);
        } else {
            unsafe.putReference(entity, offset, Integer.valueOf(value));
        }
    }

    public void set(Object entity, Integer value) {
        if (primitive) {
            unsafe.putInt(entity, offset, value.intValue());
        } else {
            unsafe.putReference(entity, offset, value);
        }
    }

    public void set(Object entity, short value) {
        if (primitive) {
            unsafe.putShort(entity, offset, value);
        } else {
            unsafe.putReference(entity, offset, Short.valueOf(value));
        }
    }

    public void set(Object entity, Short value) {
        if (primitive) {
            unsafe.putShort(entity, offset, value.shortValue());
        } else {
            unsafe.putReference(entity, offset, value);
        }
    }

    public void set(Object entity, long value) {
        if (primitive) {
            unsafe.putLong(entity, offset, value);
        } else {
            unsafe.putReference(entity, offset, Long.valueOf(value));
        }
    }

    public void set(Object entity, Long value) {
        if (primitive) {
            unsafe.putLong(entity, offset, value.longValue());
        } else {
            unsafe.putReference(entity, offset, value);
        }
    }

    public void set(Object entity, char value) {
        if (primitive) {
            unsafe.putChar(entity, offset, value);
        } else {
            unsafe.putReference(entity, offset, Character.valueOf(value));
        }
    }

    public void set(Object entity, Character value) {
        if (primitive) {
            unsafe.putChar(entity, offset, value.charValue());
        } else {
            unsafe.putReference(entity, offset, value);
        }
    }

    public void set(Object entity, byte value) {
        if (primitive) {
            unsafe.putByte(entity, offset, value);
        } else {
            unsafe.putReference(entity, offset, Byte.valueOf(value));
        }
    }

    public void set(Object entity, Byte value) {
        if (primitive) {
            unsafe.putByte(entity, offset, value.byteValue());
        } else {
            unsafe.putReference(entity, offset, value);
        }
    }

    public void set(Object entity, boolean value) {
        if (primitive) {
            unsafe.putBoolean(entity, offset, value);
        } else {
            unsafe.putReference(entity, offset, Boolean.valueOf(value));
        }
    }

    public void set(Object entity, Boolean value) {
        if (primitive) {
            unsafe.putBoolean(entity, offset, value.booleanValue());
        } else {
            unsafe.putReference(entity, offset, value);
        }
    }

    public void set(Object entity, float value) {
        if (primitive) {
            unsafe.putFloat(entity, offset, value);
        } else {
            unsafe.putReference(entity, offset, Float.valueOf(value));
        }
    }

    public void set(Object entity, Float value) {
        if (primitive) {
            unsafe.putFloat(entity, offset, value.floatValue());
        } else {
            unsafe.putReference(entity, offset, value);
        }
    }

    public void set(Object entity, double value) {
        if (primitive) {
            unsafe.putDouble(entity, offset, value);
        } else {
            unsafe.putReference(entity, offset, Double.valueOf(value));
        }
    }

    public void set(Object entity, Double value) {
        if (primitive) {
            unsafe.putDouble(entity, offset, value.doubleValue());
        } else {
            unsafe.putReference(entity, offset, value);
        }
    }

    public void setObject(Object entity, Object value) {
        if (primitive) {
            switch (primitiveType) {
                case INT:
                    unsafe.putInt(entity, offset, ((Number) value).intValue());break;
                case SHORT:
                    unsafe.putShort(entity, offset, ((Number) value).shortValue());break;
                case LONG:
                    unsafe.putLong(entity, offset, ((Number) value).longValue());break;
                case FLOAT:
                    unsafe.putFloat(entity, offset, ((Number) value).floatValue());break;
                case DOUBLE:
                    unsafe.putDouble(entity, offset, ((Number) value).doubleValue());break;
                case BOOL:
                    unsafe.putBoolean(entity, offset, ((Boolean) value).booleanValue());break;
                case BYTE:
                    unsafe.putByte(entity, offset, ((Number) value).byteValue());break;
                case CHAR:
                    unsafe.putChar(entity, offset, ((Character) value).charValue());break;
                default:
                    throw new UnsupportedOperationException();
            }
        } else {
            unsafe.putReference(entity, offset, value);
        }
    }

    public int getInt(Object entity) {
        return primitive ? unsafe.getInt(entity, offset) : (Integer) unsafe.getReference(entity, offset);
    }

    public Integer getIntObject(Object entity) {
        return primitive ? Integer.valueOf(unsafe.getInt(entity, offset)) : (Integer) unsafe.getReference(entity, offset);
    }

    public short getShort(Object entity) {
        return primitive ? unsafe.getShort(entity, offset) : (Short) unsafe.getReference(entity, offset);
    }

    public Short getShortObject(Object entity) {
        return primitive ? Short.valueOf(unsafe.getShort(entity, offset)) : (Short) unsafe.getReference(entity, offset);
    }

    public boolean getBoolean(Object entity) {
        return primitive ? unsafe.getBoolean(entity, offset) : (Boolean) unsafe.getReference(entity, offset);
    }

    public Boolean getBooleanObject(Object entity) {
        return primitive ? Boolean.valueOf(unsafe.getBoolean(entity, offset)) : (Boolean) unsafe.getReference(entity, offset);
    }

    public long getLong(Object entity) {
        return primitive ? unsafe.getLong(entity, offset) : (Long) unsafe.getReference(entity, offset);
    }

    public Long getLongObject(Object entity) {
        return primitive ? Long.valueOf(unsafe.getLong(entity, offset)) : (Long) unsafe.getReference(entity, offset);
    }

    public byte getByte(Object entity) {
        return primitive ? unsafe.getByte(entity, offset) : (Byte) unsafe.getReference(entity, offset);
    }

    public Byte getByteObject(Object entity) {
        return primitive ? Byte.valueOf(unsafe.getByte(entity, offset)) : (Byte) unsafe.getReference(entity, offset);
    }

    public char getChar(Object entity) {
        return primitive ? unsafe.getChar(entity, offset) : (Character) unsafe.getReference(entity, offset);
    }

    public Character getCharObject(Object entity) {
        return primitive ? Character.valueOf(unsafe.getChar(entity, offset)) : (Character) unsafe.getReference(entity, offset);
    }

    public float getFloat(Object entity) {
        return primitive ? unsafe.getFloat(entity, offset) : (Float) unsafe.getReference(entity, offset);
    }

    public Float getFloatObject(Object entity) {
        return primitive ? Float.valueOf(unsafe.getFloat(entity, offset)) : (Float) unsafe.getReference(entity, offset);
    }

    public double getDouble(Object entity) {
        return primitive ? unsafe.getDouble(entity, offset) : (Double) unsafe.getReference(entity, offset);
    }

    public Double getDoubleObject(Object entity) {
        return primitive ? Double.valueOf(unsafe.getDouble(entity, offset)) : (Double) unsafe.getReference(entity, offset);
    }

    public Object get(Object entity) {
        if (primitive) {
            switch (primitiveType) {
                case INT:
                    return unsafe.getInt(entity, offset);
                case SHORT:
                    return unsafe.getShort(entity, offset);
                case LONG:
                    return unsafe.getLong(entity, offset);
                case FLOAT:
                    return unsafe.getFloat(entity, offset);
                case DOUBLE:
                    return unsafe.getDouble(entity, offset);
                case BOOL:
                    return unsafe.getBoolean(entity, offset);
                case BYTE:
                    return unsafe.getByte(entity, offset);
                case CHAR:
                    return unsafe.getChar(entity, offset);
                default:
                    throw new UnsupportedOperationException();
            }
        } else {
            return unsafe.getReference(entity, offset);
        }
    }

    public Field getField() {
        return field;
    }

    public Object newInstace() {
        throw new IllegalStateException("还未创建ValueAccessor构造器实例");
    }

    public Object newInstance(Object... params) {
        throw new IllegalStateException("还未创建ValueAccessor构造器实例");
    }
}
