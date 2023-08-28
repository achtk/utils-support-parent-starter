package com.chua.common.support.ansi;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Erase
 *
 * @author CH
 */
@AllArgsConstructor
@Getter
public enum AnsiErase implements AnsiElement {
    /**
     * FORWARD
     */
    FORWARD(0),
    /**
     * BACKWARD
     */
    BACKWARD(1),
    /**
     * ALL
     */
    ALL(2);

    private final int value;


    @Override
    public String toString() {
        return value + "";
    }

    @Override
    public int value() {
        return value;
    }
}
