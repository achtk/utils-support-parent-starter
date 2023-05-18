package com.chua.agent.support.plugin;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * 插件
 *
 * @author CH
 */
public interface AgentPlugin {
    /**
     * 名称
     *
     * @return 名称
     */
    String name();

    /**
     * 插件类型
     *
     * @return 插件类型
     */
    default Class<?> pluginType() {
        return this.getClass();
    }

    /**
     * 插件运行对象
     *
     * @return 插件运行对象
     */
    default AgentPlugin resolve() {
        return this;
    }

    /**
     * 编译器
     *
     * @param builder 编译器
     * @return ReceiverTypeDefinition
     */
    DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<?> transform(DynamicType.Builder<?> builder);

    /**
     * 编译器
     *
     * @param transform 编译器
     * @return Extendable
     */
    default AgentBuilder.Identified.Extendable transforms(AgentBuilder.Identified.Extendable transform) {
        return transform;
    }

    /**
     * 类型
     *
     * @return 类型
     */
    ElementMatcher<? super TypeDescription> type();

}
