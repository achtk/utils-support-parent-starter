package com.chua.proxy.support.definition;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 路线定义
 *
 * @author CH
 */
@Data
public class RouteDefinition {

    private Set<String> methods;

    private String path;

    private AccessLogConf accessLogConf;

    private List<PredicateDefinition> predicateDefinitions = new ArrayList<>(1);

    private List<PluginInstanceDefinition> pluginDefinitions = new ArrayList<>(4);

}
