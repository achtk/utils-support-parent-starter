package com.chua.common.support.task.scheduler.expression;

import java.time.temporal.Temporal;

/**
 * @author CH
 * @since 2022-02-15
 */
final class CompositeAbstractCronFieldBase extends AbstractCronFieldBase {

    private final AbstractCronFieldBase[] fields;

    private final String value;


    private CompositeAbstractCronFieldBase(Type type, AbstractCronFieldBase[] fields, String value) {
        super(type);
        this.fields = fields;
        this.value = value;
    }

    /**
     * Composes the given fields into a {@link AbstractCronFieldBase}.
     */
    public static AbstractCronFieldBase compose(AbstractCronFieldBase[] fields, Type type, String value) {

        if (fields.length == 1) {
            return fields[0];
        } else {
            return new CompositeAbstractCronFieldBase(type, fields, value);
        }
    }


    @Override
    public <T extends Temporal & Comparable<? super T>> T nextOrSame(T temporal) {
        T result = null;
        for (AbstractCronFieldBase field : this.fields) {
            T candidate = field.nextOrSame(temporal);
            boolean rs = result == null ||
                    candidate != null && candidate.compareTo(result) < 0;
            if (rs) {
                result = candidate;
            }
        }
        return result;
    }


    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompositeAbstractCronFieldBase)) {
            return false;
        }
        CompositeAbstractCronFieldBase other = (CompositeAbstractCronFieldBase) o;
        return type() == other.type() &&
                this.value.equals(other.value);
    }

    @Override
    public String toString() {
        return type() + " '" + this.value + "'";

    }
}
