package com.chua.common.support.printer;

import lombok.Getter;

/**
 * 打印器
 *
 * @author CH
 */
public interface Printer<E> {

    /**
     * 打印
     *
     * @param e    元素
     * @param type 类型
     * @return 输出
     */
    String print(E e, Type type);

    /**
     * 打印
     *
     * @param e 元素
     * @return 输出
     */
    default String print(E e) {
        return print(e, Type.SLF4j);
    }

    /**
     * 打印
     *
     * @param e 元素
     * @return 输出
     */
    default String println(E e) {
        return print(e, Type.SYSTEM);
    }


    @Getter
    public static enum Type {
        /**
         * system
         */
        SYSTEM,
        /**
         * slf4j
         */
        SLF4j,
        /**
         * debug
         */
        SLF4J_DEBUG,
        /**
         * trace
         */
        SLF4J_TRACE,
        /**
         * 输出
         */
        OUT
    }
}
