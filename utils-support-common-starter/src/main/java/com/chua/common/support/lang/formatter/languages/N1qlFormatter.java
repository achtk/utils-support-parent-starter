package com.chua.common.support.lang.formatter.languages;

import com.chua.common.support.lang.formatter.core.AbstractFormatter;
import com.chua.common.support.lang.formatter.core.DialectConfig;
import com.chua.common.support.lang.formatter.core.FormatConfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/**
 * 基础类
 * @author CH
 */
public class N1qlFormatter extends AbstractFormatter {

  private static final List<String> RESERVED_WORDS =
      Arrays.asList(
          "ALL",
          "ALTER",
          "ANALYZE",
          "AND",
          "ANY",
          "ARRAY",
          "AS",
          "ASC",
          "BEGIN",
          "BETWEEN",
          "BINARY",
          "BOOLEAN",
          "BREAK",
          "BUCKET",
          "BUILD",
          "BY",
          "CALL",
          "CASE",
          "CAST",
          "CLUSTER",
          "COLLATE",
          "COLLECTION",
          "COMMIT",
          "CONNECT",
          "CONTINUE",
          "CORRELATE",
          "COVER",
          "CREATE",
          "DATABASE",
          "DATASET",
          "DATASTORE",
          "DECLARE",
          "DECREMENT",
          "DELETE",
          "DERIVED",
          "DESC",
          "DESCRIBE",
          "DISTINCT",
          "DO",
          "DROP",
          "EACH",
          "ELEMENT",
          "ELSE",
          "END",
          "EVERY",
          "EXCEPT",
          "EXCLUDE",
          "EXECUTE",
          "EXISTS",
          "EXPLAIN",
          "FALSE",
          "FETCH",
          "FIRST",
          "FLATTEN",
          "FOR",
          "FORCE",
          "FROM",
          "FUNCTION",
          "GRANT",
          "GROUP",
          "GSI",
          "HAVING",
          "IF",
          "IGNORE",
          "ILIKE",
          "IN",
          "INCLUDE",
          "INCREMENT",
          "INDEX",
          "INFER",
          "INLINE",
          "INNER",
          "INSERT",
          "INTERSECT",
          "INTO",
          "IS",
          "JOIN",
          "KEY",
          "KEYS",
          "KEYSPACE",
          "KNOWN",
          "LAST",
          "LEFT",
          "LET",
          "LETTING",
          "LIKE",
          "LIMIT",
          "LSM",
          "MAP",
          "MAPPING",
          "MATCHED",
          "MATERIALIZED",
          "MERGE",
          "MISSING",
          "NAMESPACE",
          "NEST",
          "NOT",
          "NULL",
          "NUMBER",
          "OBJECT",
          "OFFSET",
          "ON",
          "OPTION",
          "OR",
          "ORDER",
          "OUTER",
          "OVER",
          "PARSE",
          "PARTITION",
          "PASSWORD",
          "PATH",
          "POOL",
          "PREPARE",
          "PRIMARY",
          "PRIVATE",
          "PRIVILEGE",
          "PROCEDURE",
          "PUBLIC",
          "RAW",
          "REALM",
          "REDUCE",
          "RENAME",
          "RETURN",
          "RETURNING",
          "REVOKE",
          "RIGHT",
          "ROLE",
          "ROLLBACK",
          "SATISFIES",
          "SCHEMA",
          "SELECT",
          "SELF",
          "SEMI",
          "SET",
          "SHOW",
          "SOME",
          "START",
          "STATISTICS",
          "STRING",
          "SYSTEM",
          "THEN",
          "TO",
          "TRANSACTION",
          "TRIGGER",
          "TRUE",
          "TRUNCATE",
          "UNDER",
          "UNION",
          "UNIQUE",
          "UNKNOWN",
          "UNNEST",
          "UNSET",
          "UPDATE",
          "UPSERT",
          "USE",
          "USER",
          "USING",
          "VALIDATE",
          "VALUE",
          "VALUED",
          "VALUES",
          "VIA",
          "VIEW",
          "WHEN",
          "WHERE",
          "WHILE",
          "WITH",
          "WITHIN",
          "WORK",
          "XOR");

  private static final List<String> RESERVED_TOP_LEVEL_WORDS =
      Arrays.asList(
          "DELETE FROM",
          "EXCEPT ALL",
          "EXCEPT",
          "EXPLAIN DELETE FROM",
          "EXPLAIN UPDATE",
          "EXPLAIN UPSERT",
          "FROM",
          "GROUP BY",
          "HAVING",
          "INFER",
          "INSERT INTO",
          "LET",
          "LIMIT",
          "MERGE",
          "NEST",
          "ORDER BY",
          "PREPARE",
          "SELECT",
          "SET CURRENT SCHEMA",
          "SET SCHEMA",
          "SET",
          "UNNEST",
          "UPDATE",
          "UPSERT",
          "USE KEYS",
          "VALUES",
          "WHERE");

  private static final List<String> RESERVED_TOP_LEVEL_WORDS_NO_INDENT =
      Arrays.asList("INTERSECT", "INTERSECT ALL", "MINUS", "UNION", "UNION ALL");

  private static final List<String> RESERVED_NEWLINE_WORDS =
      Arrays.asList(
          "AND",
          "OR",
          "XOR",
          // joins
          "JOIN",
          "INNER JOIN",
          "LEFT JOIN",
          "LEFT OUTER JOIN",
          "RIGHT JOIN",
          "RIGHT OUTER JOIN");

  @Override
  public DialectConfig dialectConfig() {
    return DialectConfig.builder()
        .reservedWords(RESERVED_WORDS)
        .reservedTopLevelWords(RESERVED_TOP_LEVEL_WORDS)
        .reservedTopLevelWordsNoIndent(RESERVED_TOP_LEVEL_WORDS_NO_INDENT)
        .reservedNewlineWords(RESERVED_NEWLINE_WORDS)
        .stringTypes(
            Arrays.asList(
                StringLiteral.DOUBLE_QUOTE, StringLiteral.SINGLE_QUOTE, StringLiteral.BACK_QUOTE))
        .openParens(Arrays.asList("(", "[", "{"))
        .closeParens(Arrays.asList(")", "]", "}"))
        .namedPlaceholderTypes(Collections.singletonList("$"))
        .lineCommentTypes(Arrays.asList("#", "--"))
        .operators(Arrays.asList("==", "!="))
        .build();
  }

  public N1qlFormatter(FormatConfig cfg) {
    super(cfg);
  }
}
