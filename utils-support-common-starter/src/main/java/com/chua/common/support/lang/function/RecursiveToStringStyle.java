package com.chua.common.support.lang.function;


import com.chua.common.support.utils.ClassUtils;

import java.util.Collection;

/**
 * <p>Works with {@link ToStringBuilder} to create a "deep" {@code toString}.</p>
 *
 * <p>To use this class write code as follows:</p>
 *
 * <pre>
 * public class Job {
 *   String title;
 *   ...
 * }
 *
 * public class Person {
 *   String name;
 *   int age;
 *   boolean smoker;
 *   Job job;
 *
 *   ...
 *
 *   public String toString() {
 *     return new ReflectionToStringBuilder(this, new RecursiveToStringStyle()).toString();
 *   }
 * }
 * </pre>
 *
 * <p>This will produce a toString of the format:
 * {@code Person@7f54[name=Stephen,age=29,smoker=false,job=Job@43cd2[title=Manager]]}</p>
 *
 * @since 3.2
 */
public class RecursiveToStringStyle extends ToStringStyle {

    /**
     * Required for serialization support.
     *
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = 1L;

    /**
     * <p>Constructor.</p>
     */
    public RecursiveToStringStyle() {
    }

    @Override
    public void appendDetail(final StringBuffer buffer, final String fieldName, final Object value) {
        if (!ClassUtils.isPrimitiveWrapper(value.getClass()) &&
                !String.class.equals(value.getClass()) &&
                accept(value.getClass())) {
            buffer.append(ReflectionToStringBuilder.toString(value, this));
        } else {
            super.appendDetail(buffer, fieldName, value);
        }
    }

    @Override
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final Collection<?> coll) {
        appendClassName(buffer, coll);
        appendIdentityHashCode(buffer, coll);
        appendDetail(buffer, fieldName, coll.toArray());
    }

    /**
     * Returns whether or not to recursively format the given {@code Class}.
     * By default, this method always returns {@code true}, but may be overwritten by
     * sub-classes to filter specific classes.
     *
     * @param clazz The class to test.
     * @return Whether or not to recursively format the given {@code Class}.
     */
    protected boolean accept(final Class<?> clazz) {
        return true;
    }
}
