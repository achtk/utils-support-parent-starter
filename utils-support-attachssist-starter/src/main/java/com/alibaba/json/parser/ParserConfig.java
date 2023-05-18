/*
 * Copyright 1999-2017 Alibaba Group.
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
package com.alibaba.json.parser;

import com.alibaba.json.JSONException;
import com.alibaba.json.annotation.JSONCreator;
import com.alibaba.json.annotation.JSONField;
import com.alibaba.json.annotation.JSONType;
import com.alibaba.json.asm.ClassReader;
import com.alibaba.json.asm.TypeCollector;
import com.alibaba.json.parser.deserializer.ObjectDeserializer;
import com.alibaba.json.serializer.MiscCodec;
import com.alibaba.json.spi.Module;
import com.alibaba.json.util.IdentityHashMap;
import com.alibaba.json.util.ServiceLoader;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.*;
import java.nio.charset.Charset;
import java.security.AccessControlException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.*;
import java.util.regex.Pattern;

/**
 * @author wenshao[szujobs@hotmail.com]
 */
public class ParserConfig {

    public static final String DENY_PROPERTY_INTERNAL = "fastjson.parser.deny.internal";
    public static final String DENY_PROPERTY = "fastjson.parser.deny";
    public static final String AUTOTYPE_ACCEPT = "fastjson.parser.autoTypeAccept";
    public static final String AUTOTYPE_SUPPORT_PROPERTY = "fastjson.parser.autoTypeSupport";
    public static final String SAFE_MODE_PROPERTY = "fastjson.parser.safeMode";

    public static String[] DENYS_INTERNAL;
    public static String[] DENYS;
    public static boolean AUTO_SUPPORT;
    public static boolean SAFE_MODE;
    private static String[] AUTO_TYPE_ACCEPT_LIST;
    private static long[] INTERNAL_WHITELIST_HASHCODES;

    public static ParserConfig global = new ParserConfig();

    public static ParserConfig getGlobalInstance() {
        return global;
    }

    private static com.alibaba.json.util.Function<Class<?>, Boolean> isPrimitiveFuncation = new com.alibaba.json.util.Function<Class<?>, Boolean>() {
        public Boolean apply(Class<?> clazz) {
            return clazz == java.sql.Date.class //
                    || clazz == java.sql.Time.class //
                    || clazz == java.sql.Timestamp.class;
        }
    };

    static {
        refresh();
    }

    {
        denyHashCodes = new long[]{
                0x80D0C70BCC2FEA02L,
                0x868385095A22725FL,
                0x86FC2BF9BEAF7AEFL,
                0x87F52A1B07EA33A6L,
                0x8872F29FD0B0B7A7L,
                0x8BAAEE8F9BF77FA7L,
                0x8EADD40CB2A94443L,
                0x8F75F9FA0DF03F80L,
                0x9172A53F157930AFL,
                0x92122D710E364FB8L,
                0x941866E73BEFF4C9L,
                0x94305C26580F73C5L,
                0x9437792831DF7D3FL,
                0xA123A62F93178B20L,
                0xA85882CE1044C450L,
                0xAA3DAFFDB10C4937L,
                0xAAA9E6B7C1E1C6A7L,
                0xAAAA0826487A3737L,
                0xAB82562F53E6E48FL,
                0xAC6262F52C98AA39L,
                0xAD937A449831E8A0L,
                0xAE50DA1FAD60A096L,
                0xAFF6FF23388E225AL,
                0xAFFF4C95B99A334DL,
                0xB40F341C746EC94FL,
                0xB7E8ED757F5D13A2L,
                0xB98B6B5396932FE9L,
                0xBCDD9DC12766F0CEL,
                0xBCE0DEE34E726499L,
                0xBE4F13E96A6796D0L,
                0xBEBA72FB1CCBA426L,
                0xC00BE1DEBAF2808BL,
                0xC1086AFAE32E6258L,
                0xC2664D0958ECFE4CL,
                0xC41FF7C9C87C7C05L,
                0xC664B363BACA050AL,
                0xC7599EBFE3E72406L,
                0xC8D49E5601E661A9L,
                0xC8F04B3A28909935L,
                0xC963695082FD728EL,
                0xCBF29CE484222325L,
                0xD1EFCDF4B3316D34L,
                0xD54B91CC77B239EDL,
                0xD59EE91F0B09EA01L,
                0xD66F68AB92E7FEF5L,
                0xD8CA3D595E982BACL,
                0xDCD8D615A6449E3EL,
                0xDE23A0809A8B9BD6L,
                0xDEFC208F237D4104L,
                0xDF2DDFF310CDB375L,
                0xE09AE4604842582FL,
                0xE1919804D5BF468FL,
                0xE2EB3AC7E56C467EL,
                0xE603D6A51FAD692BL,
                0xE704FD19052B2A34L,
                0xE9184BE55B1D962AL,
                0xE9F20BAD25F60807L,
                0xED13653CB45C4BEDL,
                0xF2983D099D29B477L,
                0xF3702A4A5490B8E8L,
                0xF474E44518F26736L,
                0xF4D93F4FB3E3D991L,
                0xF5D77DCF8E4D71E6L,
                0xF6C0340E73A36A69L,
                0xF7E96E74DFA58DBCL,
                0xFC773AE20C827691L,
                0xFCF3E78644B98BD8L,
                0xFD5BFC610056D720L,
                0xFFA15BF021F1E37CL,
                0xFFDD1A80F1ED3405L,
                0x10E067CD55C5E5L,
                0x761619136CC13EL,
                0x22BAA234C5BFB8AL,
                0x3085068CB7201B8L,
                0x45B11BC78A3ABA3L,
                0x55CFCA0F2281C07L,
                0xA555C74FE3A5155L,
                0xB6E292FA5955ADEL,
                0xBEF8514D0B79293L,
                0xEE6511B66FD5EF0L,
                0x100150A253996624L,
                0x10B2BDCA849D9B3EL,
                0x10DBC48446E0DAE5L,
                0x119B5B1F10210AFCL,
                0x144277B467723158L,
                0x14DB2E6FEAD04AF0L,
                0x154B6CB22D294CFAL,
                0x17924CCA5227622AL,
                0x193B2697EAAED41AL,
                0x1CD6F11C6A358BB7L,
                0x1E0A8C3358FF3DAEL,
                0x24652CE717E713BBL,
                0x24D2F6048FEF4E49L,
                0x24EC99D5E7DC5571L,
                0x25E962F1C28F71A2L,
                0x275D0732B877AF29L,
                0x28AC82E44E933606L,
                0x2A71CE2CC40A710CL,
                0x2AD1CE3A112F015DL,
                0x2ADFEFBBFE29D931L,
                0x2B3A37467A344CDFL,
                0x2B6DD8B3229D6837L,
                0x2D308DBBC851B0D8L,
                0x2FE950D3EA52AE0DL,
                0x313BB4ABD8D4554CL,
                0x327C8ED7C8706905L,
                0x332F0B5369A18310L,
                0x339A3E0B6BEEBEE9L,
                0x33C64B921F523F2FL,
                0x33E7F3E02571B153L,
                0x34A81EE78429FDF1L,
                0x37317698DCFCE894L,
                0x378307CB0111E878L,
                0x3826F4B2380C8B9BL,
                0x398F942E01920CF0L,
                0x3A31412DBB05C7FFL,
                0x3A7EE0635EB2BC33L,
                0x3ADBA40367F73264L,
                0x3B0B51ECBF6DB221L,
                0x3BF14094A524F0E2L,
                0x42D11A560FC9FBA9L,
                0x43320DC9D2AE0892L,
                0x440E89208F445FB9L,
                0x46C808A4B5841F57L,
                0x470FD3A18BB39414L,
                0x49312BDAFB0077D9L,
                0x4A3797B30328202CL,
                0x4BA3E254E758D70DL,
                0x4BF881E49D37F530L,
                0x4CF54EEC05E3E818L,
                0x4DA972745FEB30C1L,
                0x4EF08C90FF16C675L,
                0x4FD10DDC6D13821FL,
                0x521B4F573376DF4AL,
                0x527DB6B46CE3BCBCL,
                0x535E552D6F9700C1L,
                0x54855E265FE1DAD5L,
                0x5728504A6D454FFCL,
                0x599B5C1213A099ACL,
                0x5A5BD85C072E5EFEL,
                0x5AB0CB3071AB40D1L,
                0x5B6149820275EA42L,
                0x5D74D3E5B9370476L,
                0x5D92E6DDDE40ED84L,
                0x5E61093EF8CDDDBBL,
                0x5F215622FB630753L,
                0x61C5BDD721385107L,
                0x62DB241274397C34L,
                0x636ECCA2A131B235L,
                0x63A220E60A17C7B9L,
                0x647AB0224E149EBEL,
                0x65F81B84C1D920CDL,
                0x665C53C311193973L,
                0x6749835432E0F0D2L,
                0x69B6E0175084B377L,
                0x6A47501EBB2AFDB2L,
                0x6FCABF6FA54CAFFFL,
                0x6FE92D83FC0A4628L,
                0x746BD4A53EC195FBL,
                0x74B50BB9260E31FFL,
                0x75CC60F5871D0FD3L,
                0x767A586A5107FEEFL,
                0x78E5935826671397L,
                0x793ADDDED7A967F5L,
                0x7AA7EE3627A19CF3L,
                0x7AFA070241B8CC4BL,
                0x7ED9311D28BF1A65L,
                0x7ED9481D28BF417AL,
                0x7EE6C477DA20BBE3L
        };

        if (null == AUTO_TYPE_ACCEPT_LIST) {
            refresh();
        }

        long[] hashCodes = new long[AUTO_TYPE_ACCEPT_LIST.length];
        for (int i = 0; i < AUTO_TYPE_ACCEPT_LIST.length; i++) {
            hashCodes[i] = com.alibaba.json.util.TypeUtils.fnv1a_64(AUTO_TYPE_ACCEPT_LIST[i]);
        }

        Arrays.sort(hashCodes);
        acceptHashCodes = hashCodes;
    }

    public final com.alibaba.json.parser.SymbolTable symbolTable = new SymbolTable(4096);
    public final boolean fieldBased;
    private final com.alibaba.json.util.IdentityHashMap<Type, com.alibaba.json.parser.deserializer.ObjectDeserializer> deserializers = new com.alibaba.json.util.IdentityHashMap<Type, com.alibaba.json.parser.deserializer.ObjectDeserializer>();
    private final com.alibaba.json.util.IdentityHashMap<Type, com.alibaba.json.util.IdentityHashMap<Type, com.alibaba.json.parser.deserializer.ObjectDeserializer>> mixInDeserializers = new com.alibaba.json.util.IdentityHashMap<Type, com.alibaba.json.util.IdentityHashMap<Type, com.alibaba.json.parser.deserializer.ObjectDeserializer>>(16);
    private final ConcurrentMap<String, Class<?>> typeMapping = new ConcurrentHashMap<String, Class<?>>(16, 0.75f, 1);
    private final Callable<Void> initDeserializersWithJavaSql = new Callable<Void>() {
        public Void call() {
            deserializers.put(java.sql.Timestamp.class, com.alibaba.json.parser.deserializer.SqlDateDeserializer.instance_timestamp);
            deserializers.put(java.sql.Date.class, com.alibaba.json.parser.deserializer.SqlDateDeserializer.instance);
            deserializers.put(java.sql.Time.class, com.alibaba.json.parser.deserializer.TimeDeserializer.instance);
            deserializers.put(java.util.Date.class, com.alibaba.json.serializer.DateCodec.instance);
            return null;
        }
    };
    public com.alibaba.json.PropertyNamingStrategy propertyNamingStrategy;

    private static boolean awtError = false;
    private static boolean jdk8Error = false;
    private static boolean jodaError = false;
    private static boolean guavaError = false;
    public boolean compatibleWithJavaBean = com.alibaba.json.util.TypeUtils.compatibleWithJavaBean;
    protected ClassLoader defaultClassLoader;
    protected com.alibaba.json.parser.deserializer.ASMDeserializerFactory asmFactory;
    private boolean asmEnable = !com.alibaba.json.util.ASMUtils.IS_ANDROID;
    private boolean autoTypeSupport = AUTO_SUPPORT;
    private long[] internalDenyHashCodes;
    private long[] denyHashCodes;
    private long[] acceptHashCodes;
    private boolean jacksonCompatible = false;
    private List<com.alibaba.json.spi.Module> modules = new ArrayList<com.alibaba.json.spi.Module>();
    private volatile List<AutoTypeCheckHandler> autoTypeCheckHandlers;

    public ParserConfig() {
        this(false);
    }

    public ParserConfig(boolean fieldBase) {
        this(null, null, fieldBase);
    }

    public ParserConfig(ClassLoader parentClassLoader) {
        this(null, parentClassLoader, false);
    }

    private boolean safeMode = SAFE_MODE;

    static void refresh() {
        {
            String property = com.alibaba.json.util.IOUtils.getStringProperty(DENY_PROPERTY_INTERNAL);
            DENYS_INTERNAL = splitItemsFormProperty(property);
        }
        {
            String property = com.alibaba.json.util.IOUtils.getStringProperty(DENY_PROPERTY);
            DENYS = splitItemsFormProperty(property);
        }
        {
            String property = com.alibaba.json.util.IOUtils.getStringProperty(AUTOTYPE_SUPPORT_PROPERTY);
            AUTO_SUPPORT = "true".equals(property);
        }
        {
            String property = com.alibaba.json.util.IOUtils.getStringProperty(SAFE_MODE_PROPERTY);
            SAFE_MODE = "true".equals(property);
        }
        {
            String property = com.alibaba.json.util.IOUtils.getStringProperty(AUTOTYPE_ACCEPT);
            String[] items = splitItemsFormProperty(property);
            if (items == null) {
                items = new String[0];
            }
            AUTO_TYPE_ACCEPT_LIST = items;
        }

        INTERNAL_WHITELIST_HASHCODES = new long[]{
                0x9F2E20FB6049A371L,
                0xA8AAA929446FFCE4L,
                0xD45D6F8C9017FAL,
                0x64DC636F343516DCL
        };
    }

    public ParserConfig(com.alibaba.json.parser.deserializer.ASMDeserializerFactory asmFactory) {
        this(asmFactory, null, false);
    }

    private ParserConfig(com.alibaba.json.parser.deserializer.ASMDeserializerFactory asmFactory, ClassLoader parentClassLoader, boolean fieldBased) {
        this.fieldBased = fieldBased;
        if (asmFactory == null && !com.alibaba.json.util.ASMUtils.IS_ANDROID) {
            try {
                if (parentClassLoader == null) {
                    asmFactory = new com.alibaba.json.parser.deserializer.ASMDeserializerFactory(new com.alibaba.json.util.ASMClassLoader());
                } else {
                    asmFactory = new com.alibaba.json.parser.deserializer.ASMDeserializerFactory(parentClassLoader);
                }
            } catch (ExceptionInInitializerError error) {
                // skip
            } catch (AccessControlException error) {
                // skip
            } catch (NoClassDefFoundError error) {
                // skip
            }
        }

        this.asmFactory = asmFactory;

        if (asmFactory == null) {
            asmEnable = false;
        }

        initDeserializers();

        addItemsToDeny(DENYS);
        addItemsToDeny0(DENYS_INTERNAL);
        addItemsToAccept(AUTO_TYPE_ACCEPT_LIST);

    }

    private static String[] splitItemsFormProperty(final String property ){
        if (property != null && property.length() > 0) {
            return property.split(",");
        }
        return null;
    }

    public void configFromPropety(Properties properties) {
        {
            String property = properties.getProperty(DENY_PROPERTY);
            String[] items = splitItemsFormProperty(property);
            addItemsToDeny(items);
        }
        {
            String property = properties.getProperty(AUTOTYPE_ACCEPT);
            String[] items = splitItemsFormProperty(property);
            addItemsToAccept(items);
        }
        {
            String property = properties.getProperty(AUTOTYPE_SUPPORT_PROPERTY);
            if ("true".equals(property)) {
                this.autoTypeSupport = true;
            } else if ("false".equals(property)) {
                this.autoTypeSupport = false;
            }
        }
    }

    private void addItemsToDeny0(final String[] items){
        if (items == null){
            return;
        }

        for (int i = 0; i < items.length; ++i) {
            String item = items[i];
            this.addDenyInternal(item);
        }
    }

    private void addItemsToDeny(final String[] items){
        if (items == null){
            return;
        }

        for (int i = 0; i < items.length; ++i) {
            String item = items[i];
            this.addDeny(item);
        }
    }

    private void addItemsToAccept(final String[] items){
        if (items == null){
            return;
        }

        for (int i = 0; i < items.length; ++i) {
            String item = items[i];
            this.addAccept(item);
        }
    }

    /**
     * @since 1.2.68
     */
    public boolean isSafeMode() {
        return safeMode;
    }

    /**
     * @since 1.2.68
     */
    public void setSafeMode(boolean safeMode) {
        this.safeMode = safeMode;
    }

    public boolean isAutoTypeSupport() {
        return autoTypeSupport;
    }

    public void setAutoTypeSupport(boolean autoTypeSupport) {
        this.autoTypeSupport = autoTypeSupport;
    }

    public boolean isAsmEnable() {
        return asmEnable;
    }

    public void setAsmEnable(boolean asmEnable) {
        this.asmEnable = asmEnable;
    }

    private static Method getEnumCreator(Class clazz, Class enumClass) {
        Method[] methods = clazz.getMethods();
        Method jsonCreatorMethod = null;
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers())
                    && method.getReturnType() == enumClass
                    && method.getParameterTypes().length == 1
            ) {
                com.alibaba.json.annotation.JSONCreator jsonCreator = method.getAnnotation(JSONCreator.class);
                if (jsonCreator != null) {
                    jsonCreatorMethod = method;
                    break;
                }
            }
        }

        return jsonCreatorMethod;
    }

    /**
     * @deprecated  internal method, dont call
     */
    public static boolean isPrimitive2(final Class<?> clazz) {
        Boolean primitive = clazz.isPrimitive() //
                || clazz == Boolean.class //
                || clazz == Character.class //
                || clazz == Byte.class //
                || clazz == Short.class //
                || clazz == Integer.class //
                || clazz == Long.class //
                || clazz == Float.class //
                || clazz == Double.class //
                || clazz == BigInteger.class //
                || clazz == BigDecimal.class //
                || clazz == String.class //
                || clazz == java.util.Date.class //
                || clazz.isEnum() //
                ;
        if (!primitive) {
            primitive = com.alibaba.json.util.ModuleUtil.callWhenHasJavaSql(isPrimitiveFuncation, clazz);
        }
        return primitive != null ? primitive : false;
    }

    private void initDeserializers() {
        deserializers.put(SimpleDateFormat.class, com.alibaba.json.serializer.MiscCodec.instance);
        deserializers.put(Calendar.class, com.alibaba.json.serializer.CalendarCodec.instance);
        deserializers.put(XMLGregorianCalendar.class, com.alibaba.json.serializer.CalendarCodec.instance);

        deserializers.put(com.alibaba.json.JSONObject.class, com.alibaba.json.parser.deserializer.MapDeserializer.instance);
        deserializers.put(com.alibaba.json.JSONArray.class, com.alibaba.json.serializer.CollectionCodec.instance);

        deserializers.put(Map.class, com.alibaba.json.parser.deserializer.MapDeserializer.instance);
        deserializers.put(HashMap.class, com.alibaba.json.parser.deserializer.MapDeserializer.instance);
        deserializers.put(LinkedHashMap.class, com.alibaba.json.parser.deserializer.MapDeserializer.instance);
        deserializers.put(TreeMap.class, com.alibaba.json.parser.deserializer.MapDeserializer.instance);
        deserializers.put(ConcurrentMap.class, com.alibaba.json.parser.deserializer.MapDeserializer.instance);
        deserializers.put(ConcurrentHashMap.class, com.alibaba.json.parser.deserializer.MapDeserializer.instance);

        deserializers.put(Collection.class, com.alibaba.json.serializer.CollectionCodec.instance);
        deserializers.put(List.class, com.alibaba.json.serializer.CollectionCodec.instance);
        deserializers.put(ArrayList.class, com.alibaba.json.serializer.CollectionCodec.instance);

        deserializers.put(Object.class, com.alibaba.json.parser.deserializer.JavaObjectDeserializer.instance);
        deserializers.put(String.class, com.alibaba.json.serializer.StringCodec.instance);
        deserializers.put(StringBuffer.class, com.alibaba.json.serializer.StringCodec.instance);
        deserializers.put(StringBuilder.class, com.alibaba.json.serializer.StringCodec.instance);
        deserializers.put(char.class, com.alibaba.json.serializer.CharacterCodec.instance);
        deserializers.put(Character.class, com.alibaba.json.serializer.CharacterCodec.instance);
        deserializers.put(byte.class, com.alibaba.json.parser.deserializer.NumberDeserializer.instance);
        deserializers.put(Byte.class, com.alibaba.json.parser.deserializer.NumberDeserializer.instance);
        deserializers.put(short.class, com.alibaba.json.parser.deserializer.NumberDeserializer.instance);
        deserializers.put(Short.class, com.alibaba.json.parser.deserializer.NumberDeserializer.instance);
        deserializers.put(int.class, com.alibaba.json.serializer.IntegerCodec.instance);
        deserializers.put(Integer.class, com.alibaba.json.serializer.IntegerCodec.instance);
        deserializers.put(long.class, com.alibaba.json.serializer.LongCodec.instance);
        deserializers.put(Long.class, com.alibaba.json.serializer.LongCodec.instance);
        deserializers.put(BigInteger.class, com.alibaba.json.serializer.BigIntegerCodec.instance);
        deserializers.put(BigDecimal.class, com.alibaba.json.serializer.BigDecimalCodec.instance);
        deserializers.put(float.class, com.alibaba.json.serializer.FloatCodec.instance);
        deserializers.put(Float.class, com.alibaba.json.serializer.FloatCodec.instance);
        deserializers.put(double.class, com.alibaba.json.parser.deserializer.NumberDeserializer.instance);
        deserializers.put(Double.class, com.alibaba.json.parser.deserializer.NumberDeserializer.instance);
        deserializers.put(boolean.class, com.alibaba.json.serializer.BooleanCodec.instance);
        deserializers.put(Boolean.class, com.alibaba.json.serializer.BooleanCodec.instance);
        deserializers.put(Class.class, com.alibaba.json.serializer.MiscCodec.instance);
        deserializers.put(char[].class, new com.alibaba.json.serializer.CharArrayCodec());

        deserializers.put(AtomicBoolean.class, com.alibaba.json.serializer.BooleanCodec.instance);
        deserializers.put(AtomicInteger.class, com.alibaba.json.serializer.IntegerCodec.instance);
        deserializers.put(AtomicLong.class, com.alibaba.json.serializer.LongCodec.instance);
        deserializers.put(AtomicReference.class, com.alibaba.json.serializer.ReferenceCodec.instance);

        deserializers.put(WeakReference.class, com.alibaba.json.serializer.ReferenceCodec.instance);
        deserializers.put(SoftReference.class, com.alibaba.json.serializer.ReferenceCodec.instance);

        deserializers.put(UUID.class, com.alibaba.json.serializer.MiscCodec.instance);
        deserializers.put(TimeZone.class, com.alibaba.json.serializer.MiscCodec.instance);
        deserializers.put(Locale.class, com.alibaba.json.serializer.MiscCodec.instance);
        deserializers.put(Currency.class, com.alibaba.json.serializer.MiscCodec.instance);

        deserializers.put(Inet4Address.class, com.alibaba.json.serializer.MiscCodec.instance);
        deserializers.put(Inet6Address.class, com.alibaba.json.serializer.MiscCodec.instance);
        deserializers.put(InetSocketAddress.class, com.alibaba.json.serializer.MiscCodec.instance);
        deserializers.put(File.class, com.alibaba.json.serializer.MiscCodec.instance);
        deserializers.put(URI.class, com.alibaba.json.serializer.MiscCodec.instance);
        deserializers.put(URL.class, com.alibaba.json.serializer.MiscCodec.instance);
        deserializers.put(Pattern.class, com.alibaba.json.serializer.MiscCodec.instance);
        deserializers.put(Charset.class, com.alibaba.json.serializer.MiscCodec.instance);
        deserializers.put(com.alibaba.json.JSONPath.class, com.alibaba.json.serializer.MiscCodec.instance);
        deserializers.put(Number.class, com.alibaba.json.parser.deserializer.NumberDeserializer.instance);
        deserializers.put(AtomicIntegerArray.class, com.alibaba.json.serializer.AtomicCodec.instance);
        deserializers.put(AtomicLongArray.class, com.alibaba.json.serializer.AtomicCodec.instance);
        deserializers.put(StackTraceElement.class, com.alibaba.json.parser.deserializer.StackTraceElementDeserializer.instance);

        deserializers.put(Serializable.class, com.alibaba.json.parser.deserializer.JavaObjectDeserializer.instance);
        deserializers.put(Cloneable.class, com.alibaba.json.parser.deserializer.JavaObjectDeserializer.instance);
        deserializers.put(Comparable.class, com.alibaba.json.parser.deserializer.JavaObjectDeserializer.instance);
        deserializers.put(Closeable.class, com.alibaba.json.parser.deserializer.JavaObjectDeserializer.instance);

        deserializers.put(com.alibaba.json.JSONPObject.class, new com.alibaba.json.parser.deserializer.JSONPDeserializer());
        com.alibaba.json.util.ModuleUtil.callWhenHasJavaSql(initDeserializersWithJavaSql);
    }

    /**
     * @deprecated
     */
    public com.alibaba.json.util.IdentityHashMap<Type, com.alibaba.json.parser.deserializer.ObjectDeserializer> getDerializers() {
        return deserializers;
    }

    public com.alibaba.json.util.IdentityHashMap<Type, com.alibaba.json.parser.deserializer.ObjectDeserializer> getDeserializers() {
        return deserializers;
    }

    public com.alibaba.json.parser.deserializer.ObjectDeserializer getDeserializer(Type type) {
        com.alibaba.json.parser.deserializer.ObjectDeserializer deserializer = get(type);
        if (deserializer != null) {
            return deserializer;
        }

        if (type instanceof Class<?>) {
            return getDeserializer((Class<?>) type, type);
        }

        if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            if (rawType instanceof Class<?>) {
                return getDeserializer((Class<?>) rawType, type);
            } else {
                return getDeserializer(rawType);
            }
        }

        if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) type;
            Type[] upperBounds = wildcardType.getUpperBounds();
            if (upperBounds.length == 1) {
                Type upperBoundType = upperBounds[0];
                return getDeserializer(upperBoundType);
            }
        }

        return com.alibaba.json.parser.deserializer.JavaObjectDeserializer.instance;
    }

    public com.alibaba.json.parser.deserializer.ObjectDeserializer getDeserializer(Class<?> clazz, Type type) {
        com.alibaba.json.parser.deserializer.ObjectDeserializer deserializer = get(type);
        if (deserializer == null && type instanceof com.alibaba.json.util.ParameterizedTypeImpl) {
            Type innerType = com.alibaba.json.TypeReference.intern((com.alibaba.json.util.ParameterizedTypeImpl) type);
            deserializer = get(innerType);
        }

        if (deserializer != null) {
            return deserializer;
        }

        if (type == null) {
            type = clazz;
        }

        deserializer = get(type);
        if (deserializer != null) {
            return deserializer;
        }

        {
            com.alibaba.json.annotation.JSONType annotation = com.alibaba.json.util.TypeUtils.getAnnotation(clazz, com.alibaba.json.annotation.JSONType.class);
            if (annotation != null) {
                Class<?> mappingTo = annotation.mappingTo();
                if (mappingTo != Void.class) {
                    return getDeserializer(mappingTo, mappingTo);
                }
            }
        }

        if (type instanceof WildcardType || type instanceof TypeVariable || type instanceof ParameterizedType) {
            deserializer = get(clazz);
        }

        if (deserializer != null) {
            return deserializer;
        }

        for (com.alibaba.json.spi.Module module : modules) {
            deserializer = module.createDeserializer(this, clazz);
            if (deserializer != null) {
                putDeserializer(type, deserializer);
                return deserializer;
            }
        }

        String className = clazz.getName();
        className = className.replace('$', '.');

        if (className.startsWith("java.awt.") //
                && com.alibaba.json.serializer.AwtCodec.support(clazz)) {
            if (!awtError) {
                String[] names = new String[]{
                        "java.awt.Point",
                        "java.awt.Font",
                        "java.awt.Rectangle",
                        "java.awt.Color"
                };

                try {
                    for (String name : names) {
                        if (name.equals(className)) {
                            putDeserializer(Class.forName(name), deserializer = com.alibaba.json.serializer.AwtCodec.instance);
                            return deserializer;
                        }
                    }
                } catch (Throwable e) {
                    // skip
                    awtError = true;
                }

                deserializer = com.alibaba.json.serializer.AwtCodec.instance;
            }
        }

        if (!jdk8Error) {
            try {
                if (className.startsWith("java.time.")) {
                    String[] names = new String[] {
                            "java.time.LocalDateTime",
                            "java.time.LocalDate",
                            "java.time.LocalTime",
                            "java.time.ZonedDateTime",
                            "java.time.OffsetDateTime",
                            "java.time.OffsetTime",
                            "java.time.ZoneOffset",
                            "java.time.ZoneRegion",
                            "java.time.ZoneId",
                            "java.time.Period",
                            "java.time.Duration",
                            "java.time.Instant"
                    };

                    for (String name : names) {
                        if (name.equals(className)) {
                            putDeserializer(Class.forName(name), deserializer = com.alibaba.json.parser.deserializer.Jdk8DateCodec.instance);
                            return deserializer;
                        }
                    }
                } else if (className.startsWith("java.util.Optional")) {
                    String[] names = new String[] {
                            "java.util.Optional",
                            "java.util.OptionalDouble",
                            "java.util.OptionalInt",
                            "java.util.OptionalLong"
                    };
                    for (String name : names) {
                        if (name.equals(className)) {
                            putDeserializer(Class.forName(name), deserializer = com.alibaba.json.parser.deserializer.OptionalCodec.instance);
                            return deserializer;
                        }
                    }
                }
            } catch (Throwable e) {
                // skip
                jdk8Error = true;
            }
        }


        if (className.equals("java.nio.ByteBuffer")) {
            putDeserializer(clazz, deserializer = com.alibaba.json.serializer.ByteBufferCodec.instance);
        }

        if (className.equals("java.nio.file.Path")) {
            putDeserializer(clazz, deserializer = com.alibaba.json.serializer.MiscCodec.instance);
        }

        if (clazz == Map.Entry.class) {
            putDeserializer(clazz, deserializer = com.alibaba.json.serializer.MiscCodec.instance);
        }

        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            for (com.alibaba.json.parser.deserializer.AutowiredObjectDeserializer autowired : ServiceLoader.load(com.alibaba.json.parser.deserializer.AutowiredObjectDeserializer.class,
                    classLoader)) {
                for (Type forType : autowired.getAutowiredFor()) {
                    putDeserializer(forType, autowired);
                }
            }
        } catch (Exception ex) {
            // skip
        }

        if (deserializer == null) {
            deserializer = get(type);
        }

        if (deserializer != null) {
            return deserializer;
        }

        if (clazz.isEnum()) {
            if (jacksonCompatible) {
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (com.alibaba.json.util.TypeUtils.isJacksonCreator(method)) {
                        deserializer = createJavaBeanDeserializer(clazz, type);
                        putDeserializer(type, deserializer);
                        return deserializer;
                    }
                }
            }

            Class mixInType = (Class) com.alibaba.json.JSON.getMixInAnnotations(clazz);

            Class<?> deserClass = null;
            com.alibaba.json.annotation.JSONType jsonType = com.alibaba.json.util.TypeUtils.getAnnotation(mixInType != null ? mixInType : clazz, com.alibaba.json.annotation.JSONType.class);

            if (jsonType != null) {
                deserClass = jsonType.deserializer();
                try {
                    deserializer = (com.alibaba.json.parser.deserializer.ObjectDeserializer) deserClass.newInstance();
                    putDeserializer(clazz, deserializer);
                    return deserializer;
                } catch (Throwable error) {
                    // skip
                }
            }

            Method jsonCreatorMethod = null;
            if (mixInType != null) {
                Method mixedCreator = getEnumCreator(mixInType, clazz);
                if (mixedCreator != null) {
                    try {
                        jsonCreatorMethod = clazz.getMethod(mixedCreator.getName(), mixedCreator.getParameterTypes());
                    } catch (Exception e) {
                        // skip
                    }
                }
            } else {
                jsonCreatorMethod = getEnumCreator(clazz, clazz);
            }

            if (jsonCreatorMethod != null) {
                deserializer = new com.alibaba.json.parser.deserializer.EnumCreatorDeserializer(jsonCreatorMethod);
                putDeserializer(clazz, deserializer);
                return deserializer;
            }

            deserializer = getEnumDeserializer(clazz);
        } else if (clazz.isArray()) {
            deserializer = com.alibaba.json.serializer.ObjectArrayCodec.instance;
        } else if (clazz == Set.class || clazz == HashSet.class || clazz == Collection.class || clazz == List.class
                   || clazz == ArrayList.class) {
            deserializer = com.alibaba.json.serializer.CollectionCodec.instance;
        } else if (Collection.class.isAssignableFrom(clazz)) {
            deserializer = com.alibaba.json.serializer.CollectionCodec.instance;
        } else if (Map.class.isAssignableFrom(clazz)) {
            deserializer = com.alibaba.json.parser.deserializer.MapDeserializer.instance;
        } else if (Throwable.class.isAssignableFrom(clazz)) {
            deserializer = new com.alibaba.json.parser.deserializer.ThrowableDeserializer(this, clazz);
        } else if (com.alibaba.json.parser.deserializer.PropertyProcessable.class.isAssignableFrom(clazz)) {
            deserializer = new com.alibaba.json.parser.deserializer.PropertyProcessableDeserializer((Class<com.alibaba.json.parser.deserializer.PropertyProcessable>) clazz);
        } else if (clazz == InetAddress.class) {
            deserializer = MiscCodec.instance;
        } else {
            deserializer = createJavaBeanDeserializer(clazz, type);
        }

        putDeserializer(type, deserializer);

        return deserializer;
    }

    /**
     * 可以通过重写这个方法，定义自己的枚举反序列化实现
     *
     * @param clazz 转换的类型
     * @return 返回一个枚举的反序列化实现
     * @author zhu.xiaojie
     * @time 2020-4-5
     */
    protected com.alibaba.json.parser.deserializer.ObjectDeserializer getEnumDeserializer(Class<?> clazz) {
        return new com.alibaba.json.parser.deserializer.EnumDeserializer(clazz);
    }

    /**
     *
     * @since 1.2.25
     */
    public void initJavaBeanDeserializers(Class<?>... classes) {
        if (classes == null) {
            return;
        }

        for (Class<?> type : classes) {
            if (type == null) {
                continue;
            }
            com.alibaba.json.parser.deserializer.ObjectDeserializer deserializer = createJavaBeanDeserializer(type, type);
            putDeserializer(type, deserializer);
        }
    }

    public com.alibaba.json.parser.deserializer.ObjectDeserializer createJavaBeanDeserializer(Class<?> clazz, Type type) {
        boolean asmEnable = this.asmEnable & !this.fieldBased;
        if (asmEnable) {
            com.alibaba.json.annotation.JSONType jsonType = com.alibaba.json.util.TypeUtils.getAnnotation(clazz, JSONType.class);

            if (jsonType != null) {
                Class<?> deserializerClass = jsonType.deserializer();
                if (deserializerClass != Void.class) {
                    try {
                        Object deseralizer = deserializerClass.newInstance();
                        if (deseralizer instanceof com.alibaba.json.parser.deserializer.ObjectDeserializer) {
                            return (com.alibaba.json.parser.deserializer.ObjectDeserializer) deseralizer;
                        }
                    } catch (Throwable e) {
                        // skip
                    }
                }

                asmEnable = jsonType.asm()
                        && jsonType.parseFeatures().length == 0;
            }

            if (asmEnable) {
                Class<?> superClass = com.alibaba.json.util.JavaBeanInfo.getBuilderClass(clazz, jsonType);
                if (superClass == null) {
                    superClass = clazz;
                }

                for (;;) {
                    if (!Modifier.isPublic(superClass.getModifiers())) {
                        asmEnable = false;
                        break;
                    }

                    superClass = superClass.getSuperclass();
                    if (superClass == Object.class || superClass == null) {
                        break;
                    }
                }
            }
        }

        if (clazz.getTypeParameters().length != 0) {
            asmEnable = false;
        }

        if (asmEnable && asmFactory != null && asmFactory.classLoader.isExternalClass(clazz)) {
            asmEnable = false;
        }

        if (asmEnable) {
            asmEnable = com.alibaba.json.util.ASMUtils.checkName(clazz.getSimpleName());
        }

        if (asmEnable) {
            if (clazz.isInterface()) {
                asmEnable = false;
            }
            com.alibaba.json.util.JavaBeanInfo beanInfo = com.alibaba.json.util.JavaBeanInfo.build(clazz
                    , type
                    , propertyNamingStrategy
                    , false
                    , com.alibaba.json.util.TypeUtils.compatibleWithJavaBean
                    , jacksonCompatible
            );

            if (asmEnable && beanInfo.fields.length > 200) {
                asmEnable = false;
            }

            Constructor<?> defaultConstructor = beanInfo.defaultConstructor;
            if (asmEnable && defaultConstructor == null && !clazz.isInterface()) {
                asmEnable = false;
            }

            for (com.alibaba.json.util.FieldInfo fieldInfo : beanInfo.fields) {
                if (fieldInfo.getOnly) {
                    asmEnable = false;
                    break;
                }

                Class<?> fieldClass = fieldInfo.fieldClass;
                if (!Modifier.isPublic(fieldClass.getModifiers())) {
                    asmEnable = false;
                    break;
                }

                if (fieldClass.isMemberClass() && !Modifier.isStatic(fieldClass.getModifiers())) {
                    asmEnable = false;
                    break;
                }

                if (fieldInfo.getMember() != null //
                        && !com.alibaba.json.util.ASMUtils.checkName(fieldInfo.getMember().getName())) {
                    asmEnable = false;
                    break;
                }

                com.alibaba.json.annotation.JSONField annotation = fieldInfo.getAnnotation();
                if (annotation != null //
                        && ((!com.alibaba.json.util.ASMUtils.checkName(annotation.name())) //
                        || annotation.format().length() != 0 //
                        || annotation.deserializeUsing() != Void.class //
                        || annotation.parseFeatures().length != 0 //
                        || annotation.unwrapped())
                        || (fieldInfo.method != null && fieldInfo.method.getParameterTypes().length > 1)) {
                    asmEnable = false;
                    break;
                }

                if (fieldClass.isEnum()) { // EnumDeserializer
                    com.alibaba.json.parser.deserializer.ObjectDeserializer fieldDeser = this.getDeserializer(fieldClass);
                    if (!(fieldDeser instanceof com.alibaba.json.parser.deserializer.EnumDeserializer)) {
                        asmEnable = false;
                        break;
                    }
                }
            }
        }

        if (asmEnable) {
            if (clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers())) {
                asmEnable = false;
            }
        }

        if (asmEnable) {
            if (com.alibaba.json.util.TypeUtils.isXmlField(clazz)) {
                asmEnable = false;
            }
        }

        if (!asmEnable) {
            return new com.alibaba.json.parser.deserializer.JavaBeanDeserializer(this, clazz, type);
        }

        com.alibaba.json.util.JavaBeanInfo beanInfo = com.alibaba.json.util.JavaBeanInfo.build(clazz, type, propertyNamingStrategy);
        try {
            return asmFactory.createJavaBeanDeserializer(this, beanInfo);
            // } catch (VerifyError e) {
            // e.printStackTrace();
            // return new JavaBeanDeserializer(this, clazz, type);
        } catch (NoSuchMethodException ex) {
            return new com.alibaba.json.parser.deserializer.JavaBeanDeserializer(this, clazz, type);
        } catch (com.alibaba.json.JSONException asmError) {
            return new com.alibaba.json.parser.deserializer.JavaBeanDeserializer(this, beanInfo);
        } catch (Exception e) {
            throw new com.alibaba.json.JSONException("create asm deserializer error, " + clazz.getName(), e);
        }
    }

    public com.alibaba.json.parser.deserializer.FieldDeserializer createFieldDeserializer(ParserConfig mapping, //
                                                                                          com.alibaba.json.util.JavaBeanInfo beanInfo, //
                                                                                          com.alibaba.json.util.FieldInfo fieldInfo) {
        Class<?> clazz = beanInfo.clazz;
        Class<?> fieldClass = fieldInfo.fieldClass;

        Class<?> deserializeUsing = null;
        JSONField annotation = fieldInfo.getAnnotation();
        if (annotation != null) {
            deserializeUsing = annotation.deserializeUsing();
            if (deserializeUsing == Void.class) {
                deserializeUsing = null;
            }
        }

        if (deserializeUsing == null && (fieldClass == List.class || fieldClass == ArrayList.class)) {
            return new com.alibaba.json.parser.deserializer.ArrayListTypeFieldDeserializer(mapping, clazz, fieldInfo);
        }

        return new com.alibaba.json.parser.deserializer.DefaultFieldDeserializer(mapping, clazz, fieldInfo);
    }

    public void putDeserializer(Type type, com.alibaba.json.parser.deserializer.ObjectDeserializer deserializer) {
        Type mixin = com.alibaba.json.JSON.getMixInAnnotations(type);
        if (mixin != null) {
            com.alibaba.json.util.IdentityHashMap<Type, com.alibaba.json.parser.deserializer.ObjectDeserializer> mixInClasses = this.mixInDeserializers.get(type);
            if (mixInClasses == null) {
                //多线程下可能会重复创建，但不影响正确性
                mixInClasses = new com.alibaba.json.util.IdentityHashMap<Type, com.alibaba.json.parser.deserializer.ObjectDeserializer>(4);
                this.mixInDeserializers.put(type, mixInClasses);
            }
            mixInClasses.put(mixin, deserializer);
        } else {
            this.deserializers.put(type, deserializer);
        }
    }

    /**
     * @deprecated  internal method, dont call
     */
    public boolean isPrimitive(Class<?> clazz) {
        return isPrimitive2(clazz);
    }

    public com.alibaba.json.parser.deserializer.ObjectDeserializer get(Type type) {
        Type mixin = com.alibaba.json.JSON.getMixInAnnotations(type);
        if (null == mixin) {
            return this.deserializers.get(type);
        }
        IdentityHashMap<Type, com.alibaba.json.parser.deserializer.ObjectDeserializer> mixInClasses = this.mixInDeserializers.get(type);
        if (mixInClasses == null) {
            return null;
        }
        return mixInClasses.get(mixin);
    }

    public ObjectDeserializer getDeserializer(com.alibaba.json.util.FieldInfo fieldInfo) {
        return getDeserializer(fieldInfo.fieldClass, fieldInfo.fieldType);
    }

    /**
     * fieldName,field ，先生成fieldName的快照，减少之后的findField的轮询
     *
     * @param clazz
     * @param fieldCacheMap :map&lt;fieldName ,Field&gt;
     */
    public static void  parserAllFieldToCache(Class<?> clazz,Map</**fieldName*/String , Field> fieldCacheMap){
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (!fieldCacheMap.containsKey(fieldName)) {
                fieldCacheMap.put(fieldName, field);
            }
        }
        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            parserAllFieldToCache(clazz.getSuperclass(), fieldCacheMap);
        }
    }

    public static Field getFieldFromCache(String fieldName, Map<String, Field> fieldCacheMap) {
        Field field = fieldCacheMap.get(fieldName);

        if (field == null) {
            field = fieldCacheMap.get("_" + fieldName);
        }

        if (field == null) {
            field = fieldCacheMap.get("m_" + fieldName);
        }

        if (field == null) {
            char c0 = fieldName.charAt(0);
            if (c0 >= 'a' && c0 <= 'z') {
                char[] chars = fieldName.toCharArray();
                chars[0] -= 32; // lower
                String fieldNameX = new String(chars);
                field = fieldCacheMap.get(fieldNameX);
            }

            if (fieldName.length() > 2) {
                char c1 = fieldName.charAt(1);
                if (c0 >= 'a' && c0 <= 'z'
                        && c1 >= 'A' && c1 <= 'Z') {
                    for (Map.Entry<String, Field> entry : fieldCacheMap.entrySet()) {
                        if (fieldName.equalsIgnoreCase(entry.getKey())) {
                            field = entry.getValue();
                            break;
                        }
                    }
                }
            }
        }

        return field;
    }

    public ClassLoader getDefaultClassLoader() {
        return defaultClassLoader;
    }

    public void setDefaultClassLoader(ClassLoader defaultClassLoader) {
        this.defaultClassLoader = defaultClassLoader;
    }

    public void addDenyInternal(String name) {
        if (name == null || name.length() == 0) {
            return;
        }

        long hash = com.alibaba.json.util.TypeUtils.fnv1a_64(name);
        if (internalDenyHashCodes == null) {
            this.internalDenyHashCodes = new long[] {hash};
            return;
        }

        if (Arrays.binarySearch(this.internalDenyHashCodes, hash) >= 0) {
            return;
        }

        long[] hashCodes = new long[this.internalDenyHashCodes.length + 1];
        hashCodes[hashCodes.length - 1] = hash;
        System.arraycopy(this.internalDenyHashCodes, 0, hashCodes, 0, this.internalDenyHashCodes.length);
        Arrays.sort(hashCodes);
        this.internalDenyHashCodes = hashCodes;
    }

    public void addDeny(String name) {
        if (name == null || name.length() == 0) {
            return;
        }

        long hash = com.alibaba.json.util.TypeUtils.fnv1a_64(name);
        if (Arrays.binarySearch(this.denyHashCodes, hash) >= 0) {
            return;
        }

        long[] hashCodes = new long[this.denyHashCodes.length + 1];
        hashCodes[hashCodes.length - 1] = hash;
        System.arraycopy(this.denyHashCodes, 0, hashCodes, 0, this.denyHashCodes.length);
        Arrays.sort(hashCodes);
        this.denyHashCodes = hashCodes;
    }

    public void addAccept(String name) {
        if (name == null || name.length() == 0) {
            return;
        }

        long hash = com.alibaba.json.util.TypeUtils.fnv1a_64(name);
        if (Arrays.binarySearch(this.acceptHashCodes, hash) >= 0) {
            return;
        }

        long[] hashCodes = new long[this.acceptHashCodes.length + 1];
        hashCodes[hashCodes.length - 1] = hash;
        System.arraycopy(this.acceptHashCodes, 0, hashCodes, 0, this.acceptHashCodes.length);
        Arrays.sort(hashCodes);
        this.acceptHashCodes = hashCodes;
    }

    public Class<?> checkAutoType(Class type) {
        if (get(type) != null) {
            return type;
        }

        return checkAutoType(type.getName(), null, com.alibaba.json.JSON.DEFAULT_PARSER_FEATURE);
    }

    public Class<?> checkAutoType(String typeName, Class<?> expectClass) {
        return checkAutoType(typeName, expectClass, com.alibaba.json.JSON.DEFAULT_PARSER_FEATURE);
    }

    public Class<?> checkAutoType(String typeName, Class<?> expectClass, int features) {
        if (typeName == null) {
            return null;
        }

        if (autoTypeCheckHandlers != null) {
            for (AutoTypeCheckHandler h : autoTypeCheckHandlers) {
                Class<?> type = h.handler(typeName, expectClass, features);
                if (type != null) {
                    return type;
                }
            }
        }

        final int safeModeMask = Feature.SafeMode.mask;
        boolean safeMode = this.safeMode
                || (features & safeModeMask) != 0
                || (com.alibaba.json.JSON.DEFAULT_PARSER_FEATURE & safeModeMask) != 0;
        if (safeMode) {
            throw new com.alibaba.json.JSONException("safeMode not support autoType : " + typeName);
        }

        final int mask = Feature.SupportAutoType.mask;
        boolean autoTypeSupport = this.autoTypeSupport
                || (features & mask) != 0
                || (com.alibaba.json.JSON.DEFAULT_PARSER_FEATURE & mask) != 0;

        if (typeName.length() >= 192 || typeName.length() < 3) {
            throw new com.alibaba.json.JSONException("autoType is not support. " + typeName);
        }

        final boolean expectClassFlag;
        if (expectClass == null) {
            expectClassFlag = false;
        } else {
            long expectHash = com.alibaba.json.util.TypeUtils.fnv1a_64(expectClass.getName());
            if (expectHash == 0x90a25f5baa21529eL
                    || expectHash == 0x2d10a5801b9d6136L
                    || expectHash == 0xaf586a571e302c6bL
                    || expectHash == 0xed007300a7b227c6L
                    || expectHash == 0x295c4605fd1eaa95L
                    || expectHash == 0x47ef269aadc650b4L
                    || expectHash == 0x6439c4dff712ae8bL
                    || expectHash == 0xe3dd9875a2dc5283L
                    || expectHash == 0xe2a8ddba03e69e0dL
                    || expectHash == 0xd734ceb4c3e9d1daL
            ) {
                expectClassFlag = false;
            } else {
                expectClassFlag = true;
            }
        }

        String className = typeName.replace('$', '.');
        Class<?> clazz;

        final long h1 = (com.alibaba.json.util.TypeUtils.fnv1a_64_magic_hashcode ^ className.charAt(0)) * com.alibaba.json.util.TypeUtils.fnv1a_64_magic_prime;
        if (h1 == 0xaf64164c86024f1aL) { // [
            throw new com.alibaba.json.JSONException("autoType is not support. " + typeName);
        }

        if ((h1 ^ className.charAt(className.length() - 1)) * com.alibaba.json.util.TypeUtils.fnv1a_64_magic_prime == 0x9198507b5af98f0L) {
            throw new com.alibaba.json.JSONException("autoType is not support. " + typeName);
        }

        final long h3 = (((((com.alibaba.json.util.TypeUtils.fnv1a_64_magic_hashcode ^ className.charAt(0))
                * com.alibaba.json.util.TypeUtils.fnv1a_64_magic_prime)
                ^ className.charAt(1))
                * com.alibaba.json.util.TypeUtils.fnv1a_64_magic_prime)
                ^ className.charAt(2))
                * com.alibaba.json.util.TypeUtils.fnv1a_64_magic_prime;

        long fullHash = com.alibaba.json.util.TypeUtils.fnv1a_64(className);
        boolean internalWhite = Arrays.binarySearch(INTERNAL_WHITELIST_HASHCODES, fullHash) >= 0;

        if (internalDenyHashCodes != null) {
            long hash = h3;
            for (int i = 3; i < className.length(); ++i) {
                hash ^= className.charAt(i);
                hash *= com.alibaba.json.util.TypeUtils.fnv1a_64_magic_prime;
                if (Arrays.binarySearch(internalDenyHashCodes, hash) >= 0) {
                    throw new com.alibaba.json.JSONException("autoType is not support. " + typeName);
                }
            }
        }

        if ((!internalWhite) && (autoTypeSupport || expectClassFlag)) {
            long hash = h3;
            for (int i = 3; i < className.length(); ++i) {
                hash ^= className.charAt(i);
                hash *= com.alibaba.json.util.TypeUtils.fnv1a_64_magic_prime;
                if (Arrays.binarySearch(acceptHashCodes, hash) >= 0) {
                    clazz = com.alibaba.json.util.TypeUtils.loadClass(typeName, defaultClassLoader, true);
                    if (clazz != null) {
                        return clazz;
                    }
                }
                if (Arrays.binarySearch(denyHashCodes, hash) >= 0 && com.alibaba.json.util.TypeUtils.getClassFromMapping(typeName) == null) {
                    if (Arrays.binarySearch(acceptHashCodes, fullHash) >= 0) {
                        continue;
                    }

                    throw new com.alibaba.json.JSONException("autoType is not support. " + typeName);
                }
            }
        }

        clazz = com.alibaba.json.util.TypeUtils.getClassFromMapping(typeName);

        if (clazz == null) {
            clazz = deserializers.findClass(typeName);
        }

        if (expectClass == null && clazz != null && Throwable.class.isAssignableFrom(clazz) && !autoTypeSupport) {
            clazz = null;
        }

        if (clazz == null) {
            clazz = typeMapping.get(typeName);
        }

        if (internalWhite) {
            clazz = com.alibaba.json.util.TypeUtils.loadClass(typeName, defaultClassLoader, true);
        }

        if (clazz != null) {
            if (expectClass != null
                    && clazz != java.util.HashMap.class
                    && clazz != java.util.LinkedHashMap.class
                    && !expectClass.isAssignableFrom(clazz)) {
                throw new com.alibaba.json.JSONException("type not match. " + typeName + " -> " + expectClass.getName());
            }

            return clazz;
        }

        if (!autoTypeSupport) {
            long hash = h3;
            for (int i = 3; i < className.length(); ++i) {
                char c = className.charAt(i);
                hash ^= c;
                hash *= com.alibaba.json.util.TypeUtils.fnv1a_64_magic_prime;

                if (Arrays.binarySearch(denyHashCodes, hash) >= 0) {
                    if (typeName.endsWith("Exception") || typeName.endsWith("Error")) {
                        return null;
                    }

                    throw new com.alibaba.json.JSONException("autoType is not support. " + typeName);
                }

                // white list
                if (Arrays.binarySearch(acceptHashCodes, hash) >= 0) {
                    clazz = com.alibaba.json.util.TypeUtils.loadClass(typeName, defaultClassLoader, true);

                    if (clazz == null) {
                        return expectClass;
                    }

                    if (expectClass != null && expectClass.isAssignableFrom(clazz)) {
                        throw new com.alibaba.json.JSONException("type not match. " + typeName + " -> " + expectClass.getName());
                    }

                    return clazz;
                }
            }
        }

        boolean jsonType = false;
        InputStream is = null;
        try {
            String resource = typeName.replace('.', '/') + ".class";
            if (defaultClassLoader != null) {
                is = defaultClassLoader.getResourceAsStream(resource);
            } else {
                is = ParserConfig.class.getClassLoader().getResourceAsStream(resource);
            }
            if (is != null) {
                com.alibaba.json.asm.ClassReader classReader = new ClassReader(is, true);
                com.alibaba.json.asm.TypeCollector visitor = new TypeCollector("<clinit>", new Class[0]);
                classReader.accept(visitor);
                jsonType = visitor.hasJsonType();
            }
        } catch (Exception e) {
            // skip
        } finally {
            com.alibaba.json.util.IOUtils.close(is);
        }

        if (autoTypeSupport || jsonType || expectClassFlag) {
            boolean cacheClass = autoTypeSupport || jsonType;
            clazz = com.alibaba.json.util.TypeUtils.loadClass(typeName, defaultClassLoader, cacheClass);
        }

        if (clazz != null) {
            if (jsonType) {
                if (autoTypeSupport) {
                    com.alibaba.json.util.TypeUtils.addMapping(typeName, clazz);
                }
                return clazz;
            }

            if (ClassLoader.class.isAssignableFrom(clazz) // classloader is danger
                    || javax.sql.DataSource.class.isAssignableFrom(clazz) // dataSource can load jdbc driver
                    || javax.sql.RowSet.class.isAssignableFrom(clazz) //
                    ) {
                throw new com.alibaba.json.JSONException("autoType is not support. " + typeName);
            }

            if (expectClass != null) {
                if (expectClass.isAssignableFrom(clazz)) {
                    if (autoTypeSupport) {
                        com.alibaba.json.util.TypeUtils.addMapping(typeName, clazz);
                    }
                    return clazz;
                } else {
                    throw new com.alibaba.json.JSONException("type not match. " + typeName + " -> " + expectClass.getName());
                }
            }

            com.alibaba.json.util.JavaBeanInfo beanInfo = com.alibaba.json.util.JavaBeanInfo.build(clazz, clazz, propertyNamingStrategy);
            if (beanInfo.creatorConstructor != null && autoTypeSupport) {
                throw new com.alibaba.json.JSONException("autoType is not support. " + typeName);
            }
        }

        if (!autoTypeSupport) {
            if (typeName.endsWith("Exception") || typeName.endsWith("Error")) {
                return null;
            }

            throw new JSONException("autoType is not support. " + typeName);
        }

        if (clazz != null) {
            if (autoTypeSupport) {
                com.alibaba.json.util.TypeUtils.addMapping(typeName, clazz);
            }
        }

        return clazz;
    }

    public void clearDeserializers() {
        this.deserializers.clear();
        this.initDeserializers();
    }

    public boolean isJacksonCompatible() {
        return jacksonCompatible;
    }

    public void setJacksonCompatible(boolean jacksonCompatible) {
        this.jacksonCompatible = jacksonCompatible;
    }

    public void register(String typeName, Class type) {
        typeMapping.putIfAbsent(typeName, type);
    }

    public void register(Module module) {
        this.modules.add(module);
    }

    public void addAutoTypeCheckHandler(AutoTypeCheckHandler h) {
        List<AutoTypeCheckHandler> autoTypeCheckHandlers = this.autoTypeCheckHandlers;
        if (autoTypeCheckHandlers == null) {
            this.autoTypeCheckHandlers
                    = autoTypeCheckHandlers
                    = new CopyOnWriteArrayList();
        }

        autoTypeCheckHandlers.add(h);
    }

    /**
     * @since 1.2.68
     */
    public interface AutoTypeCheckHandler {
        Class<?> handler(String typeName, Class<?> expectClass, int features);
    }
}
