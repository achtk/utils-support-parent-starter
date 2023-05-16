/*
 * Copyright 2013 Stanley Shyiko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chua.common.support.mysql.event.deserialization;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public enum ColumnType {
    /**
     * d
     */
    DECIMAL(0),
    /**
     * tiny
     */
    TINY(1),
    /**
     * short
     */
    SHORT(2),
    /**
     * long
     */
    LONG(3),
    /**
     * float
     */
    FLOAT(4),
    /**
     * double
     */
    DOUBLE(5),
    /**
     * null
     */
    NULL(6),
    /**
     * TIMESTAMP
     */
    TIMESTAMP(7),
    /**
     * LONGLONG
     */
    LONGLONG(8),
    /**
     * INT24
     */
    INT24(9),
    /**
     * DATE
     */
    DATE(10),
    /**
     * TIME
     */
    TIME(11),
    /**
     * DATETIME
     */
    DATETIME(12),
    /**
     * YEAR
     */
    YEAR(13),
    /**
     * NEWDATE
     */
    NEWDATE(14),
    /**
     * VARCHAR
     */
    VARCHAR(15),
    /**
     * BIT
     */
    BIT(16),
    /**
     * (TIMESTAMP|DATETIME|TIME)_V2 data types appeared in MySQL 5.6.4
     * @see  {@link  // STOPSHIP: 2023/1/16   http://dev.mysql.com/doc/internals/en/date-and-time-data-type-representation.html}
     */
    TIMESTAMP_V2(17),
    /**
     * DATETIME_V2
     */
    DATETIME_V2(18),
    /**
     * db
     */
    TIME_V2(19),
    /**
     * db
     */
    JSON(245),
    /**
     * db
     */
    NEWDECIMAL(246),
    /**
     * db
     */
    ENUM(247),
    /**
     * db
     */
    SET(248),
    /**
     * db
     */
    TINY_BLOB(249),
    /**
     * db
     */
    MEDIUM_BLOB(250),
    /**
     * db
     */
    LONG_BLOB(251),
    /**
     * db
     */
    BLOB(252),
    /**
     * db
     */
    VAR_STRING(253),
    /**
     * db
     */
    STRING(254),
    /**
     * db
     */
    GEOMETRY(255);

    private final int code;

    private ColumnType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    private static final Map<Integer, ColumnType> INDEX_BY_CODE;

    static {
        INDEX_BY_CODE = new HashMap<Integer, ColumnType>();
        for (ColumnType columnType : values()) {
            INDEX_BY_CODE.put(columnType.code, columnType);
        }
    }

    public static ColumnType byCode(int code) {
        return INDEX_BY_CODE.get(code);
    }

}
