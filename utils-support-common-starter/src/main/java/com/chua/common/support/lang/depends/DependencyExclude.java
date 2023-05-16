package com.chua.common.support.lang.depends;

import java.lang.annotation.*;

/**
 * 依赖
 * @author CH
 */
@Documented
@Target(ElementType.CONSTRUCTOR)
@Repeatable(DependencyExcludeCollect.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface DependencyExclude {

    /**
     * The organisation or group, e.g.: "org.apache.ant"; required unless the compact form is used.
     */
    String group() default "";

    /**
     * The module or artifact, e.g.: "ant-junit"; required unless the compact form is used.
     */
    String module() default "";

    /**
     * Allows you to specify the group (organisation) and the module (artifact) in one of two compact convenience formats,
     * e.g.: <code>@GrabExclude('org.apache.ant:ant-junit')</code> or <code>@GrabExclude('org.apache.ant#ant-junit')</code>
     */
    String value() default "";
}
