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
     * 模块描述
     */
    private String moduleDesc;
    /**
     * 响应类型
     */
    private String moduleResponse;
    /**
     * 请求类型
     */
    private String moduleRequest;
    /**
     * 请求是否缓存
     */
    private Integer moduleEnableCache;

    /**
     * 依赖关系
     */
    private String moduleDepends;
    /**
     * 连接超时时间
     */
    @Builder.Default
    private String moduleConnectionTimeout = "1min";
    /**
     * 消息头
     */
    private String moduleHeader;
    /**
     * url
     */
    private String moduleUrl;
    /**
     * driver
     */
    private String moduleDriver;
    /**
     * ak
     */
    @Builder.Default
    private String moduleAppKey = "root";
    /**
     * sk
     */
    @Builder.Default
    private String moduleAppSecret = "root";
    /**
     * 自定义解析器
     */
    private String moduleResolver;

    /**
     * 是否有依赖
     * @return 是否有依赖
     */
    public boolean hasDepends() {
        return StringUtils.isNotEmpty(moduleDepends);
    }

    public boolean hasResponseType() {
        return StringUtils.isNotEmpty(moduleResponse);
    }

    public String getModuleId() {
        return getModuleType() + ":" + getModuleName();
    }
}
