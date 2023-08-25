package com.chua.common.support.extra.el.expression.token;

import java.util.HashMap;
import java.util.Map;

public enum Symbol implements Token {
    /**
     * (
     */
    LEFT_PAREN("("),
    /**
     * )
     */
    RIGHT_PAREN(")"),
    /**
     * [
     */
    LEFT_BRACKET("["),
    /**
     * ]
     */
    RIGHT_BRACKET("]"),
    /**
     * ,
     */
    COMMA(",");

    private static final Map<String, Symbol> SYMBOLS = new HashMap<String, Symbol>(128);

    private final String literals;

    Symbol(String literals) {
        this.literals = literals;
    }

    /**
     * 通过字面量查找词法符号.
     *
     * @param literals 字面量
     * @return 词法符号
     */
    public static Symbol literalsOf(final String literals) {
        return SYMBOLS.get(literals);
    }

    public static boolean isSymbol(Token token) {
        return token instanceof Symbol;
    }

    public String getLiterals() {
        return literals;
    }
}
