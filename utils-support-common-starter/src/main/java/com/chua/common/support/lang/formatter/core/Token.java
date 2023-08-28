package com.chua.common.support.lang.formatter.core;

import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
/**
 * 基础类
 * @author CH
 */
public class Token {
  public final TokenTypes type;
  public final String value;
  public final String regex;
  public final String whitespaceBefore;
  public final String key;

  public Token(TokenTypes type, String value, String regex, String whitespaceBefore, String key) {
    this.type = type;
    this.value = value;
    this.regex = regex;
    this.whitespaceBefore = whitespaceBefore;
    this.key = key;
  }

  public Token(TokenTypes type, String value, String regex, String whitespaceBefore) {
    this(type, value, regex, whitespaceBefore, null);
  }

  public Token(TokenTypes type, String value, String regex) {
    this(type, value, regex, null);
  }

  public Token(TokenTypes type, String value) {
    this(type, value, null, null);
  }

  public Token withWhitespaceBefore(String whitespaceBefore) {
    return new Token(this.type, this.value, this.regex, whitespaceBefore, this.key);
  }

  public Token withKey(String key) {
    return new Token(this.type, this.value, this.regex, this.whitespaceBefore, key);
  }

  @Override
  public String toString() {
    return "type: " + type + ", value: [" + value + "], regex: /" + regex + "/" + ", key: " + key;
  }

  private static final Pattern AND =
      Pattern.compile("^AND$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
  private static final Pattern BETWEEN =
      Pattern.compile("^BETWEEN$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
  private static final Pattern LIMIT =
      Pattern.compile("^LIMIT$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
  private static final Pattern SET =
      Pattern.compile("^SET$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
  private static final Pattern BY =
      Pattern.compile("^BY$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
  private static final Pattern WINDOW =
      Pattern.compile("^WINDOW$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
  private static final Pattern END =
      Pattern.compile("^END$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

  private static Function<Token, Boolean> isToken(TokenTypes type, Pattern pattern) {
    return token -> token.type == type && pattern.matcher(token.value).matches();
  }

  public static boolean isAnd(Token token) {
    return isAnd(Optional.ofNullable(token));
  }

  public static boolean isAnd(Optional<Token> token) {
    return token.map(isToken(TokenTypes.RESERVED_NEWLINE, AND)).orElse(false);
  }

  public static boolean isBetween(Token token) {
    return isBetween(Optional.ofNullable(token));
  }

  public static boolean isBetween(Optional<Token> token) {
    return token.map(isToken(TokenTypes.RESERVED, BETWEEN)).orElse(false);
  }

  public static boolean isLimit(Token token) {
    return isLimit(Optional.ofNullable(token));
  }

  public static boolean isLimit(Optional<Token> token) {
    return token.map(isToken(TokenTypes.RESERVED_TOP_LEVEL, LIMIT)).orElse(false);
  }

  public static boolean isSet(Token token) {
    return isSet(Optional.ofNullable(token));
  }

  public static boolean isSet(Optional<Token> token) {
    return token.map(isToken(TokenTypes.RESERVED_TOP_LEVEL, SET)).orElse(false);
  }

  public static boolean isBy(Token token) {
    return isBy(Optional.ofNullable(token));
  }

  public static boolean isBy(Optional<Token> token) {
    return token.map(isToken(TokenTypes.RESERVED, BY)).orElse(false);
  }

  public static boolean isWindow(Token token) {
    return isWindow(Optional.ofNullable(token));
  }

  public static boolean isWindow(Optional<Token> token) {
    return token.map(isToken(TokenTypes.RESERVED_TOP_LEVEL, WINDOW)).orElse(false);
  }

  public static boolean isEnd(Token token) {
    return isEnd(Optional.ofNullable(token));
  }

  public static boolean isEnd(Optional<Token> token) {
    return token.map(isToken(TokenTypes.CLOSE_PAREN, END)).orElse(false);
  }
}
