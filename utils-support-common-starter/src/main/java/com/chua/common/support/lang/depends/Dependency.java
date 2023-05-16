package com.chua.common.support.lang.depends;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 依赖
 * @author CH
 */
@Documented
@Repeatable(Dependencies.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dependency {
    /**
     * The organisation or group, e.g.: "org.apache.ant". A non-empty value is required unless value() is used.
     */
    String group() default "";

    /**
     * The module or artifact, e.g.: "ant-junit". A non-empty value is required unless value() is used.
     */
    String module() default "";

    /**
     * The revision or version, e.g.: "1.7.1". A non-empty value is required unless value() is used.
     */
    String version() default "";

    /**
     * The classifier if in use, e.g.: "jdk14"
     */
    String classifier() default "";

    /**
     * Defaults to {@code true} but set to {@code false} if you don't want transitive dependencies also to be downloaded.
     * You may then need additional {@code @Grab} statements for any required dependencies.
     */
    boolean transitive() default true;

    /**
     * Defaults to {@code false} but set to {@code true} to indicate to the underlying Ivy conflict manager that this
     * dependency should be forced to the given revision. Otherwise, depending on the conflict manager in play, a later
     * compatible version might be used instead.
     */
    boolean force() default false;

    /**
     * Defaults to {@code false} but set to {@code true} if the dependency artifacts may change without a corresponding
     * revision change. Not normally recommended but may be useful for certain kinds of snapshot artifacts.
     * May reduce the amount of underlying Ivy caching. Proper behavior may be dependent on the resolver in use.
     */
    boolean changing() default false;

    /**
     * The configuration if in use (normally only used by internal ivy repositories).
     * One or more comma separated values with or without square brackets,
     * e.g.&#160;for hibernate you might have "default,proxool,oscache" or "[default,dbcp,swarmcache]".
     * This last hibernate example assumes you have set up such configurations in your local Ivy repo
     * and have changed your grape config (using grapeConfig.xml) or the {@code @GrabConfig} annotation
     * to point to that repo.
     */
    String conf() default "";

    /**
     * The extension of the artifact (normally safe to leave at default value of "jar" but other values like "zip"
     * are sometimes useful).
     */
    String ext() default "";

    /**
     * The type of the artifact (normally safe to leave at default value of "jar" but other values like "sources" and "javadoc" are sometimes useful).
     * But see also the "classifier" attribute which is also sometimes used for "sources" and "javadoc".
     */
    String type() default "";

    /**
     * Allows a more compact convenience form in one of two formats with optional appended attributes.
     * Must not be used if group(), module() or version() are used.
     * <p>
     * You can choose either format but not mix-n-match:<br>
     * {@code group:module:version:classifier@ext} (where only group and module are required)<br>
     * {@code group#module;version[confs]} (where only group and module are required and confs,
     * if used, is one or more comma separated configuration names)<br>
     * In addition, you can add any valid Ivy attributes at the end of your string value using
     * semi-colon separated name = value pairs, e.g.:<br>
     * {@code @Grab('junit:junit:*;transitive=false')}<br>
     * {@code @Grab('group=junit;module=junit;version=4.8.2;classifier=javadoc')}<br>
     */
    @AliasFor("artifact")
    String value() default "";

    @AliasFor("value")
    String artifact() default "";

    /**
     * By default, when a {@code @Grab} annotation is used, a {@code Grape.grab()} call is added
     * to the static initializers of the class the annotatable node appears in.
     * If you wish to disable this, add {@code initClass=false} to the annotation.
     */
    boolean initClass() default true;
}
