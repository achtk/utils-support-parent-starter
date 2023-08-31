package com.chua.common.support.constant;


import com.chua.common.support.utils.NumberUtils;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_DOT;
import static com.chua.common.support.constant.NumberConstant.*;

/**
 * <p>An enum representing all the versions of the Java specification.
 * This is intended to mirror available values from the
 * <em>java.specification.version</em> System property. </p>
 *
 * @author Administrator
 * @since 3.0
 */
public enum JavaVersion {

    /**
     * The Java version reported by Android. This is not an official Java version number.
     */
    JAVA_0_9(1.5f, "0.9"),

    /**
     * Java 1.1.
     */
    JAVA_1_1(1.1f, "1.1"),

    /**
     * Java 1.2.
     */
    JAVA_1_2(1.2f, "1.2"),

    /**
     * Java 1.3.
     */
    JAVA_1_3(1.3f, "1.3"),

    /**
     * Java 1.4.
     */
    JAVA_1_4(1.4f, "1.4"),

    /**
     * Java 1.5.
     */
    JAVA_1_5(1.5f, "1.5"),

    /**
     * Java 1.6.
     */
    JAVA_1_6(1.6f, "1.6"),

    /**
     * Java 1.7.
     */
    JAVA_1_7(1.7f, "1.7"),

    /**
     * Java 1.8.
     */
    JAVA_1_8(1.8f, "1.8"),

    /**
     * Java 1.9.
     *
     * @deprecated As of release 3.5, replaced by {@link #JAVA_9}
     */
    @Deprecated
    JAVA_1_9(9.0f, "9"),

    /**
     * Java 9.
     *
     * @since 3.5
     */
    JAVA_9(9.0f, "9"),

    /**
     * Java 10.
     *
     * @since 3.7
     */
    JAVA_10(10.0f, "10"),

    /**
     * Java 11.
     *
     * @since 3.8
     */
    JAVA_11(11.0f, "11"),

    /**
     * Java 12.
     *
     * @since 3.9
     */
    JAVA_12(12.0f, "12"),

    /**
     * Java 13.
     *
     * @since 3.9
     */
    JAVA_13(13.0f, "13"),

    /**
     * Java 14.
     *
     * @since 3.11
     */
    JAVA_14(14.0f, "14"),

    /**
     * Java 15.
     *
     * @since 3.11
     */
    JAVA_15(15.0f, "15"),

    /**
     * Java 16.
     *
     * @since 3.11
     */
    JAVA_16(16.0f, "16"),

    /**
     * Java 17.
     *
     * @since 3.12.0
     */
    JAVA_17(17.0f, "17"),

    /**
     * The most recent java version. Mainly introduced to avoid to break when a new version of Java is used.
     */
    JAVA_RECENT(maxVersion(), Float.toString(maxVersion()));

    /**
     * The float value.
     */
    private final float value;

    /**
     * The standard name.
     */
    private final String name;
    public static final String JAVA_SPECIFICATION_VERSION = getSystemProperty("java.specification.version");
    private static final JavaVersion JAVA_SPECIFICATION_VERSION_AS_ENUM = JavaVersion.get(JAVA_SPECIFICATION_VERSION);

    /**
     * Constructor.
     *
     * @param value the float value
     * @param name  the standard name, not null
     */
    JavaVersion(final float value, final String name) {
        this.value = value;
        this.name = name;
    }


    /**
     * <p>
     * Gets a System property, defaulting to {@code null} if the property cannot be read.
     * </p>
     * <p>
     * If a {@code SecurityException} is caught, the return value is {@code null} and a message is written to
     * {@code System.err}.
     * </p>
     *
     * @param property the system property name
     * @return the system property value or {@code null} if a security problem occurs
     */
    private static String getSystemProperty(final String property) {
        try {
            return System.getProperty(property);
        } catch (final SecurityException ex) {
            return null;
        }
    }


    /**
     * <p>
     * Is the Java version at least the requested version.
     * </p>
     * <p>
     *
     * @param requiredVersion the required version, for example 1.31f
     * @return {@code true} if the actual version is equal or greater than the required version
     */
    public static boolean isJavaVersionAtLeast(final JavaVersion requiredVersion) {
        return JAVA_SPECIFICATION_VERSION_AS_ENUM.atLeast(requiredVersion);
    }

    

    /**
     * <p>Whether this version of Java is at least the version of Java passed in.</p>
     *
     * <p>For example:<br>
     * {@code myVersion.atLeast(JavaVersion.JAVA_1_4)}<p>
     *
     * @param requiredVersion the version to check against, not null
     * @return true if this version is equal to or greater than the specified version
     */
    public boolean atLeast(final JavaVersion requiredVersion) {
        return this.value >= requiredVersion.value;
    }

    

    /**
     * <p>Whether this version of Java is at most the version of Java passed in.</p>
     *
     * <p>For example:<br>
     * {@code myVersion.atMost(JavaVersion.JAVA_1_4)}<p>
     *
     * @param requiredVersion the version to check against, not null
     * @return true if this version is equal to or greater than the specified version
     * @since 3.9
     */
    public boolean atMost(final JavaVersion requiredVersion) {
        return this.value <= requiredVersion.value;
    }

    /**
     * Transforms the given string with a Java version number to the
     * corresponding constant of this enumeration class. This method is used
     * internally.
     *
     * @param nom the Java version as string
     * @return the corresponding enumeration constant or <b>null</b> if the
     * version is unknown
     */
    
    static JavaVersion getJavaVersion(final String nom) {
        return get(nom);
    }

    /**
     * Transforms the given string with a Java version number to the
     * corresponding constant of this enumeration class. This method is used
     * internally.
     *
     * @param versionStr the Java version as string
     * @return the corresponding enumeration constant or <b>null</b> if the
     * version is unknown
     */
    static JavaVersion get(final String versionStr) {
        if (versionStr == null) {
            return null;
        }
        switch (versionStr) {
            case "0.9":
                return JAVA_0_9;
            case "1.1":
                return JAVA_1_1;
            case "1.2":
                return JAVA_1_2;
            case "1.3":
                return JAVA_1_3;
            case "1.4":
                return JAVA_1_4;
            case "1.5":
                return JAVA_1_5;
            case "1.6":
                return JAVA_1_6;
            case "1.7":
                return JAVA_1_7;
            case "1.8":
                return JAVA_1_8;
            case "9":
                return JAVA_9;
            case "10":
                return JAVA_10;
            case "11":
                return JAVA_11;
            case "12":
                return JAVA_12;
            case "13":
                return JAVA_13;
            case "14":
                return JAVA_14;
            case "15":
                return JAVA_15;
            case "16":
                return JAVA_16;
            case "17":
                return JAVA_17;
            default:
                final float v = toFloatVersion(versionStr);
                if ((v - ONE_FLOAT) < ONE_FLOAT) {
                    final int firstComma = Math.max(versionStr.indexOf('.'), versionStr.indexOf(','));
                    final int end = Math.max(versionStr.length(), versionStr.indexOf(',', firstComma));
                    if (Float.parseFloat(versionStr.substring(firstComma + 1, end)) > ZERO_NIGHT_FLOAT) {
                        return JAVA_RECENT;
                    }
                } else if (v > NUM_10) {
                    return JAVA_RECENT;
                }
                return null;
        }
    }

    

    /**
     * <p>The string value is overridden to return the standard name.</p>
     *
     * <p>For example, {@code "1.5"}.</p>
     *
     * @return the name, not null
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Gets the Java Version from the system or 99.0 if the {@code java.specification.version} system property is not set.
     *
     * @return the value of {@code java.specification.version} system property or 99.0 if it is not set.
     */
    private static float maxVersion() {
        final float v = toFloatVersion(System.getProperty("java.specification.version", "99.0"));
        if (v > 0) {
            return v;
        }
        return 99f;
    }

    /**
     * Parses a float value from a String.
     *
     * @param value the String to parse.
     * @return the float value represented by the string or -1 if the given String can not be parsed.
     */
    private static float toFloatVersion(final String value) {
        final float defaultReturnValue = -1f;
        if (value.contains(SYMBOL_DOT)) {
            final String[] toParse = value.split("\\.");
            if (toParse.length >= NUM_2) {
                return NumberUtils.toFloat(toParse[0] + '.' + toParse[1], defaultReturnValue);
            }
        } else {
            return NumberUtils.toFloat(value, defaultReturnValue);
        }
        return defaultReturnValue;
    }
}
