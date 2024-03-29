
package com.chua.common.support.lang.template.basis.interpreter;

/**
 * Used by {@link AstInterpreter} to access fields and methods of objects. This is a singleton class used by all
 * {@link AstInterpreter} instances. Replace the default implementation via {@link #setInstance(BaseReflection)}. The implementation
 * must be thread-safe.
 *
 * @author Administrator
 */
public abstract class BaseReflection {
	private static BaseReflection instance = new JavaReflection();

	/**
	 * Sets the Reflection instance to be used by all Template interpreters
	 *
	 * @param reflection this
	 **/
	public synchronized static void setInstance(BaseReflection reflection) {
		instance = reflection;
	}

	/**
	 * Returns the Reflection instance used to fetch field and call methods
	 *
	 * @return this
	 **/
	public synchronized static BaseReflection getInstance() {
		return instance;
	}

	/**
	 * Returns an opaque handle to a field with the given name or null if the field could not be found
	 *
	 * @param obj  obj
	 * @param name name
	 * @return obj
	 **/
	public abstract Object getField(Object obj, String name);

	/**
	 * Returns an opaque handle to the method with the given name best matching the signature implied by the given arguments, or
	 * null if the method could not be found. If obj is an instance of Class, the matching static method is returned. If the name
	 * is null and the object is a {@link FunctionalInterface}, the first declared method on the object is returned.
	 *
	 * @param obj       obj
	 * @param name      name
	 * @param arguments args
	 * @return obj
	 **/
	public abstract Object getMethod(Object obj, String name, Object... arguments);

	/**
	 * Returns the value of the field from the object. The field must have been previously retrieved via
	 * {@link #getField(Object, String)}.
	 *
	 * @param obj   obj
	 * @param field field
	 * @return obj
	 **/
	public abstract Object getFieldValue(Object obj, Object field);

	/**
	 * Calls the method on the object with the given arguments. The method must have been previously retrieved via
	 * {@link #getMethod(Object, String, Object...)}.
	 *
	 * @param obj       obj
	 * @param method    method
	 * @param arguments args
	 * @return obj
	 **/
	public abstract Object callMethod(Object obj, Object method, Object... arguments);
}
