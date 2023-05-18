package com.chua.common.support.mock;

import java.lang.annotation.*;

/**
 * mock
 * @author CH
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Mock {
    /**
     * 数据类型
     *
     * @return 类型
     */
    Type[] value() default Type.RANDOM;

    /**
     * 符号
     * @return 符号
     */
    Symbol symbol() default Symbol.NONE;

    /**
     * 基础数据
     * @return 基础数据
     */
    String base() default "";

    /**
     * 数据格式
     * @return 数据格式
     */
    String formatter() default "";

    /**
     * 符号
     */
    enum Symbol {
        /**
         * 无效
         */
        NONE,
        /**
         * 之后
         */
        AFTER,
        /**
         * 之前
         */
        BEFORE
    }
    /**
     * 类型
     */
    enum Type {
        /**
         * 随机
         */
        RANDOM,
        /**
         * 身份证
         */
        CERT,
        /**
         * 手机号
         */
        PHONE,
        /**
         * 位置
         */
        LOCATION,
        /**
         * 大学
         */
        COLLEGE,
        /**
         * private-ipv4
         */
        PRIVATE_IPV4,
        /**
         * ipv4
         */
        IPV4,
        /**
         * ipv6
         */
        IPV6,
        /**
         * 生日
         */
        BIRTHDAY,
        /**
         * 年龄
         */
        AGE,
        /**
         * 民族
         */
        NATIONALITY,
        /**
         * 维度
         */
        LATITUDE,
        /**
         * 经度
         */
        LONGITUDE,
        /**
         * 姓名拼音
         */
        NAME_PINYIN,
        /**
         * UUID
         */
        UUID,
        /**
         * 社会信用代码
         */
        SOCIAL_CREDIT,
        /**
         * 端口
         */
        PORT,
        /**
         * mac
         */
        MAC,
        /**
         * 车牌
         */
        PLATE,
        /**
         * 性别
         */
        SEX,
        /**
         * url
         */
        URL,
        /**
         * number
         */
        NUMBER,
        /**
         * hanyu
         */
        HANYU,
        /**
         * 汉语译文
         */
        HANYU_YIWEN,
        /**
         * url like
         */
        URL_LIKE,
        /**
         * color
         */
        COLOR,
        /**
         * 姓名
         */
        NAME,
        /**
         * 邮箱
         */
        MAIL,
        /**
         * 邮箱后缀
         */
        MAIL_SUFFIX,
        /**
         * 教育程度
         */
        DEGREE,
        /**
         * 日期
         */
        DATE,
        /**
         * 时间
         */
        TIME,
        /**
         * 年
         */
        YEAR,
        /**
         * 日期时间
         */
        DATETIME,
        /**
         * 城市
         */
        CITY,
        /**
         * 城市编码
         */
        CITY_CODE
    }
}
