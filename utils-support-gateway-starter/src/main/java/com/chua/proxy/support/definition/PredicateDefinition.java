package com.chua.proxy.support.definition;

import lombok.Data;

/**
 * 谓词定义
 *
 * @author CH
 */
@Data
public class PredicateDefinition {

    private String name;

    private String version;

    private String jsonConf;

    private boolean builtin = true;

}
