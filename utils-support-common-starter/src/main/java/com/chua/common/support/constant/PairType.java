package com.chua.common.support.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * jdbc - java
 *
 * @author CH
 */
@Getter
@AllArgsConstructor
public enum PairType {
    /**
     * varchar
     */
    VARCHAR(String.class, "VARCHAR"),
    /**
     * char
     */
    CHAR(String.class, "CHAR"),
    /**
     * bit
     */
    BIT(Boolean.class, "BIT"),
    /**
     * NUMERIC
     */
    NUMERIC(BigDecimal.class, "NUMERIC"),
    /**
     * TINYINT
     */
    TINYINT(Integer.class, "TINYINT"),
    /**
     * SMALLINT
     */
    SMALLINT(Integer.class, "SMALLINT"),
    /**
     * INTEGER
     */
    INTEGER(Integer.class, "INTEGER"),
    /**
     * BIGINT
     */
    BIGINT(Long.class, "BIGINT"),
    /**
     * REAL
     */
    REAL(Float.class, "REAL"),
    /**
     * FLOAT
     */
    FLOAT(Float.class, "FLOAT"),
    /**
     * DOUBLE
     */
    DOUBLE(Double.class, "DOUBLE"),
    /**
     * VARBINARY
     */
    VARBINARY(byte[].class, "VARBINARY"),
    /**
     * BINARY
     */
    BINARY(byte[].class, "BINARY"),
    /**
     * DATE
     */
    DATE(Date.class, "DATE"),
    /**
     * DATETIME
     */
    DATETIME(LocalDateTime.class, "DATETIME"),
    /**
     * DATETIME
     */
    DATETIME2(LocalDate.class, "DATETIME"),
    /**
     * DATETIME
     */
    DATETIME3(LocalTime.class, "TIME"),
    /**
     * DATE
     */
    SQL_DATE(java.sql.Date.class, "DATE"),
    /**
     * TIME
     */
    TIME(Time.class, "TIME"),
    /**
     * TIMESTAMP
     */
    TIMESTAMP(Timestamp.class, "TIMESTAMP"),
    /**
     * CLOB
     */
    CLOB(Clob.class, "CLOB"),
    /**
     * BLOB
     */
    BLOB(Blob.class, "BLOB"),
    /**
     * ARRAY
     */
    ARRAY(Array.class, "ARRAY"),
    /**
     * STRUCT
     */
    STRUCT(Struct.class, "STRUCT"),
    /**
     * TEXT
     */
    TEXT(String.class, "TEXT"),
    /**
     * LONGTEXT
     */
    LONGTEXT(String.class, "LONGTEXT"),
    /**
     * BOOLEAN
     */
    BOOLEAN(Boolean.class, "BOOLEAN"),
    /**
     * REF
     */
    REF(Ref.class, "REF");

    private Class<?> javaType;

    private String jdbcType;
}
