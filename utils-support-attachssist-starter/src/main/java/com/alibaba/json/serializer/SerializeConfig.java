/*
 * Copyright 1999-2018 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.json.serializer;

import com.alibaba.json.PropertyNamingStrategy;
import com.alibaba.json.annotation.JSONField;
import com.alibaba.json.annotation.JSONType;
import com.alibaba.json.parser.deserializer.Jdk8DateCodec;
import com.alibaba.json.parser.deserializer.OptionalCodec;
import com.alibaba.json.spi.Module;
import com.alibaba.json.util.IdentityHashMap;
import com.alibaba.json.util.ServiceLoader;
import com.alibaba.json.util.TypeUtils;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Proxy;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.regex.Pattern;

/**
 * circular references detect
 * 
 * @author wenshao[szujobs@hotmail.com]
 */
public class SerializeConfig {

    public final static SerializeConfig globalInstance = new SerializeConfig();

    private static boolean awtError = false;
    private static boolean jdk8Error = false;
    private static boolean oracleJdbcError = false;
    private static boolean springfoxError = false;
    private static boolean guavaError = false;

    private static boolean jodaError = false;
    private final com.alibaba.json.util.IdentityHashMap<Type, ObjectSerializer> serializers;
    private final com.alibaba.json.util.IdentityHashMap<Type, com.alibaba.json.util.IdentityHashMap<Type, ObjectSerializer>> mixInSerializers;
    private final boolean fieldBased;
    public com.alibaba.json.PropertyNamingStrategy propertyNamingStrategy;
    protected String typeKey = com.alibaba.json.JSON.DEFAULT_TYPE_KEY;
    private boolean asm = !com.alibaba.json.util.ASMUtils.IS_ANDROID;
    private com.alibaba.json.serializer.ASMSerializerFactory asmFactory;
    private long[] denyClasses =
            {
                    4165360493669296979L,
                    4446674157046724083L
            };

    private List<com.alibaba.json.spi.Module> modules = new ArrayList<com.alibaba.json.spi.Module>();

	public SerializeConfig() {
        this(com.alibaba.json.util.IdentityHashMap.DEFAULT_SIZE);
	}

    public SerializeConfig(boolean fieldBase) {
        this(com.alibaba.json.util.IdentityHashMap.DEFAULT_SIZE, fieldBase);
    }

	public SerializeConfig(int tableSize, boolean fieldBase) {
        this.fieldBased = fieldBase;
        serializers = new com.alibaba.json.util.IdentityHashMap<Type, ObjectSerializer>(tableSize);
        this.mixInSerializers = new com.alibaba.json.util.IdentityHashMap<Type, com.alibaba.json.util.IdentityHashMap<Type, ObjectSerializer>>(16);
        try {
            if (asm) {
                asmFactory = new ASMSerializerFactory();
            }
        } catch (Throwable eror) {
            asm = false;
        }

        initSerializers();
    }

    public String getTypeKey() {
        return typeKey;
    }

    public void setTypeKey(String typeKey) {
        this.typeKey = typeKey;
    }

	public boolean isAsmEnable() {
		return asm;
	}

    private final com.alibaba.json.serializer.JavaBeanSerializer createASMSerializer(com.alibaba.json.serializer.SerializeBeanInfo beanInfo) throws Exception {
        com.alibaba.json.serializer.JavaBeanSerializer serializer = asmFactory.createJavaBeanSerializer(beanInfo);

        for (int i = 0; i < serializer.sortedGetters.length; ++i) {
            FieldSerializer fieldDeser = serializer.sortedGetters[i];
            Class<?> fieldClass = fieldDeser.fieldInfo.fieldClass;
            if (fieldClass.isEnum()) {
                ObjectSerializer fieldSer = this.getObjectWriter(fieldClass);
                if (!(fieldSer instanceof com.alibaba.json.serializer.EnumSerializer)) {
                    serializer.writeDirect = false;
                }
            }
        }

        return serializer;
    }

	public static SerializeConfig getGlobalInstance() {
		return globalInstance;
	}

    public final ObjectSerializer createJavaBeanSerializer(Class<?> clazz) {
        String className = clazz.getName();
        long hashCode64 = TypeUtils.fnv1a_64(className);
	    if (Arrays.binarySearch(denyClasses, hashCode64) >= 0) {
            throw new com.alibaba.json.JSONException("not support class : " + className);
        }

        com.alibaba.json.serializer.SerializeBeanInfo beanInfo = TypeUtils.buildBeanInfo(clazz, null, propertyNamingStrategy, fieldBased);
	    if (beanInfo.fields.length == 0 && Iterable.class.isAssignableFrom(clazz)) {
	        return MiscCodec.instance;
	    }

	    return createJavaBeanSerializer(beanInfo);
	}

    public ObjectSerializer createJavaBeanSerializer(com.alibaba.json.serializer.SerializeBeanInfo beanInfo) {
        JSONType jsonType = beanInfo.jsonType;

        boolean asm = this.asm && !fieldBased;

        if (jsonType != null) {
            Class<?> serializerClass = jsonType.serializer();
            if (serializerClass != Void.class) {
                try {
                    Object seralizer = serializerClass.newInstance();
                    if (seralizer instanceof ObjectSerializer) {
                        return (ObjectSerializer) seralizer;
                    }
                } catch (Throwable e) {
                    // skip
                }
	        }

	        if (jsonType.asm() == false) {
	            asm = false;
	        }

	        if (asm) {
                for (com.alibaba.json.serializer.SerializerFeature feature : jsonType.serialzeFeatures()) {
                    if (com.alibaba.json.serializer.SerializerFeature.WriteNonStringValueAsString == feature //
                            || com.alibaba.json.serializer.SerializerFeature.WriteEnumUsingToString == feature //
                            || com.alibaba.json.serializer.SerializerFeature.NotWriteDefaultValue == feature
                            || com.alibaba.json.serializer.SerializerFeature.BrowserCompatible == feature) {
                        asm = false;
                        break;
                    }
                }
            }

            if (asm) {
                final Class<? extends com.alibaba.json.serializer.SerializeFilter>[] filterClasses = jsonType.serialzeFilters();
                if (filterClasses.length != 0) {
                    asm = false;
                }
            }
        }

	    Class<?> clazz = beanInfo.beanType;
		if (!Modifier.isPublic(beanInfo.beanType.getModifiers())) {
            return new com.alibaba.json.serializer.JavaBeanSerializer(beanInfo);
		}



		if (asm && asmFactory.classLoader.isExternalClass(clazz)
				|| clazz == Serializable.class || clazz == Object.class) {
			asm = false;
		}

        if (asm && !com.alibaba.json.util.ASMUtils.checkName(clazz.getSimpleName())) {
            asm = false;
        }

		if (asm && beanInfo.beanType.isInterface()) {
		    asm = false;
        }

		if (asm) {
            for (com.alibaba.json.util.FieldInfo fieldInfo : beanInfo.fields) {
                Field field = fieldInfo.field;
                if (field != null && !field.getType().equals(fieldInfo.fieldClass)) {
                    asm = false;
                    break;
                }

                Method method = fieldInfo.method;
                if (method != null && !method.getReturnType().equals(fieldInfo.fieldClass)) {
                    asm = false;
                    break;
                }

                if (fieldInfo.fieldClass.isEnum()
                        && get(fieldInfo.fieldClass) != com.alibaba.json.serializer.EnumSerializer.instance) {
                    asm = false;
                    break;
                }

    			JSONField annotation = fieldInfo.getAnnotation();

    			if (annotation == null) {
    			    continue;
    			}

    			String format = annotation.format();
    			if (format.length() != 0) {
    			    if (fieldInfo.fieldClass == String.class && "trim".equals(format)) {

                    } else {
                        asm = false;
                        break;
                    }
                }

                if ((!com.alibaba.json.util.ASMUtils.checkName(annotation.name())) //
                        || annotation.jsonDirect()
                        || annotation.serializeUsing() != Void.class
                        || annotation.unwrapped()
                ) {
                    asm = false;
                    break;
                }

                for (com.alibaba.json.serializer.SerializerFeature feature : annotation.serialzeFeatures()) {
                    if (com.alibaba.json.serializer.SerializerFeature.WriteNonStringValueAsString == feature //
                            || com.alibaba.json.serializer.SerializerFeature.WriteEnumUsingToString == feature //
                            || com.alibaba.json.serializer.SerializerFeature.NotWriteDefaultValue == feature
                            || com.alibaba.json.serializer.SerializerFeature.BrowserCompatible == feature
                            || com.alibaba.json.serializer.SerializerFeature.WriteClassName == feature) {
                        asm = false;
                        break;
                    }
                }

                if (TypeUtils.isAnnotationPresentOneToMany(method) || TypeUtils.isAnnotationPresentManyToMany(method)) {
                    asm = false;
                    break;
                }
                if (annotation.defaultValue() != null && !"".equals(annotation.defaultValue())) {
                    asm = false;
                    break;
                }
    		}
		}

		if (asm) {
			try {
                ObjectSerializer asmSerializer = createASMSerializer(beanInfo);
                if (asmSerializer != null) {
                    return asmSerializer;
                }
            } catch (ClassNotFoundException ex) {
			    // skip
			} catch (ClassFormatError e) {
			    // skip
			} catch (ClassCastException e) {
			    // skip
            } catch (OutOfMemoryError e) {
			    if (e.getMessage().indexOf("Metaspace") != -1) {
			        throw e;
                }
                // skip
			} catch (Throwable e) {
                throw new com.alibaba.json.JSONException("create asm serializer error, verson " + com.alibaba.json.JSON.VERSION + ", class " + clazz, e);
			}
		}

        return new com.alibaba.json.serializer.JavaBeanSerializer(beanInfo);
	}

    public SerializeConfig(int tableSize) {
        this(tableSize, false);
    }

	public void setAsmEnable(boolean asmEnable) {
        if (com.alibaba.json.util.ASMUtils.IS_ANDROID) {
            return;
        }
		this.asm = asmEnable;
	}

    private void initSerializers() {
        put(Boolean.class, BooleanCodec.instance);
        put(Character.class, CharacterCodec.instance);
        put(Byte.class, com.alibaba.json.serializer.IntegerCodec.instance);
        put(Short.class, com.alibaba.json.serializer.IntegerCodec.instance);
        put(Integer.class, IntegerCodec.instance);
        put(Long.class, LongCodec.instance);
        put(Float.class, FloatCodec.instance);
        put(Double.class, DoubleSerializer.instance);
        put(BigDecimal.class, BigDecimalCodec.instance);
        put(BigInteger.class, BigIntegerCodec.instance);
        put(String.class, StringCodec.instance);
        put(byte[].class, PrimitiveArraySerializer.instance);
        put(short[].class, PrimitiveArraySerializer.instance);
        put(int[].class, PrimitiveArraySerializer.instance);
        put(long[].class, PrimitiveArraySerializer.instance);
        put(float[].class, PrimitiveArraySerializer.instance);
        put(double[].class, PrimitiveArraySerializer.instance);
        put(boolean[].class, PrimitiveArraySerializer.instance);
        put(char[].class, PrimitiveArraySerializer.instance);
        put(Object[].class, ObjectArrayCodec.instance);
        put(Class.class, MiscCodec.instance);

        put(SimpleDateFormat.class, MiscCodec.instance);
        put(Currency.class, new MiscCodec());
        put(TimeZone.class, MiscCodec.instance);
        put(InetAddress.class, MiscCodec.instance);
        put(Inet4Address.class, MiscCodec.instance);
        put(Inet6Address.class, MiscCodec.instance);
        put(InetSocketAddress.class, MiscCodec.instance);
        put(File.class, MiscCodec.instance);
        put(Appendable.class, com.alibaba.json.serializer.AppendableSerializer.instance);
        put(StringBuffer.class, com.alibaba.json.serializer.AppendableSerializer.instance);
        put(StringBuilder.class, com.alibaba.json.serializer.AppendableSerializer.instance);
        put(Charset.class, ToStringSerializer.instance);
        put(Pattern.class, ToStringSerializer.instance);
        put(Locale.class, ToStringSerializer.instance);
        put(URI.class, ToStringSerializer.instance);
        put(URL.class, ToStringSerializer.instance);
        put(UUID.class, ToStringSerializer.instance);

        // atomic
        put(AtomicBoolean.class, com.alibaba.json.serializer.AtomicCodec.instance);
        put(AtomicInteger.class, com.alibaba.json.serializer.AtomicCodec.instance);
        put(AtomicLong.class, com.alibaba.json.serializer.AtomicCodec.instance);
        put(AtomicReference.class, com.alibaba.json.serializer.ReferenceCodec.instance);
        put(AtomicIntegerArray.class, com.alibaba.json.serializer.AtomicCodec.instance);
        put(AtomicLongArray.class, AtomicCodec.instance);

        put(WeakReference.class, com.alibaba.json.serializer.ReferenceCodec.instance);
        put(SoftReference.class, ReferenceCodec.instance);

        put(LinkedList.class, com.alibaba.json.serializer.CollectionCodec.instance);
    }

    /**
	 * add class level serialize filter
	 * @since 1.2.10
	 */
	public void addFilter(Class<?> clazz, SerializeFilter filter) {
        ObjectSerializer serializer = getObjectWriter(clazz);

        if (serializer instanceof com.alibaba.json.serializer.SerializeFilterable) {
            com.alibaba.json.serializer.SerializeFilterable filterable = (SerializeFilterable) serializer;

            if (this != SerializeConfig.globalInstance) {
                if (filterable == MapSerializer.instance) {
                    MapSerializer newMapSer = new MapSerializer();
                    this.put(clazz, newMapSer);
                    newMapSer.addFilter(filter);
                    return;
                }
            }
	        
	        filterable.addFilter(filter);
	    }
	}

    /**
     * class level serializer feature config
     *
     * @since 1.2.12
     */
    public void config(Class<?> clazz, com.alibaba.json.serializer.SerializerFeature feature, boolean value) {
        ObjectSerializer serializer = getObjectWriter(clazz, false);

        if (serializer == null) {
            com.alibaba.json.serializer.SerializeBeanInfo beanInfo = TypeUtils.buildBeanInfo(clazz, null, propertyNamingStrategy);

            if (value) {
                beanInfo.features |= feature.mask;
            } else {
                beanInfo.features &= ~feature.mask;
            }

            serializer = this.createJavaBeanSerializer(beanInfo);

            put(clazz, serializer);
            return;
        }

        if (serializer instanceof com.alibaba.json.serializer.JavaBeanSerializer) {
            com.alibaba.json.serializer.JavaBeanSerializer javaBeanSerializer = (com.alibaba.json.serializer.JavaBeanSerializer) serializer;
            com.alibaba.json.serializer.SerializeBeanInfo beanInfo = javaBeanSerializer.beanInfo;

            int originalFeaturs = beanInfo.features;
            if (value) {
                beanInfo.features |= feature.mask;
            } else {
                beanInfo.features &= ~feature.mask;
            }

            if (originalFeaturs == beanInfo.features) {
                return;
            }
            
            Class<?> serializerClass = serializer.getClass();
            if (serializerClass != com.alibaba.json.serializer.JavaBeanSerializer.class) {
                ObjectSerializer newSerializer = this.createJavaBeanSerializer(beanInfo);
                this.put(clazz, newSerializer);
            }
        }
    }
    
    public ObjectSerializer getObjectWriter(Class<?> clazz) {
        return getObjectWriter(clazz, true);
    }
	
	public ObjectSerializer getObjectWriter(Class<?> clazz, boolean create) {
        ObjectSerializer writer = get(clazz);

        if (writer != null) {
            return writer;
        }

        try {
            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            for (Object o : ServiceLoader.load(com.alibaba.json.serializer.AutowiredObjectSerializer.class, classLoader)) {
                if (!(o instanceof com.alibaba.json.serializer.AutowiredObjectSerializer)) {
                    continue;
                }

                com.alibaba.json.serializer.AutowiredObjectSerializer autowired = (com.alibaba.json.serializer.AutowiredObjectSerializer) o;
                for (Type forType : autowired.getAutowiredFor()) {
                    put(forType, autowired);
                }
            }
        } catch (ClassCastException ex) {
            // skip
        }

        writer = get(clazz);

        if (writer == null) {
            final ClassLoader classLoader = com.alibaba.json.JSON.class.getClassLoader();
            if (classLoader != Thread.currentThread().getContextClassLoader()) {
                try {
                    for (Object o : ServiceLoader.load(com.alibaba.json.serializer.AutowiredObjectSerializer.class, classLoader)) {

                        if (!(o instanceof com.alibaba.json.serializer.AutowiredObjectSerializer)) {
                            continue;
                        }

                        com.alibaba.json.serializer.AutowiredObjectSerializer autowired = (AutowiredObjectSerializer) o;
                        for (Type forType : autowired.getAutowiredFor()) {
                            put(forType, autowired);
                        }
                    }
                } catch (ClassCastException ex) {
                    // skip
                }

                writer = get(clazz);
            }
        }

        for (com.alibaba.json.spi.Module module : modules) {
            writer = module.createSerializer(this, clazz);
            if (writer != null) {
                put(clazz, writer);
                return writer;
            }
        }
        
        if (writer == null) {
            String className = clazz.getName();
            Class<?> superClass;

            if (Map.class.isAssignableFrom(clazz)) {
                put(clazz, writer = MapSerializer.instance);
            } else if (List.class.isAssignableFrom(clazz)) {
                put(clazz, writer = ListSerializer.instance);
            } else if (Collection.class.isAssignableFrom(clazz)) {
                put(clazz, writer = CollectionCodec.instance);
            } else if (Date.class.isAssignableFrom(clazz)) {
                put(clazz, writer = com.alibaba.json.serializer.DateCodec.instance);
            } else if (com.alibaba.json.JSONAware.class.isAssignableFrom(clazz)) {
                put(clazz, writer = JSONAwareSerializer.instance);
            } else if (JSONSerializable.class.isAssignableFrom(clazz)) {
                put(clazz, writer = JSONSerializableSerializer.instance);
            } else if (com.alibaba.json.JSONStreamAware.class.isAssignableFrom(clazz)) {
                put(clazz, writer = MiscCodec.instance);
            } else if (clazz.isEnum()) {
                Class mixedInType = (Class) com.alibaba.json.JSON.getMixInAnnotations(clazz);

                JSONType jsonType;
                if (mixedInType != null) {
                    jsonType = TypeUtils.getAnnotation(mixedInType, JSONType.class);
                } else {
                    jsonType = TypeUtils.getAnnotation(clazz, JSONType.class);
                }

                if (jsonType != null && jsonType.serializeEnumAsJavaBean()) {
                    put(clazz, writer = createJavaBeanSerializer(clazz));
                } else {
                    Member member = null;
                    if (mixedInType != null) {
                        Member mixedInMember = getEnumValueField(mixedInType);
                        if (mixedInMember != null) {
                            try {
                                if (mixedInMember instanceof Method) {
                                    Method mixedInMethod = (Method) mixedInMember;
                                    member = clazz.getMethod(mixedInMethod.getName(), mixedInMethod.getParameterTypes());
                                }
                            } catch (Exception e) {
                                // skip
                            }
                        }
                    } else {
                        member = getEnumValueField(clazz);
                    }
                    if (member != null) {
                        put(clazz, writer = new com.alibaba.json.serializer.EnumSerializer(member));
                    } else {
                        put(clazz, writer = getEnumSerializer());
                    }
                }
            } else if ((superClass = clazz.getSuperclass()) != null && superClass.isEnum()) {
                JSONType jsonType = TypeUtils.getAnnotation(superClass, JSONType.class);
                if (jsonType != null && jsonType.serializeEnumAsJavaBean()) {
                    put(clazz, writer = createJavaBeanSerializer(clazz));
                } else {
                    put(clazz, writer = getEnumSerializer());
                }
            } else if (clazz.isArray()) {
                Class<?> componentType = clazz.getComponentType();
                ObjectSerializer compObjectSerializer = getObjectWriter(componentType);
                put(clazz, writer = new ArraySerializer(componentType, compObjectSerializer));
            } else if (Throwable.class.isAssignableFrom(clazz)) {
                SerializeBeanInfo beanInfo = TypeUtils.buildBeanInfo(clazz, null, propertyNamingStrategy);
                beanInfo.features |= SerializerFeature.WriteClassName.mask;
                put(clazz, writer = new JavaBeanSerializer(beanInfo));
            } else if (TimeZone.class.isAssignableFrom(clazz) || Map.Entry.class.isAssignableFrom(clazz)) {
                put(clazz, writer = MiscCodec.instance);
            } else if (Appendable.class.isAssignableFrom(clazz)) {
                put(clazz, writer = AppendableSerializer.instance);
            } else if (Charset.class.isAssignableFrom(clazz)) {
                put(clazz, writer = ToStringSerializer.instance);
            } else if (Enumeration.class.isAssignableFrom(clazz)) {
                put(clazz, writer = EnumerationSerializer.instance);
            } else if (Calendar.class.isAssignableFrom(clazz) //
                    || XMLGregorianCalendar.class.isAssignableFrom(clazz)) {
                put(clazz, writer = CalendarCodec.instance);
            } else if (TypeUtils.isClob(clazz)) {
                put(clazz, writer = ClobSerializer.instance);
            } else if (TypeUtils.isPath(clazz)) {
                put(clazz, writer = ToStringSerializer.instance);
            } else if (Iterator.class.isAssignableFrom(clazz)) {
                put(clazz, writer = MiscCodec.instance);
            } else if (org.w3c.dom.Node.class.isAssignableFrom(clazz)) {
                put(clazz, writer = MiscCodec.instance);
            } else {
                if (className.startsWith("java.awt.") //
                        && com.alibaba.json.serializer.AwtCodec.support(clazz) //
                ) {
                    // awt
                    if (!awtError) {
                        try {
                            String[] names = new String[]{
                                    "java.awt.Color",
                                    "java.awt.Font",
                                    "java.awt.Point",
                                    "java.awt.Rectangle"
                            };
                            for (String name : names) {
                                if (name.equals(className)) {
                                    put(Class.forName(name), writer = AwtCodec.instance);
                                    return writer;
                                }
                            }
                        } catch (Throwable e) {
                            awtError = true;
                            // skip
                        }
                    }
                }
                
                // jdk8
                if ((!jdk8Error) //
                    && (className.startsWith("java.time.") //
                        || className.startsWith("java.util.Optional") //
                        || className.equals("java.util.concurrent.atomic.LongAdder")
                        || className.equals("java.util.concurrent.atomic.DoubleAdder")
                    )) {
                    try {
                        {
                            String[] names = new String[]{
                                    "java.time.LocalDateTime",
                                    "java.time.LocalDate",
                                    "java.time.LocalTime",
                                    "java.time.ZonedDateTime",
                                    "java.time.OffsetDateTime",
                                    "java.time.OffsetTime",
                                    "java.time.ZoneOffset",
                                    "java.time.ZoneRegion",
                                    "java.time.Period",
                                    "java.time.Duration",
                                    "java.time.Instant"
                            };
                            for (String name : names) {
                                if (name.equals(className)) {
                                    put(Class.forName(name), writer = Jdk8DateCodec.instance);
                                    return writer;
                                }
                            }
                        }
                        {
                            String[] names = new String[]{
                                    "java.util.Optional",
                                    "java.util.OptionalDouble",
                                    "java.util.OptionalInt",
                                    "java.util.OptionalLong"
                            };
                            for (String name : names) {
                                if (name.equals(className)) {
                                    put(Class.forName(name), writer = OptionalCodec.instance);
                                    return writer;
                                }
                            }
                        }
                        {
                            String[] names = new String[]{
                                    "java.util.concurrent.atomic.LongAdder",
                                    "java.util.concurrent.atomic.DoubleAdder"
                            };
                            for (String name : names) {
                                if (name.equals(className)) {
                                    put(Class.forName(name), writer = AdderSerializer.instance);
                                    return writer;
                                }
                            }
                        }
                    } catch (Throwable e) {
                        // skip
                        jdk8Error = true;
                    }
                }
                
                if ((!oracleJdbcError) //
                    && className.startsWith("oracle.sql.")) {
                    try {
                        String[] names = new String[] {
                                "oracle.sql.DATE",
                                "oracle.sql.TIMESTAMP"
                        };

                        for (String name : names) {
                            if (name.equals(className)) {
                                put(Class.forName(name), writer = DateCodec.instance);
                                return writer;
                            }
                        }
                    } catch (Throwable e) {
                        // skip
                        oracleJdbcError = true;
                    }
                }
                

                if (className.equals("net.sf.json.JSONNull")) {
                    put(clazz, writer = MiscCodec.instance);
                    return writer;
                }
                
				if (className.equals("org.json.JSONObject")) {
                    put(clazz, writer = JSONObjectCodec.instance);
                    return writer;
				}


                if ("java.nio.HeapByteBuffer".equals(className)) {
                    put(clazz, writer = ByteBufferCodec.instance);
                    return writer;
                }


                if ("com.google.protobuf.Descriptors$FieldDescriptor".equals(className)) {
                    put(clazz, writer = ToStringSerializer.instance);
                    return writer;
                }

                Class[] interfaces = clazz.getInterfaces();
                if (interfaces.length == 1 && interfaces[0].isAnnotation()) {
                    put(clazz, com.alibaba.json.serializer.AnnotationSerializer.instance);
                    return AnnotationSerializer.instance;
                }

                if (TypeUtils.isProxy(clazz)) {
                    Class<?> superClazz = clazz.getSuperclass();

                    ObjectSerializer superWriter = getObjectWriter(superClazz);
                    put(clazz, superWriter);
                    return superWriter;
                }

                if (Proxy.isProxyClass(clazz)) {
                    Class handlerClass = null;

                    if (interfaces.length == 2) {
                        handlerClass = interfaces[1];
                    } else {
                        for (Class proxiedInterface : interfaces) {
                            if (proxiedInterface.getName().startsWith("org.springframework.aop.")) {
                                continue;
                            }
                            if (handlerClass != null) {
                                handlerClass = null; // multi-matched
                                break;
                            }
                            handlerClass = proxiedInterface;
                        }
                    }

                    if (handlerClass != null) {
                        ObjectSerializer superWriter = getObjectWriter(handlerClass);
                        put(clazz, superWriter);
                        return superWriter;
                    }
                }

                if (create) {
                    writer = createJavaBeanSerializer(clazz);
                    put(clazz, writer);
                }
            }

            if (writer == null) {
                writer = get(clazz);
            }
        }
        return writer;
    }

    private static Member getEnumValueField(Class clazz) {
        Member member = null;

        Method[] methods = clazz.getMethods();

        for (Method method : methods) {
            if (method.getReturnType() == Void.class) {
                continue;
            }
            JSONField jsonField = method.getAnnotation(JSONField.class);
            if (jsonField != null) {
                if (member != null) {
                    return null;
                }

                member = method;
            }
        }

        for (Field field : clazz.getFields()) {
            JSONField jsonField = field.getAnnotation(JSONField.class);

            if (jsonField != null) {
                if (member != null) {
                    return null;
                }

                member = field;
            }
        }

        return member;
    }

    /**
     * 可以通过重写这个方法，定义自己的枚举序列化实现
     * @return 返回一个枚举的反序列化实现
     * @author zhu.xiaojie
     * @time 2020-4-5
     */
    protected ObjectSerializer getEnumSerializer(){
        return EnumSerializer.instance;
    }
	
    public final ObjectSerializer get(Type type) {
        Type mixin = com.alibaba.json.JSON.getMixInAnnotations(type);
        if (null == mixin) {
            return this.serializers.get(type);
        }
        com.alibaba.json.util.IdentityHashMap<Type, ObjectSerializer> mixInClasses = this.mixInSerializers.get(type);
        if (mixInClasses == null) {
            return null;
        }
        return mixInClasses.get(mixin);
    }

    public boolean put(Object type, Object value) {
        return put((Type)type, (ObjectSerializer)value);
    }

    public boolean put(Type type, ObjectSerializer value) {
        Type mixin = com.alibaba.json.JSON.getMixInAnnotations(type);
        if (mixin != null) {
            com.alibaba.json.util.IdentityHashMap<Type, ObjectSerializer> mixInClasses = this.mixInSerializers.get(type);
            if (mixInClasses == null) {
                //多线程下可能会重复创建，但不影响正确性
                mixInClasses = new IdentityHashMap<Type, ObjectSerializer>(4);
                mixInSerializers.put(type, mixInClasses);
            }
            return mixInClasses.put(mixin, value);
        }
        return this.serializers.put(type, value);
    }

    /**
     * 1.2.24
     * @param enumClasses
     */
	public void configEnumAsJavaBean(Class<? extends Enum>... enumClasses) {
        for (Class<? extends Enum> enumClass : enumClasses) {
            put(enumClass, createJavaBeanSerializer(enumClass));
        }
    }

    /**
     * for spring config support
     * @param propertyNamingStrategy
     */
    public void setPropertyNamingStrategy(PropertyNamingStrategy propertyNamingStrategy) {
        this.propertyNamingStrategy = propertyNamingStrategy;
    }

    public void clearSerializers() {
        this.serializers.clear();
        this.initSerializers();
    }

    public void register(Module module) {
        this.modules.add(module);
    }
}
