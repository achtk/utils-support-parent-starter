package com.chua.proxy.support.definition;

import lombok.Data;

/**
 * 插件实例定义
 *
 * @author CH
 */
@Data
public class PluginInstanceDefinition {

    private String name;

    private String fullClassName;

    private String version;

    private String jsonConf;

    private boolean builtin = true;

}
