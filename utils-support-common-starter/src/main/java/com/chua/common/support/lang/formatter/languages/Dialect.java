package com.chua.common.support.lang.formatter.languages;

import com.chua.common.support.lang.formatter.core.AbstractFormatter;
import com.chua.common.support.lang.formatter.core.FormatConfig;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * 基础类
 *
 * @author CH
 */
public enum Dialect {
    /**
     * Db2
     */
    Db2(Db2Formatter::new),
    /**
     * MariaDb
     */
    MariaDb(MariaDbFormatter::new),
    /**
     * MySql
     */
    MySql(MySqlFormatter::new),
    /**
     * N1ql
     */
    N1ql(N1qlFormatter::new),
    /**
     * PlSql
     */
    PlSql(PlSqlFormatter::new, "pl/sql"),
    /**
     * PostgreSql
     */
    PostgreSql(PostgreSqlFormatter::new),
    /**
     * Redshift
     */
    Redshift(RedshiftFormatter::new),
    /**
     * SparkSql
     */
    SparkSql(SparkSqlFormatter::new, "spark"),
    /**
     * StandardSql
     */
    StandardSql(StandardSqlFormatter::new, "sql"),
    /**
     * TSql
     */
    TSql(TSqlFormatter::new),
    ;

    public final Function<FormatConfig, AbstractFormatter> func;
    public final List<String> aliases;

    Dialect(Function<FormatConfig, AbstractFormatter> func, String... aliases) {
        this.func = func;
        this.aliases = Arrays.asList(aliases);
    }

    private boolean matches(String name) {
        return this.name().equalsIgnoreCase(name)
                || this.aliases.stream().anyMatch(s -> s.equalsIgnoreCase(name));
    }

    public static Optional<Dialect> nameOf(String name) {
        return Arrays.stream(values()).filter(d -> d.matches(name)).findFirst();
    }
}
