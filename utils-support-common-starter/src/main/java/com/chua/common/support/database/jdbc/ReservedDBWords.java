/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.chua.common.support.database.jdbc;

import java.util.HashMap;
import java.util.Map;

/**
 * Collect reserved words of all databases, keywords not included
 *
 * @author Yong Zhu
 * @since 1.0.1
 */
@SuppressWarnings("all")
public class ReservedDBWords {
    private static final String[] RESERV_WD = {"#ANSI-SQL", "ABSOLUTE", "ACTION", "ADD", "AFTER", "ALL", "ALLOCATE",
            "ALTER", "AND", "ANY", "ARE", "ARRAY", "AS", "ASC", "ASENSITIVE", "ASSERTION", "ASYMMETRIC", "AT", "ATOMIC",
            "AUTHORIZATION", "AVG", "BEFORE", "BEGIN", "BETWEEN", "BIGINT", "BINARY", "BIT", "BIT_LENGTH", "BLOB",
            "BOOLEAN", "BOTH", "BREADTH", "BY", "CALL", "CALLED", "CASCADE", "CASCADED", "CASE", "CAST", "CATALOG",
            "CHAR", "CHAR_LENGTH", "CHARACTER", "CHARACTER_LENGTH", "CHECK", "CLOB", "CLOSE", "COALESCE", "COLLATE",
            "COLLATION", "COLUMN", "COMMIT", "CONDITION", "CONNECT", "CONNECTION", "CONSTRAINT", "CONSTRAINTS",
            "CONSTRUCTOR", "CONTAINS", "CONTINUE", "CONVERT", "CORRESPONDING", "COUNT", "CREATE", "CROSS", "CUBE",
            "CURRENT", "CURRENT_DATE", "CURRENT_DEFAULT_TRANSFORM_GROUP", "CURRENT_PATH", "CURRENT_ROLE",
            "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_TRANSFORM_GROUP_FOR_TYPE", "CURRENT_USER", "CURSOR", "CYCLE",
            "DATA", "DATE", "DAY", "DEALLOCATE", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DEFERRABLE", "DEFERRED",
            "DELETE", "DEPTH", "DEREF", "DESC", "DESCRIBE", "DESCRIPTOR", "DETERMINISTIC", "DIAGNOSTICS", "DISCONNECT",
            "DISTINCT", "DO", "DOMAIN", "DOUBLE", "DROP", "DYNAMIC", "EACH", "ELEMENT", "ELSE", "ELSEIF", "END",
            "EQUALS", "ESCAPE", "EXCEPT", "EXCEPTION", "EXEC", "EXECUTE", "EXISTS", "EXIT", "EXTERNAL", "EXTRACT",
            "FETCH", "FILTER", "FIRST", "FLOAT", "FOR", "FOREIGN", "FOUND", "FREE", "FROM", "FULL", "FUNCTION",
            "GENERAL", "GET", "GLOBAL", "GO", "GOTO", "GRANT", "GROUP", "GROUPING", "HANDLER", "HAVING", "HOLD", "HOUR",
            "IDENTITY", "IF", "IMMEDIATE", "IN", "INDICATOR", "INITIALLY", "INNER", "INOUT", "INPUT", "INSENSITIVE",
            "INSERT", "INT", "INTEGER", "INTERSECT", "INTERVAL", "INTO", "IS", "ISOLATION", "ITERATE", "JOIN", "KEY",
            "LANGUAGE", "LARGE", "LAST", "LATERAL", "LEADING", "LEAVE", "LEFT", "LEVEL", "LIKE", "LOCAL", "LOCALTIME",
            "LOCALTIMESTAMP", "LOCATOR", "LOOP", "LOWER", "MAP", "MATCH", "MAX", "MEMBER", "MERGE", "METHOD", "MIN",
            "MINUTE", "MODIFIES", "MODULE", "MONTH", "MULTISET", "NAMES", "NATIONAL", "NATURAL", "NCHAR", "NCLOB",
            "NEW", "NEXT", "NO", "NONE", "NOT", "NULL", "NULLIF", "NUMERIC", "OBJECT", "OCTET_LENGTH", "OF", "OLD",
            "ON", "ONLY", "OPEN", "OPTION", "OR", "ORDER", "ORDINALITY", "OUT", "OUTER", "OUTPUT", "OVER", "OVERLAPS",
            "PAD", "PARAMETER", "PARTIAL", "PARTITION", "PATH", "POSITION", "PRECISION", "PREPARE", "PRESERVE",
            "PRIMARY", "PRIOR", "PRIVILEGES", "PROCEDURE", "PUBLIC", "RANGE", "READ", "READS", "REAL", "RECURSIVE",
            "REF", "REFERENCES", "REFERENCING", "RELATIVE", "RELEASE", "REPEAT", "RESIGNAL", "RESTRICT", "RESULT",
            "RETURN", "RETURNS", "REVOKE", "RIGHT", "ROLE", "ROLLBACK", "ROLLUP", "ROUTINE", "ROW", "ROWS", "SAVEPOINT",
            "SCHEMA", "SCOPE", "SCROLL", "SEARCH", "SECOND", "SECTION", "SELECT", "SENSITIVE", "SESSION",
            "SESSION_USER", "SET", "SETS", "SIGNAL", "SIMILAR", "SIZE", "SMALLINT", "SOME", "SPACE", "SPECIFIC",
            "SPECIFICTYPE", "SQL", "SQLCODE", "SQLERROR", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "START", "STATE",
            "STATIC", "SUBMULTISET", "SUBSTRING", "SUM", "SYMMETRIC", "SYSTEM", "SYSTEM_USER", "TABLE", "TABLESAMPLE",
            "TEMPORARY", "THEN", "TIME", "TIMESTAMP", "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TO", "TRAILING",
            "TRANSACTION", "TRANSLATE", "TRANSLATION", "TREAT", "TRIGGER", "TRIM", "UNDER", "UNDO", "UNION", "UNIQUE",
            "UNKNOWN", "UNNEST", "UNTIL", "UPDATE", "UPPER", "USAGE", "USER", "USING", "VALUE", "VALUES", "VARCHAR",
            "VARYING", "VIEW", "WHEN", "WHENEVER", "WHERE", "WHILE", "WINDOW", "WITH", "WITHIN", "WITHOUT", "WORK",
            "WRITE", "YEAR", "ZONE", "FALSE", "TRUE", "#MySQL", "ACCESSIBLE", "ADD", "ALL", "ALTER", "ANALYZE", "AND",
            "AS", "ASC", "ASENSITIVE", "BEFORE", "BETWEEN", "BIGINT", "BINARY", "BLOB", "BOTH", "BY", "CALL", "CASCADE",
            "CASE", "CHANGE", "CHAR", "CHARACTER", "CHECK", "COLLATE", "COLUMN", "CONDITION", "CONSTRAINT", "CONTINUE",
            "CONVERT", "CREATE", "CROSS", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR",
            "DATABASE", "DATABASES", "DAY_HOUR", "DAY_MICROSECOND", "DAY_MINUTE", "DAY_SECOND", "DEC", "DECIMAL",
            "DECLARE", "DEFAULT", "DELAYED", "DELETE", "DESC", "DESCRIBE", "DETERMINISTIC", "DISTINCT", "DISTINCTROW",
            "DIV", "DOUBLE", "DROP", "DUAL", "EACH", "ELSE", "ELSEIF", "ENCLOSED", "ESCAPED", "EXISTS", "EXIT",
            "EXPLAIN", "FALSE", "FETCH", "FLOAT", "FLOAT4", "FLOAT8", "FOR", "FORCE", "FOREIGN", "FROM", "FULLTEXT",
            "GENERATED[i]", "GET", "GRANT", "GROUP", "HAVING", "HIGH_PRIORITY", "HOUR_MICROSECOND", "HOUR_MINUTE",
            "HOUR_SECOND", "IF", "IGNORE", "IN", "INDEX", "INFILE", "INNER", "INOUT", "INSENSITIVE", "INSERT", "INT",
            "INT1", "INT2", "INT3", "INT4", "INT8", "INTEGER", "INTERVAL", "INTO", "IO_AFTER_GTIDS", "IO_BEFORE_GTIDS",
            "IS", "ITERATE", "JOIN", "KEY", "KEYS", "KILL", "LEADING", "LEAVE", "LEFT", "LIKE", "LIMIT", "LINEAR",
            "LINES", "LOAD", "LOCALTIME", "LOCALTIMESTAMP", "LOCK", "LONG", "LONGBLOB", "LONGTEXT", "LOOP",
            "LOW_PRIORITY", "MASTER_BIND", "MASTER_SSL_VERIFY_SERVER_CERT", "MATCH", "MAXVALUE", "MEDIUMBLOB",
            "MEDIUMINT", "MEDIUMTEXT", "MIDDLEINT", "MINUTE_MICROSECOND", "MINUTE_SECOND", "MOD", "MODIFIES", "NATURAL",
            "NOT", "NO_WRITE_TO_BINLOG", "NULL", "NUMERIC", "ON", "OPTIMIZE", "OPTIMIZER_COSTS[r]", "OPTION",
            "OPTIONALLY", "OR", "ORDER", "OUT", "OUTER", "OUTFILE", "PARTITION", "PRECISION", "PRIMARY", "PROCEDURE",
            "PURGE", "RANGE", "READ", "READS", "READ_WRITE", "REAL", "REFERENCES", "REGEXP", "RELEASE", "RENAME",
            "REPEAT", "REPLACE", "REQUIRE", "RESIGNAL", "RESTRICT", "RETURN", "REVOKE", "RIGHT", "RLIKE", "SCHEMA",
            "SCHEMAS", "SECOND_MICROSECOND", "SELECT", "SENSITIVE", "SEPARATOR", "SET", "SHOW", "SIGNAL", "SMALLINT",
            "SPATIAL", "SPECIFIC", "SQL", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "SQL_BIG_RESULT",
            "SQL_CALC_FOUND_ROWS", "SQL_SMALL_RESULT", "SSL", "STARTING", "STORED[ac]", "STRAIGHT_JOIN", "TABLE",
            "TERMINATED", "THEN", "TINYBLOB", "TINYINT", "TINYTEXT", "TO", "TRAILING", "TRIGGER", "TRUE", "UNDO",
            "UNION", "UNIQUE", "UNLOCK", "UNSIGNED", "UPDATE", "USAGE", "USE", "USING", "UTC_DATE", "UTC_TIME",
            "UTC_TIMESTAMP", "VALUES", "VARBINARY", "VARCHAR", "VARCHARACTER", "VARYING", "VIRTUAL[ae]", "WHEN",
            "WHERE", "WHILE", "WITH", "WRITE", "XOR", "YEAR_MONTH", "ZEROFILL", "#SQLServer", "ADD", "ALL", "ALTER",
            "AND", "ANY", "AS", "ASC", "AUTHORIZATION", "BACKUP", "BEGIN", "BETWEEN", "BREAK", "BROWSE", "BULK", "BY",
            "CASCADE", "CASE", "CHECK", "CHECKPOINT", "CLOSE", "CLUSTERED", "COALESCE", "COLLATE", "COLUMN", "COMMIT",
            "COMPUTE", "CONSTRAINT", "CONTAINS", "CONTAINSTABLE", "CONTINUE", "CONVERT", "CREATE", "CROSS", "CURRENT",
            "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "DATABASE", "DBCC",
            "DEALLOCATE", "DECLARE", "DEFAULT", "DELETE", "DENY", "DESC", "DISK", "DISTINCT", "DISTRIBUTED", "DOUBLE",
            "DROP", "DUMP", "ELSE", "END", "ERRLVL", "ESCAPE", "EXCEPT", "EXEC", "EXECUTE", "EXISTS", "EXIT",
            "EXTERNAL", "FETCH", "FILE", "FILLFACTOR", "FOR", "FOREIGN", "FREETEXT", "FREETEXTTABLE", "FROM", "FULL",
            "FUNCTION", "GOTO", "GRANT", "GROUP", "HAVING", "HOLDLOCK", "IDENTITY", "IDENTITY_INSERT", "IDENTITYCOL",
            "IF", "IN", "INDEX", "INNER", "INSERT", "INTERSECT", "INTO", "IS", "JOIN", "KEY", "KILL", "LEFT", "LIKE",
            "LINENO", "LOAD", "MERGE", "NATIONAL", "NOCHECK", "NONCLUSTERED", "NOT", "NULL", "NULLIF", "OF", "OFF",
            "OFFSETS", "ON", "OPEN", "OPENDATASOURCE", "OPENQUERY", "OPENROWSET", "OPENXML", "OPTION", "OR", "ORDER",
            "OUTER", "OVER", "PERCENT", "PIVOT", "PLAN", "PRECISION", "PRIMARY", "PRINT", "PROC", "PROCEDURE", "PUBLIC",
            "RAISERROR", "READ", "READTEXT", "RECONFIGURE", "REFERENCES", "REPLICATION", "RESTORE", "RESTRICT",
            "RETURN", "REVERT", "REVOKE", "RIGHT", "ROLLBACK", "ROWCOUNT", "ROWGUIDCOL", "RULE", "SAVE", "SCHEMA",
            "SECURITYAUDIT", "SELECT", "SEMANTICKEYPHRASETABLE", "SEMANTICSIMILARITYDETAILSTABLE",
            "SEMANTICSIMILARITYTABLE", "SESSION_USER", "SET", "SETUSER", "SHUTDOWN", "SOME", "STATISTICS",
            "SYSTEM_USER", "TABLE", "TABLESAMPLE", "TEXTSIZE", "THEN", "TO", "TOP", "TRAN", "TRANSACTION", "TRIGGER",
            "TRUNCATE", "TRY_CONVERT", "TSEQUAL", "UNION", "UNIQUE", "UNPIVOT", "UPDATE", "UPDATETEXT", "USE", "USER",
            "VALUES", "VARYING", "VIEW", "WAITFOR", "WHEN", "WHERE", "WHILE", "WITH", "WITHINGROUP", "WRITETEXT",
            "#Oracle", "ACCESS", "ADD", "ALL", "ALTER", "AND", "ANY", "ARRAYLEN", "AS", "ASC", "AUDIT", "BETWEEN", "BY",
            "CHAR", "CHECK", "CLUSTER", "COLUMN", "COMMENT", "COMPRESS", "CONNECT", "CREATE", "CURRENT", "DATE",
            "DECIMAL", "DEFAULT", "DELETE", "DESC", "DISTINCT", "DROP", "ELSE", "EXCLUSIVE", "EXISTS", "FILE", "FLOAT",
            "FOR", "FROM", "GRANT", "GROUP", "HAVING", "IDENTIFIED", "IMMEDIATE", "IN", "INCREMENT", "INDEX", "INITIAL",
            "INSERT", "INTEGER", "INTERSECT", "INTO", "IS", "LEVEL", "LIKE", "LOCK", "LONG", "MAXEXTENTS", "MINUS",
            "MODE", "MODIFY", "NOAUDIT", "NOCOMPRESS", "NOT", "NOTFOUND", "NOWAIT", "NULL", "NUMBER", "OF", "OFFLINE",
            "ON", "ONLINE", "OPTION", "OR", "ORDER", "PCTFREE", "PRIOR", "PRIVILEGES", "PUBLIC", "RAW", "RENAME",
            "RESOURCE", "REVOKE", "ROW", "ROWID", "ROWLABEL", "ROWNUM", "ROWS", "SELECT", "SESSION", "SET", "SHARE",
            "SIZE", "SMALLINT", "SQLBUF", "START", "SUCCESSFUL", "SYNONYM", "SYSDATE", "TABLE", "THEN", "TO", "TRIGGER",
            "UID", "UNION", "UNIQUE", "UPDATE", "USER", "VALIDATE", "VALUES", "VARCHAR", "VARCHAR2", "VIEW", "WHENEVER",
            "WHERE", "WITH", "#PostgreSQL", "ALL", "ANALYSE", "ANALYZE", "AND", "ANY", "AS", "ASC", "AUTHORIZATION",
            "BETWEEN", "BINARY", "BOTH", "CASE", "CAST", "CHECK", "COLLATE", "COLUMN", "CONSTRAINT", "CREATE", "CROSS",
            "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "DEFAULT", "DEFERRABLE", "DESC",
            "DISTINCT", "DO", "ELSE", "END", "EXCEPT", "FOR", "FOREIGN", "FREEZE", "FROM", "FULL", "GRANT", "GROUP",
            "HAVING", "ILIKE", "IN", "INITIALLY", "INNER", "INTERSECT", "INTO", "IS", "ISNULL", "JOIN", "LEADING",
            "LEFT", "LIKE", "LIMIT", "LOCALTIME", "LOCALTIMESTAMP", "NATURAL", "NEW", "NOT", "NOTNULL", "NULL", "OFF",
            "OFFSET", "OLD", "ON", "ONLY", "OR", "ORDER", "OUTER", "OVERLAPS", "PLACING", "PRIMARY", "REFERENCES",
            "RIGHT", "SELECT", "SESSION_USER", "SIMILAR", "SOME", "TABLE", "THEN", "TO", "TRAILING", "UNION", "UNIQUE",
            "USER", "USING", "VERBOSE", "WHEN", "WHERE", "FALSE", "TRUE", "#DB2", "ACTIVATE", "ADD", "AFTER", "ALIAS",
            "ALL", "ALLOCATE", "ALLOW", "ALTER", "AND", "ANY", "AS", "ASENSITIVE", "ASSOCIATE", "ASUTIME", "AT",
            "ATTRIBUTES", "AUDIT", "AUTHORIZATION", "AUX", "AUXILIARY", "BEFORE", "BEGIN", "BETWEEN", "BINARY",
            "BUFFERPOOL", "BY", "CACHE", "CALL", "CALLED", "CAPTURE", "CARDINALITY", "CASCADED", "CASE", "CAST",
            "CCSID", "CHAR", "CHARACTER", "CHECK", "CLONE", "CLOSE", "CLUSTER", "COLLECTION", "COLLID", "COLUMN",
            "COMMENT", "COMMIT", "CONCAT", "CONDITION", "CONNECT", "CONNECTION", "CONSTRAINT", "CONTAINS", "CONTINUE",
            "COUNT", "COUNT_BIG", "CREATE", "CROSS", "CURRENT", "CURRENT_DATE", "CURRENT_LC_CTYPE", "CURRENT_PATH",
            "CURRENT_SCHEMA", "CURRENT_SERVER", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_TIMEZONE", "CURRENT_USER",
            "CURSOR", "CYCLE", "DATA", "DATABASE", "DATAPARTITIONNAME", "DATAPARTITIONNUM", "DATE", "DAY", "DAYS",
            "DB2GENERAL", "DB2GENRL", "DB2SQL", "DBINFO", "DBPARTITIONNAME", "DBPARTITIONNUM", "DEALLOCATE", "DECLARE",
            "DEFAULT", "DEFAULTS", "DEFINITION", "DELETE", "DENSE_RANK", "DENSERANK", "DESCRIBE", "DESCRIPTOR",
            "DETERMINISTIC", "DIAGNOSTICS", "DISABLE", "DISALLOW", "DISCONNECT", "DISTINCT", "DO", "DOCUMENT", "DOUBLE",
            "DROP", "DSSIZE", "DYNAMIC", "EACH", "EDITPROC", "ELSE", "ELSEIF", "ENABLE", "ENCODING", "ENCRYPTION",
            "END", "END-EXEC", "ENDING", "ERASE", "ESCAPE", "EVERY", "EXCEPT", "EXCEPTION", "EXCLUDING", "EXCLUSIVE",
            "EXECUTE", "EXISTS", "EXIT", "EXPLAIN", "EXTENDED", "EXTERNAL", "EXTRACT", "FENCED", "FETCH", "FIELDPROC",
            "FILE", "FINAL", "FOR", "FOREIGN", "FREE", "FROM", "FULL", "FUNCTION", "GENERAL", "GENERATED", "GET",
            "GLOBAL", "GO", "GOTO", "GRANT", "GRAPHIC", "GROUP", "HANDLER", "HASH", "HASHED_VALUE", "HAVING", "HINT",
            "HOLD", "HOUR", "HOURS", "IDENTITY", "IF", "IMMEDIATE", "IN", "INCLUDING", "INCLUSIVE", "INCREMENT",
            "INDEX", "INDICATOR", "INDICATORS", "INF", "INFINITY", "INHERIT", "INNER", "INOUT", "INSENSITIVE", "INSERT",
            "INTEGRITY", "INTERSECT", "INTO", "IS", "ISOBID", "ISOLATION", "ITERATE", "JAR", "JAVA", "JOIN", "KEEP",
            "KEY", "LABEL", "LANGUAGE", "LATERAL", "LC_CTYPE", "LEAVE", "LEFT", "LIKE", "LIMIT", "LINKTYPE", "LOCAL",
            "LOCALDATE", "LOCALE", "LOCALTIME", "LOCALTIMESTAMP", "LOCATOR", "LOCATORS", "LOCK", "LOCKMAX", "LOCKSIZE",
            "LONG", "LOOP", "MAINTAINED", "MATERIALIZED", "MAXVALUE", "MICROSECOND", "MICROSECONDS", "MINUTE",
            "MINUTES", "MINVALUE", "MODE", "MODIFIES", "MONTH", "MONTHS", "NAN", "NEW", "NEW_TABLE", "NEXTVAL", "NO",
            "NOCACHE", "NOCYCLE", "NODENAME", "NODENUMBER", "NOMAXVALUE", "NOMINVALUE", "NONE", "NOORDER", "NORMALIZED",
            "NOT", "NULL", "NULLS", "NUMPARTS", "OBID", "OF", "OFFSET", "OLD", "OLD_TABLE", "ON", "OPEN",
            "OPTIMIZATION", "OPTIMIZE", "OPTION", "OR", "ORDER", "OUT", "OUTER", "OVER", "OVERRIDING", "PACKAGE",
            "PADDED", "PAGESIZE", "PARAMETER", "PART", "PARTITION", "PARTITIONED", "PARTITIONING", "PARTITIONS",
            "PASSWORD", "PATH", "PIECESIZE", "PLAN", "POSITION", "PRECISION", "PREPARE", "PREVVAL", "PRIMARY", "PRIQTY",
            "PRIVILEGES", "PROCEDURE", "PROGRAM", "PSID", "PUBLIC", "QUERY", "QUERYNO", "RANGE", "RANK", "READ",
            "READS", "RECOVERY", "REFERENCES", "REFERENCING", "REFRESH", "RELEASE", "RENAME", "REPEAT", "RESET",
            "RESIGNAL", "RESTART", "RESTRICT", "RESULT", "RESULT_SET_LOCATOR", "RETURN", "RETURNS", "REVOKE", "RIGHT",
            "ROLE", "ROLLBACK", "ROUND_CEILING", "ROUND_DOWN", "ROUND_FLOOR", "ROUND_HALF_DOWN", "ROUND_HALF_EVEN",
            "ROUND_HALF_UP", "ROUND_UP", "ROUTINE", "ROW", "ROW_NUMBER", "ROWNUMBER", "ROWS", "ROWSET", "RRN", "RUN",
            "SAVEPOINT", "SCHEMA", "SCRATCHPAD", "SCROLL", "SEARCH", "SECOND", "SECONDS", "SECQTY", "SECURITY",
            "SELECT", "SENSITIVE", "SEQUENCE", "SESSION", "SESSION_USER", "SET", "SIGNAL", "SIMPLE", "SNAN", "SOME",
            "SOURCE", "SPECIFIC", "SQL", "SQLID", "STACKED", "STANDARD", "START", "STARTING", "STATEMENT", "STATIC",
            "STATMENT", "STAY", "STOGROUP", "STORES", "STYLE", "SUBSTRING", "SUMMARY", "SYNONYM", "SYSFUN", "SYSIBM",
            "SYSPROC", "SYSTEM", "SYSTEM_USER", "TABLE", "TABLESPACE", "THEN", "TIME", "TIMESTAMP", "TO", "TRANSACTION",
            "TRIGGER", "TRIM", "TRUNCATE", "TYPE", "UNDO", "UNION", "UNIQUE", "UNTIL", "UPDATE", "USAGE", "USER",
            "USING", "VALIDPROC", "VALUE", "VALUES", "VARIABLE", "VARIANT", "VCAT", "VERSION", "VIEW", "VOLATILE",
            "VOLUMES", "WHEN", "WHENEVER", "WHERE", "WHILE", "WITH", "WITHOUT", "WLM", "WRITE", "XMLELEMENT",
            "XMLEXISTS", "XMLNAMESPACES", "YEAR", "YEARS", "#SQLite", "ABORT", "ACTION", "ADD", "AFTER", "ALL", "ALTER",
            "ANALYZE", "AND", "AS", "ASC", "ATTACH", "AUTOINCREMENT", "BEFORE", "BEGIN", "BETWEEN", "BY", "CASCADE",
            "CASE", "CAST", "CHECK", "COLLATE", "COLUMN", "COMMIT", "CONFLICT", "CONSTRAINT", "CREATE", "CROSS",
            "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "DATABASE", "DEFAULT", "DEFERRABLE", "DEFERRED",
            "DELETE", "DESC", "DETACH", "DISTINCT", "DROP", "EACH", "ELSE", "END", "ESCAPE", "EXCEPT", "EXCLUSIVE",
            "EXISTS", "EXPLAIN", "FAIL", "FOR", "FOREIGN", "FROM", "FULL", "GLOB", "GROUP", "HAVING", "IF", "IGNORE",
            "IMMEDIATE", "IN", "INDEX", "INDEXED", "INITIALLY", "INNER", "INSERT", "INSTEAD", "INTERSECT", "INTO", "IS",
            "ISNULL", "JOIN", "KEY", "LEFT", "LIKE", "LIMIT", "MATCH", "NATURAL", "NO", "NOT", "NOTNULL", "NULL", "OF",
            "OFFSET", "ON", "OR", "ORDER", "OUTER", "PLAN", "PRAGMA", "PRIMARY", "QUERY", "RAISE", "RECURSIVE",
            "REFERENCES", "REGEXP", "REINDEX", "RELEASE", "RENAME", "REPLACE", "RESTRICT", "RIGHT", "ROLLBACK", "ROW",
            "SAVEPOINT", "SELECT", "SET", "TABLE", "TEMP", "TEMPORARY", "THEN", "TO", "TRANSACTION", "TRIGGER", "UNION",
            "UNIQUE", "UPDATE", "USING", "VACUUM", "VALUES", "VIEW", "VIRTUAL", "WHEN", "WHERE", "WITH", "WITHOUT",
            "#Teradata", "ABORT", "ABORTSESSION", "ABS", "ACCESS_LOCK", "ACCOUNT", "ACOS", "ACOSH", "ADD", "ADD_MONTHS",
            "ADMIN", "AFTER", "AGGREGATE", "ALIAS", "ALL", "ALTER", "AMP", "AND", "ANSIDATE", "ANY", "AS", "ASC",
            "ASIN", "ASINH", "AT", "ATAN", "ATAN2", "ATANH", "ATOMIC", "AUTHORIZATION", "AVE", "AVERAGE", "AVG",
            "BEFORE", "BEGIN", "BETWEEN", "BLOB", "BOTH", "BT", "BUT", "BY", "BYTE", "BYTEINT", "BYTES", "CALL", "CASE",
            "CASE_N", "CASESPECIFIC", "CAST", "CD", "CHAR", "CHAR_LENGTH", "CHAR2HEXINT", "CHARACTER",
            "CHARACTER_LENGTH", "CHARACTERS", "CHARS", "CHECK", "CHECKPOINT", "CLASS", "CLOSE", "CLUSTER", "CM",
            "COALESCE", "COLLATION", "COLLECT", "COLUMN", "COMMENT", "COMMIT", "COMPRESS", "CONSTRAINT", "CONTINUE",
            "CONVERT_TABLE_HEADER", "CORR", "COS", "COSH", "COUNT", "COVAR_POP", "COVAR_SAMP", "CREATE", "CROSS", "CS",
            "CSUM", "CT", "CURRENT", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURSOR", "CV", "CYCLE",
            "DATABASE", "DATABLOCKSIZE", "DATE", "DATEFORM", "DAY", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DEGREES",
            "DEL", "DELETE", "DESC", "DESCRIPTOR", "DETERMINISTIC", "DIAGNOSTIC", "DISABLED", "DISTINCT", "DO",
            "DOUBLE", "DROP", "DUAL", "DUMP", "EACH", "ECHO", "ELSE", "ELSEIF", "ENABLED", "END", "EQ", "ERROR",
            "ERRORFILES", "ERRORTABLES", "ESCAPE", "ET", "EXCEPT", "EXEC", "EXECUTE", "EXISTS", "EXIT", "EXP",
            "EXPLAIN", "EXTERNAL", "EXTRACT", "FALLBACK", "FASTEXPORT", "FETCH", "FIRST", "FLOAT", "FOR", "FOREIGN",
            "FORMAT", "FOUND", "FREESPACE", "FROM", "FULL", "FUNCTION", "GE", "GENERATED", "GIVE", "GO", "GOTO",
            "GRANT", "GRAPHIC", "GROUP", "GT", "HANDLER", "HASH", "HASHAMP", "HASHBAKAMP", "HASHBUCKET", "HASHROW",
            "HAVING", "HELP", "HOUR", "IDENTITY", "IF", "IMMEDIATE", "IN", "INCONSISTENT", "INDEX", "INDICATOR",
            "INITIATE", "INNER", "INOUT", "INPUT", "INS", "INSERT", "INSTEAD", "INT", "INTEGER", "INTEGERDATE",
            "INTERSECT", "INTERVAL", "INTO", "IS", "ITERATE", "JOIN", "JOURNAL", "KEY", "KURTOSIS", "LANGUAGE", "LE",
            "LEADING", "LEAVE", "LEFT", "LIKE", "LIMIT", "LN", "LOADING", "LOCAL", "LOCATOR", "LOCK", "LOCKING", "LOG",
            "LOGGING", "LOGON", "LONG", "LOOP", "LOWER", "LT", "MACRO", "MAVG", "MAX", "MAXIMUM", "MCHARACTERS",
            "MDIFF", "MERGE", "MIN", "MINDEX", "MINIMUM", "MINUS", "MINUTE", "MLINREG", "MLOAD", "MOD", "MODE",
            "MODIFY", "MONITOR", "MONRESOURCE", "MONSESSION", "MONTH", "MSUBSTR", "MSUM", "MULTISET", "NAMED",
            "NATURAL", "NE", "NEW", "NEW_TABLE", "NEXT", "NO", "NONE", "NOT", "NOWAIT", "NULL", "NULLIF", "NULLIFZERO",
            "NUMERIC", "OBJECTS", "OCTET_LENGTH", "OF", "OFF", "OLD", "OLD_TABLE", "ON", "ONLY", "OPEN", "OPTION", "OR",
            "ORDER", "OUT", "OUTER", "OVER", "OVERLAPS", "OVERRIDE", "PARAMETER", "PASSWORD", "PERCENT", "PERCENT_RANK",
            "PERM", "PERMANENT", "POSITION", "PRECISION", "PREPARE", "PRESERVE", "PRIMARY", "PRIVATE", "PRIVILEGES",
            "PROCEDURE", "PROFILE", "PROPORTIONAL", "PROTECTION", "PUBLIC", "QUALIFIED", "QUALIFY", "QUANTILE",
            "RADIANS", "RANDOM", "RANGE_N", "RANK", "REAL", "REFERENCES", "REFERENCING", "REGR_AVGX", "REGR_AVGY",
            "REGR_COUNT", "REGR_INTERCEPT", "REGR_R2", "REGR_SLOPE", "REGR_SXX", "REGR_SXY", "REGR_SYY", "RELEASE",
            "RENAME", "REPEAT", "REPLACE", "REPLICATION", "REPOVERRIDE", "REQUEST", "RESTART", "RESTORE", "RESUME",
            "RET", "RETRIEVE", "RETURNS", "REVALIDATE", "REVOKE", "RIGHT", "RIGHTS", "ROLE", "ROLLBACK", "ROLLFORWARD",
            "ROW", "ROW_NUMBER", "ROWID", "ROWS", "SAMPLE", "SAMPLEID", "SCROLL", "SECOND", "SEL", "SELECT", "SESSION",
            "SET", "SETRESRATE", "SETSESSRATE", "SHOW", "SIMPLE", "SIN", "SINH", "SKEW", "SMALLINT", "SOME", "SOUNDEX",
            "SPECIFIC", "SPOOL", "SQL", "SQLEXCEPTION", "SQLTEXT", "SQLWARNING", "SQRT", "SS", "START", "STARTUP",
            "#Hana", "ALL", "ALTER", "AS", "BEFORE", "BEGIN", "BOTH", "CASE", "CHAR", "CONDITION", "CONNECT", "CROSS",
            "CUBE", "CURRENT_CONNECTION", "CURRENT_DATE", "CURRENT_SCHEMA", "CURRENT_TIME", "CURRENT_TIMESTAMP",
            "CURRENT_TRANSACTION_ISOLATION_LEVEL", "CURRENT_USER", "CURRENT_UTCDATE", "CURRENT_UTCTIME",
            "CURRENT_UTCTIMESTAMP", "CURRVAL", "CURSOR", "DECLARE", "DISTINCT", "ELSE", "ELSEIF", "END", "EXCEPT",
            "EXCEPTION", "EXEC", "FALSE", "FOR", "FROM", "FULL", "GROUP", "HAVING", "IF", "IN", "INNER", "INOUT",
            "INTERSECT", "INTO", "IS", "JOIN", "LEADING", "LEFT", "LIMIT", "LOOP", "MINUS", "NATURAL", "NCHAR",
            "NEXTVAL", "NULL", "ON", "ORDER", "OUT", "PRIOR", "RETURN", "RETURNS", "REVERSE", "RIGHT", "ROLLUP",
            "ROWID", "SELECT", "SESSION_USER", "SET", "SQL", "START", "SYSUUID", "TABLESAMPLE", "TOP", "TRAILING",
            "TRUE", "UNION", "UNKNOWN", "USING", "UTCTIMESTAMP", "VALUES", "WHEN", "WHERE", "WHILE", "WITH", "#Maria",
            "ACCESSIBLE", "ADD", "ALL", "ALTER", "ANALYZE", "AND", "AS", "ASC", "ASENSITIVE", "BEFORE", "BETWEEN",
            "BIGINT", "BINARY", "BLOB", "BOTH", "BY", "CALL", "CASCADE", "CASE", "CHANGE", "CHAR", "CHARACTER", "CHECK",
            "COLLATE", "COLUMN", "CONDITION", "CONSTRAINT", "CONTINUE", "CONVERT", "CREATE", "CROSS", "CURRENT_DATE",
            "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "DATABASE", "DATABASES", "DAY_HOUR",
            "DAY_MICROSECOND", "DAY_MINUTE", "DAY_SECOND", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DELAYED", "DELETE",
            "DESC", "DESCRIBE", "DETERMINISTIC", "DISTINCT", "DISTINCTROW", "DIV", "DOUBLE", "DROP", "DUAL", "EACH",
            "ELSE", "ELSEIF", "ENCLOSED", "ESCAPED", "EXISTS", "EXIT", "EXPLAIN", "FETCH", "FLOAT", "FLOAT4", "FLOAT8",
            "FOR", "FORCE", "FOREIGN", "FROM", "FULLTEXT", "GENERAL", "GRANT", "GROUP", "HAVING", "HIGH_PRIORITY",
            "HOUR_MICROSECOND", "HOUR_MINUTE", "HOUR_SECOND", "IF", "IGNORE", "IGNORE_SERVER_IDS", "IN", "INDEX",
            "INFILE", "INNER", "INOUT", "INSENSITIVE", "INSERT", "INT", "INT1", "INT2", "INT3", "INT4", "INT8",
            "INTEGER", "INTERVAL", "INTO", "IS", "ITERATE", "JOIN", "KEY", "KEYS", "KILL", "LEADING", "LEAVE", "LEFT",
            "LIKE", "LIMIT", "LINEAR", "LINES", "LOAD", "LOCALTIME", "LOCALTIMESTAMP", "LOCK", "LONG", "LONGBLOB",
            "LONGTEXT", "LOOP", "LOW_PRIORITY", "MASTER_HEARTBEAT_PERIOD", "MASTER_SSL_VERIFY_SERVER_CERT", "MATCH",
            "MAXVALUE", "MEDIUMBLOB", "MEDIUMINT", "MEDIUMTEXT", "MIDDLEINT", "MINUTE_MICROSECOND", "MINUTE_SECOND",
            "MOD", "MODIFIES", "NATURAL", "NO_WRITE_TO_BINLOG", "NOT", "NULL", "NUMERIC", "ON", "OPTIMIZE", "OPTION",
            "OPTIONALLY", "OR", "ORDER", "OUT", "OUTER", "OUTFILE", "PARTITION", "PRECISION", "PRIMARY", "PROCEDURE",
            "PURGE", "RANGE", "READ", "READ_WRITE", "READS", "REAL", "REFERENCES", "REGEXP", "RELEASE", "RENAME",
            "REPEAT", "REPLACE", "REQUIRE", "RESIGNAL", "RESTRICT", "RETURN", "REVOKE", "RIGHT", "RLIKE", "SCHEMA",
            "SCHEMAS", "SECOND_MICROSECOND", "SELECT", "SENSITIVE", "SEPARATOR", "SET", "SHOW", "SIGNAL", "SLOW",
            "SMALLINT", "SPATIAL", "SPECIFIC", "SQL", "SQL_BIG_RESULT", "SQL_CALC_FOUND_ROWS", "SQL_SMALL_RESULT",
            "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "SSL", "STARTING", "STRAIGHT_JOIN", "TABLE", "TERMINATED", "THEN",
            "TINYBLOB", "TINYINT", "TINYTEXT", "TO", "TRAILING", "TRIGGER", "UNDO", "UNION", "UNIQUE", "UNLOCK",
            "UNSIGNED", "UPDATE", "USAGE", "USE", "USING", "UTC_DATE", "UTC_TIME", "UTC_TIMESTAMP", "VALUES",
            "VARBINARY", "VARCHAR", "VARCHARACTER", "VARYING", "WHEN", "WHERE", "WHILE", "WITH", "WRITE", "XOR",
            "YEAR_MONTH", "ZEROFILL", "FALSE", "TRUE", "#H2", "CROSS", "CURRENT_DATE", "CURRENT_TIME",
            "CURRENT_TIMESTAMP", "DISTINCT", "EXCEPT", "EXISTS", "FALSE", "FETCH", "FOR", "FROM", "FULL", "GROUP",
            "HAVING", "INNER", "INTERSECT", "IS", "JOIN", "LIKE", "LIMIT", "MINUS", "NATURAL", "NOT", "NULL", "OFFSET",
            "ON", "ORDER", "PRIMARY", "ROWNUM", "SELECT", "SYSDATE", "SYSTIME", "SYSTIMESTAMP", "TODAY", "TRUE",
            "UNION", "UNIQUE", "WHERE", "#Sybase", "add", "all", "alter", "and", "any", "arith_overflow", "as", "asc",
            "at", "authorization", "avg", "begin", "between", "break", "browse", "bulk", "by", "cascade", "case",
            "char_convert", "check", "checkpoint", "close", "clustered", "coalesce", "commit", "compute", "confirm",
            "connect", "constraint", "continue", "controlrow", "convert", "count", "create", "current", "cursor",
            "database", "dbcc", "deallocate", "declare", "default", "delete", "desc", "deterministic", "diskdistinct",
            "double", "drop", "dummy", "dump", "else", "end", "endtran", "errlvl", "errordata", "errorexit", "escape",
            "except", "exclusive", "exec", "execute", "exists", "exit", "exp_row_size", "external", "fetch",
            "fillfactor", "for", "foreign", "from", "func", "goto", "grant", "group", "having", "holdlock", "identity",
            "identity_gap", "identity_insert", "identity_start", "if", "in", "index", "inout", "insert", "install",
            "intersect", "into", "is", "isolation", "jar", "join", "key", "kill", "level", "like", "lineno", "load",
            "lock", "max", "max_rows_per_page", "min", "mirror", "mirrorexit", "modify", "national", "new",
            "noholdlock", "nonclustered", "not", "null", "nullif", "numeric_truncation", "of", "off", "offsets", "on",
            "once", "online", "only", "open", "option", "or", "order", "out", "output", "over", "partition", "perm",
            "permanent", "plan", "precision", "prepare", "primary", "print", "privileges", "proc", "procedure",
            "processexit", "proxy_table", "public", "quiesce", "raiserror", "read", "readpast", "readtext",
            "reconfigure", "referencesremove", "reorg", "replace", "replication", "reservepagegap", "return", "returns",
            "revoke", "role", "rollback", "rowcount", "rows", "rule", "save", "schema", "select", "set", "setuser",
            "shared", "shutdown", "some", "statistics", "stringsize", "stripe", "sum", "syb_identity", "syb_restree",
            "syb_terminate", "table", "temp", "temporary", "textsize", "to", "tran", "transaction", "trigger",
            "truncate", "tsequal", "union", "unique", "unpartition", "update", "use", "user", "user_option", "using",
            "values", "varying", "view", "waitfor", "when", "where", "while", "with", "work", "writetext", "#Firebird",
            "ADD", "ADMIN", "ALL", "ALTER", "AND", "ANY", "AS", "AT", "AVG", "BEGIN", "BETWEEN", "BIGINT", "BIT_LENGTH",
            "BLOB", "BOTH", "BY", "CASE", "CAST", "CHAR", "CHAR_LENGTH", "CHARACTER", "CHARACTER_LENGTH", "CHECK",
            "CLOSE", "COLLATE", "COLUMN", "COMMIT", "CONNECT", "CONSTRAINT", "COUNT", "CREATE", "CROSS", "CURRENT",
            "CURRENT_CONNECTION", "CURRENT_DATE", "CURRENT_ROLE", "CURRENT_TIME", "CURRENT_TIMESTAMP",
            "CURRENT_TRANSACTION", "CURRENT_USER", "CURSOR", "DATE", "DAY", "DEC", "DECIMAL", "DECLARE", "DEFAULT",
            "DELETE", "DISCONNECT", "DISTINCT", "DOUBLE", "DROP", "ELSE", "END", "ESCAPE", "EXECUTE", "EXISTS",
            "EXTERNAL", "EXTRACT", "FETCH", "FILTER", "FLOAT", "FOR", "FOREIGN", "FROM", "FULL", "FUNCTION", "GDSCODE",
            "GLOBAL", "GRANT", "GROUP", "HAVING", "HOUR", "IN", "INDEX", "INNER", "INSENSITIVE", "INSERT", "INT",
            "INTEGER", "INTO", "IS", "JOIN", "LEADING", "LEFT", "LIKE", "LONG", "LOWER", "MAX", "MAXIMUM_SEGMENT",
            "MERGE", "MIN", "MINUTE", "MONTH", "NATIONAL", "NATURAL", "NCHAR", "NO", "NOT", "NULL", "NUMERIC",
            "OCTET_LENGTH", "OF", "ON", "ONLY", "OPEN", "OR", "ORDER", "OUTER", "PARAMETER", "PLAN", "POSITION",
            "POST_EVENT", "PRECISION", "PRIMARY", "PROCEDURE", "RDB$DB_KEY", "REAL", "RECORD_VERSION", "RECREATE",
            "RECURSIVE", "REFERENCES", "RELEASE", "RETURNING_VALUES", "RETURNS", "REVOKE", "RIGHT", "ROLLBACK",
            "ROW_COUNT", "ROWS", "SAVEPOINT", "SECOND", "SELECT", "SENSITIVE", "SET", "SIMILAR", "SMALLINT", "SOME",
            "SQLCODE", "SQLSTATE", "START", "SUM", "TABLE", "THEN", "TIME", "TIMESTAMP", "TO", "TRAILING", "TRIGGER",
            "TRIM", "UNION", "UNIQUE", "UPDATE", "UPPER", "USER", "USING", "VALUE", "VALUES", "VARCHAR", "VARIABLE",
            "VARYING", "VIEW", "WHEN", "WHERE", "WHILE", "WITH", "YEAR"};
    private static final Map<String, String> RESERVED_WORDS = new HashMap<String, String>();

    private ReservedDBWords() {
        // hide default constructor
    }

    static {
        String db = null;
        for (String word : RESERV_WD)
            if (word.startsWith("#"))
                db = word.substring(1);
            else
                addWord(word, db);
    }

    /**
     * Check if is a dialect reserved word of ANSI-SQL reserved word
     *
     * @return false:not reserved word. true:reserved by dialect or ANSI-SQL
     */
    public static boolean isReservedWord(Dialect dialect, String word) {
        if (!isReservedWord(word))
            return false;
        String fitDatabases = RESERVED_WORDS.get(word.toUpperCase()).toUpperCase();
        if (fitDatabases.contains("ANSI"))
            return true;
        String dia = dialect.toString().replace("Dialect", "").toUpperCase();
        if (dia.length() >= 4)
            dia = dia.substring(0, 4);// only compare first 4 letters
        return (fitDatabases.contains(dia));
    }

    /**
     * Check if is a reserved word of any database
     */
    public static boolean isReservedWord(String word) {
        return !StrUtils.isEmpty(word) && RESERVED_WORDS.containsKey(word.toUpperCase());
    }

    /**
     * Return database name of given reserved word
     */
    public static String reservedForDB(String word) {
        return RESERVED_WORDS.get(word.toUpperCase());
    }

    private static void addWord(String word, String databaseName) {
        String value = RESERVED_WORDS.get(word.toUpperCase());
        if (value == null || value.length() == 0)
            RESERVED_WORDS.put(word.toUpperCase(), databaseName);
        else {
            if (value.indexOf(databaseName) < 0)
                RESERVED_WORDS.put(word.toUpperCase(), value + "/" + databaseName);
        }
    }

}
