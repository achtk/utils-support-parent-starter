package com.chua.common.support.query;

import lombok.Getter;

/**
 * 操作
 * @author CH
 */
@Getter
public enum Operations {
    /**
     * =
     */
    EQUAL("="),
    /**
     * <>
     */
    NOT_EQUAL("<>"),
    /**
     * TO
     */
    TO("TO"),
    /**
     * !=
     */
    NOT_EQUAL_TO("!="),
    /**
     * >
     */
    GREATER_THAN(">"),
    /**
     * >=
     */
    GREATER_OR_EQUAL_THAN(">"),
    /**
     * <
     */
    LESS_THAN("<"),
    /**
     * <=
     */
    LESS_OR_EQUAL_THAN("<"),
    ;
    /**
     * 符号
     */
    private String symbol;

    Operations(String symbol) {
        this.symbol = symbol;
    }
}
