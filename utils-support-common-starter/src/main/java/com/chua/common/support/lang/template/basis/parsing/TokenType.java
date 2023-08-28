
package com.chua.common.support.lang.template.basis.parsing;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Enumeration of token types. A token type consists of a representation for error messages, and may optionally specify a literal
 * to be used by the {@link CharacterStream} to recognize the token. Token types are sorted by their literal length to easy
 * matching of token types with common prefixes, e.g. "<" and "<=". Token types with longer literals are matched first.
 * @author Administrator
 */
public enum TokenType {
    
    TextBlock("a text block"),
    Period(".", "."),
    Comma(",", ","),
    Semicolon(";", ";"),
    Colon(":", ":"),
    Plus("+", "+"),
    Minus("-", "-"),
    Asterisk("*", "*"),
    ForwardSlash("/", "/"),
    Percentage("%", "%"),
    LeftParantheses("(", ")"),
    RightParantheses(")", ")"),
    LeftBracket("[", "["),
    RightBracket("]", "]"),
    LeftCurly("{", "{"),
    RightCurly("}"), 
    Less("<", "<"),
    Greater(">", ">"),
    LessEqual("<=", "<="),
    GreaterEqual(">=", ">="),
    Equal("==", "=="),
    NotEqual("!=", "!="),
    Assignment("=", "="),
    And("&&", "&&"),
    Or("||", "||"),
    Xor("^", "^"),
    Not("!", "!"),
    Questionmark("?", "?"),
    DoubleQuote("\"", "\""),
    Backtick("`", "`"),
    BooleanLiteral("true or false"),
    DoubleLiteral("a double floating point number"),
    FloatLiteral("a floating point number"),
    LongLiteral("a long integer number"),
    IntegerLiteral("an integer number"),
    ShortLiteral("a short integer number"),
    ByteLiteral("a byte integer number"),
    CharacterLiteral("a character"),
    StringLiteral("a string"),
    RawStringLiteral("a string"),
    NullLiteral("null"),
    Identifier("an identifier");
    

    private static TokenType[] values;

    static {
        
        
        values = TokenType.values();
        Arrays.sort(values, new Comparator<TokenType>() {
            @Override
            public int compare(TokenType o1, TokenType o2) {
                if (o1.literal == null && o2.literal == null) {
                    return 0;
                }
                if (o1.literal == null && o2.literal != null) {
                    return 1;
                }
                if (o1.literal != null && o2.literal == null) {
                    return -1;
                }
                return o2.literal.length() - o1.literal.length();
            }
        });
    }

    private final String literal;
    private final String error;

    TokenType(String error) {
        this.literal = null;
        this.error = error;
    }

    TokenType(String literal, String error) {
        this.literal = literal;
        this.error = error;
    }

    /**
     * The literal to match, may be null.
     **/
    public String getLiteral() {
        return literal;
    }

    /**
     * The error string to use when reporting this token type in an error message.
     **/
    public String getError() {
        return error;
    }

    /**
     * Returns an array of token types, sorted in descending order based on their literal length. This is used by the
     * {@link CharacterStream} to match token types with the longest literal first. E.g. "<=" will be matched before "<".
     **/
    public static TokenType[] getSortedValues() {
        return values;
    }
}
