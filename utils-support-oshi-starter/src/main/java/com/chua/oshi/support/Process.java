package com.chua.oshi.support;

import lombok.Data;

/**
 * 过程
 *
 * @author CH
 */
@Data
public class Process {

    /**
     * command
     */
    private String command;

    /**
     * 名称
     */
    private String name;

    /**
     * id
     */
    private String id;
}
