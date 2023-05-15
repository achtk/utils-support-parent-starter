package com.chua.common.support.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * logger
 *
 * @author CH
 */
@Getter
@AllArgsConstructor
public enum Level {
    /**
     * trace
     */
    TRACE(0),
    /**
     * debug
     */
    DEBUG(1),
    /**
     * info
     */
    INFO(2),
    /**
     * warn
     */
    WARN(3),
    /**
     * error
     */
    ERROR(4),
    /**
     * none
     */
    NONE(5);

    private final int level;
}
