package com.chua.common.support.ansi;


/**
 * An ANSI encodable element.
 *
 * @author Phillip Webb
 * @since 1.0.0
 */
public interface AnsiElement {

    /**
     * the ANSI escape code
     * @return the ANSI escape code
     */
    @Override
    String toString();

    /**
     * value
     *
     * @return value
     */
    int value();

    /**
     * 前景色
     *
     * @return 前景色
     */
    default int fg() {
        return value() + 30;
    }

    /**
     * 背景色
     *
     * @return 背景色
     */
    default int bg() {
        return value() + 40;
    }

    /**
     * 前景亮色
     *
     * @return 前景亮色
     */
    default int fgBright() {
        return value() + 90;
    }

    /**
     * 背景亮色
     *
     * @return 背景亮色
     */
    default int bgBright() {
        return value() + 100;
    }

}
