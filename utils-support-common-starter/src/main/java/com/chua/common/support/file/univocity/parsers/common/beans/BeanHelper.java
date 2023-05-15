package com.chua.common.support.file.univocity.parsers.common.beans;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Helper class used to obtain property descriptors from annotated java beans whose values are set via reflection.
 * This class was implemented to eliminate direct compile-time dependency with {@link java.beans.Introspector} and
 * other classes in the {@code java.beans.*} package. This is required to allow Android developers to use univocity-parsers.
 * Android developers should add have <a href="https://code.google.com/p/openbeans/downloads/detail?name=openbeans-1.0.jar">openbeans-1.0.jar</a>
 * in their classpath to be able to use univocity-parsers.
 * <p>
 * When available, the classes from package {@code com.googlecode.openbeans.*} will be used, otherwise the
 * bean introspection classes classes from {@code java.beans.*} package will be loaded.
 * <p>
 * If everything fails, then the parser will try to manipulate fields in annotated java beans directly, instead
 * of using their getters and setters.
 * @author Administrator
 */
public final class BeanHelper {

	private static final PropertyWrapper[] EMPTY = new PropertyWrapper[0];

	private static final Class<?> INTROSPECTOR_CLASS = findIntrospectorImplementationClass();
	private static final Method BEAN_INFO_METHOD = getBeanInfoMethod();
	private static final Method PROPERTY_DESCRIPTOR_METHOD = getMethod("getPropertyDescriptors", BEAN_INFO_METHOD, false);

	static Method PROPERTY_WRITE_METHOD = getMethod("getWriteMethod", PROPERTY_DESCRIPTOR_METHOD, true);
	static Method PROPERTY_READ_METHOD = getMethod("getReadMethod", PROPERTY_DESCRIPTOR_METHOD, true);
	static Method PROPERTY_NAME_METHOD = getMethod("getName", PROPERTY_DESCRIPTOR_METHOD, true);

	private static final Map<Class<?>, WeakReference<PropertyWrapper[]>> DESCRIPTORS = new ConcurrentHashMap<Class<?>, WeakReference<PropertyWrapper[]>>();

	private BeanHelper() {

	}

	/**
	 * Returns the property descriptors of all properties available from a class
	 *
	 * @param beanClass the class whose property descriptors should be returned
	 * @return an array of all property descriptors of the given class. Might be empty.
	 */
	public static PropertyWrapper[] getPropertyDescriptors(Class<?> beanClass) {
		if (PROPERTY_DESCRIPTOR_METHOD == null) {
			return EMPTY;
		}
		PropertyWrapper[] out = null;
		WeakReference<PropertyWrapper[]> reference = DESCRIPTORS.get(beanClass);
		if (reference != null) {
			out = reference.get();
		}

		if (out == null) {
			try {
				Object beanInfo = BEAN_INFO_METHOD.invoke(null, beanClass, Object.class);
				Object[] propertyDescriptors = (Object[]) PROPERTY_DESCRIPTOR_METHOD.invoke(beanInfo);
				out = new PropertyWrapper[propertyDescriptors.length];

				for (int i = 0; i < propertyDescriptors.length; i++) {
					out[i] = new PropertyWrapper(propertyDescriptors[i]);
				}

			} catch (Exception ex) {
				out = EMPTY;
			}
			DESCRIPTORS.put(beanClass, new WeakReference<PropertyWrapper[]>(out));
		}

		return out;
	}

	private static Class<?> findIntrospectorImplementationClass() {
		try {
			return Class.forName("com.googlecode.openbeans.Introspector");
		} catch (Throwable e1) {
			try {
				return Class.forName("java.beans.Introspector");
			} catch (Throwable e2) {
				return null;
			}
		}
	}

	private static Method getBeanInfoMethod() {
		if (INTROSPECTOR_CLASS == null) {
			return null;
		}
		try {
			return INTROSPECTOR_CLASS.getMethod("getBeanInfo", Class.class, Class.class);
		} catch (Throwable e) {
			return null;
		}
	}


	private static Method getMethod(String methodName, Method method, boolean fromComponentType) {
		if (method == null) {
			return null;
		}
		try {
			Class<?> returnType = method.getReturnType();
			if (fromComponentType) {
				returnType = returnType.getComponentType();
			}
			return returnType.getMethod(methodName);
		} catch (Exception ex) {
			return null;
		}
	}
}
