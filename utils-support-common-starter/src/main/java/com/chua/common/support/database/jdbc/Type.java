/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http:www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.chua.common.support.database.jdbc;


import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Virtual SQL Type definitions
 *
 * @author Yong Zhu
 * @since 1.0.0
 */
@SuppressWarnings("ALL")
public enum Type {
    BIGINT, BINARY, BIT, BLOB, BOOLEAN, CHAR, CLOB, DATE, DECIMAL, DOUBLE, FLOAT, INTEGER, JAVA_OBJECT, LONGNVARCHAR, LONGVARBINARY, LONGVARCHAR, NCHAR, NCLOB, NUMERIC, NVARCHAR, UNKNOW, REAL, SMALLINT, TIME, TIMESTAMP, TINYINT, VARBINARY, VARCHAR, DATETIME, MEDIUMINT, INT, TINYBLOB, TINYTEXT, TEXT, MEDIUMBLOB, MEDIUMTEXT, LONGBLOB, LONGTEXT, YEAR, JSON, BINARY_FLOAT, DOUBLE_PRECISION, BINARY_DOUBLE, TIMESTAMP_WITH_TIME_ZONE, TIMESTAMP_WITH_LOCAL_TIME_ZONE, VARCHAR2, INTERVAL_YEAR_TO_MONTH, INTERVAL_DAY_TO_SECOND;

    public static Type getByTypeName(String typeName) {
        for (Type val : Type.values()) {
            if (val.name().equalsIgnoreCase(typeName)) {
                return val;
            }
        }
        throw new DialectException("'" + typeName + "' can not be map to a dialect type");
    }

    public static Type valueTypeOf(Class<?> javaType) {
        if (javaType == String.class) {
            return VARCHAR;
        }

        if (javaType == Integer.class || javaType == Long.class) {
            return INTEGER;
        }

        if (javaType == Byte.class || javaType == Short.class) {
            return SMALLINT;
        }

        if (javaType == byte[].class) {
            return BINARY;
        }

        if (javaType == Character.class) {
            return CHAR;
        }

        if (javaType == BigInteger.class) {
            return BIGINT;
        }

        if (javaType == Float.class || javaType == Double.class) {
            return DECIMAL;
        }

        if (javaType == LocalDateTime.class || javaType == Timestamp.class) {
            return TIMESTAMP;
        }

        if (javaType == Date.class || javaType == java.sql.Date.class) {
            return DATE;
        }

        if (javaType == Time.class) {
            return TIME;
        }

        if (javaType == Blob.class) {
            return BLOB;
        }

        if (javaType == Clob.class) {
            return CLOB;
        }

        return VARCHAR;
    }
}
