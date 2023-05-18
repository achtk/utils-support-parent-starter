package com.chua.common.support.crawler.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;

/**
 * css选择器
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/21
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, FIELD})
@Documented
public @interface XpathQuery {

    /**
     * CSS-like query, like "#body"
     * <p>
     * CSS选择器, 如 "#body"
     *
     * @return String
     */
    String value() default "";


    /**
     * data patttern, valid when date data
     * <p>
     * 时间格式化，日期类型数据有效
     *
     * @return String
     */
    String datePattern() default "yyyy-MM-dd HH:mm:ss";


}
