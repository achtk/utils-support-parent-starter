package com.chua.common.support.modularity;

import com.chua.common.support.utils.StringUtils;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 模块
 *
 * @author CH
 */
@Data
@Builder
@Accessors(chain = true)
public class Modularity {
    /**
     * 模块名称(唯一)(必填)
     */
    private String moduleName;
    /**
     * 处理内容,比如:http地址, sql等
     */
    private String moduleScript;
    /**
     * 模块类型(必填)
     */
    private String moduleType;
    /**
     * 响应类型
     */
    private String moduleResponse;
    /**
     * 请求类型
     */
    private String moduleRequest;
    /**
     * 依赖关系
     */
    private String moduleDag;
    /**
     * 连接超时时间
     */
    private String moduleConnectionTimeout = "1min";
    /**
     * 消息头
     */
    private String moduleHeader;

    /**
     * 是否有依赖
     * @return 是否有依赖
     */
    public boolean hasDepends() {
        return StringUtils.isNotEmpty(moduleDag);
    }

    public boolean hasResponseType() {
        return StringUtils.isNotEmpty(moduleResponse);
    }
}
