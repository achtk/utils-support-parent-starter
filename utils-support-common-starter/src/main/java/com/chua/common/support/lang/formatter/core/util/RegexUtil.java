package com.chua.common.support.lang.formatter.core.util;

import com.chua.common.support.lang.formatter.languages.StringLiteral;

import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 * 基础类
 * @author CH
 */
public class RegexUtil {

  private static final String ESCAPE_REGEX =
      Stream.of("^", "$", "\\", ".", "*", "+", "*", "?", "(", ")", "[", "]", "{", "}", "|")
          .map(spChr -> "(\\" + spChr + ")")
          .collect(Collectors.joining("|"));
  public static final Pattern ESCAPE_REGEX_PATTERN = Pattern.compile(ESCAPE_REGEX);

  public static String escapeRegExp(String s) {
    return ESCAPE_REGEX_PATTERN.matcher(s).replaceAll("\\\\$0");
  }

  public static String createOperatorRegex(JsLikeList<String> multiLetterOperators) {
    return String.format(
        "^(%s|.)",
        Util.sortByLengthDesc(multiLetterOperators).map(RegexUtil::escapeRegExp).join("|"));
  }

  public static String createLineCommentRegex(JsLikeList<String> lineCommentTypes) {
    return String.format(
        "^((?:%s).*?)(?:\r\n|\r|\n|$)", lineCommentTypes.map(RegexUtil::escapeRegExp).join("|"));
  }

  public static String createReservedWordRegex(JsLikeList<String> reservedWords) {
    if (reservedWords.isEmpty()) {
      return "^\b$";
    }
    String reservedWordsPattern =
        Util.sortByLengthDesc(reservedWords).join("|").replaceAll(" ", "\\\\s+");
    return "(?i)" + "^(" + reservedWordsPattern + ")\\b";
  }

  public static String createWordRegex(JsLikeList<String> specialChars) {
    return "^([\\p{IsAlphabetic}\\p{Mc}\\p{Me}\\p{Mn}\\p{Nd}\\p{Pc}\\p{IsJoin_Control}"
        + specialChars.join("")
        + "]+)";
  }

  public static String createStringRegex(JsLikeList<String> stringTypes) {
    return "^(" + createStringPattern(stringTypes) + ")";
  }

  /** This enables the following string patterns:
  * 1. backtick quoted string using `` to escape
  * 2. square bracket quoted string (SQL Server) using ]] to escape
  * 3. double quoted string using "" or \" to escape
  * 4. single quoted string using '' or \' to escape
   * 5. national character quoted string using N'' or N\' to escape
  */
  public static String createStringPattern(JsLikeList<String> stringTypes) {
    return stringTypes.map(StringLiteral::get).join("|");
  }

  public static String createParenRegex(JsLikeList<String> parens) {
    return "(?i)^(" + parens.map(RegexUtil::escapeParen).join("|") + ")";
  }

  public static String escapeParen(String paren) {
    if (paren.length() == 1) {
      // A single punctuation character
      return RegexUtil.escapeRegExp(paren);
    } else {
      // longer word
      return "\\b" + paren + "\\b";
    }
  }

  public static Pattern createPlaceholderRegexPattern(JsLikeList<String> types, String pattern) {
    if (types.isEmpty()) {
      return null;
    }
    String typesRegex = types.map(RegexUtil::escapeRegExp).join("|");

    return Pattern.compile(String.format("^((?:%s)(?:%s))", typesRegex, pattern));
  }
}
