package com.chua.common.support.lang.spider.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Define the extractor for field or class.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface ExtractBy {

    /**
     * Extractor expression, support XPath, CSS Selector and regex.
     *
     * @return extractor expression
     */
    String value();

    /**
     * types of extractor expressions
     */
    enum Type {
        /**
         * xpath
         */
        X_PATH,
        /**
         * regex
         */
        REGEX,
        /**
         * Css
         */
        CSS,
        /**
         * JsonPath
         */
        JSON_PATH
    }

    /**
     * Extractor type, support XPath, CSS Selector and regex.
     *
     * @return extractor type
     */
    Type type() default Type.X_PATH;

    /**
     * Define whether the field can be null.<br>
     * If set to 'true' and the extractor get no result, the entire class will be discarded. <br>
     *
     * @return whether the field can be null
     */
    boolean notNull() default false;

    /**
     * types of source for extracting.
     */
    public static enum Source {
        /**
         * extract from the content extracted by class extractor
         */
        SelectedHtml,
        /**
         * extract from the raw html
         */
        RawHtml,
        RawText
    }

    /**
     * The source for extracting. <br>
     * It works only if you already added 'ExtractBy' to Class. <br>
     *
     * @return the source for extracting
     */
    Source source() default Source.SelectedHtml;

    /**
     * Define whether the extractor return more than one result.
     * When set to 'true', the extractor return a list of string (so you should define the field as List). <br>
     * <p>
     *
     * @return whether the extractor return more than one result
     */
    boolean multi() default false;

}
