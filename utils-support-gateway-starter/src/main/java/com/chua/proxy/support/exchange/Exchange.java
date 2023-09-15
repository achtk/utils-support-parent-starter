package com.chua.proxy.support.exchange;

import com.chua.proxy.support.attribute.AttributesHolder;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 交换
 *
 * @author CH
 */
public interface Exchange extends AttributesHolder {

    /**
     * 获取请求
     *
     * @return {@link HttpServerRequest}
     */
    HttpServerRequest getRequest();

    /**
     * 获取响应
     *
     * @return {@link HttpServerResponse}
     */
    HttpServerResponse getResponse();

    /**
     * 建设者
     *
     * @author CH
     */
    interface Builder {
        /**
         * 请求
         *
         * @param request 请求
         * @return {@link Builder}
         */
        Builder request(HttpServerRequest request);

        /**
         * 响应
         *
         * @param response 响应
         * @return {@link Builder}
         */
        Builder response(HttpServerResponse response);

        /**
         * 构建
         *
         * @return {@link Exchange}
         */
        Exchange build();

    }

    /**
     * mutate
     *
     * @return {@link Builder}
     */
    Builder mutate();

    /**
     * 设置审核信息
     *
     * @param key   钥匙
     * @param value 值
     */
    default void setAuditInfo(String key, Object value) {
        String auditInfoKey = "__audit_info__";
        Map<String, Object> auditInfo = getAttribute(auditInfoKey);
        if (Objects.isNull(auditInfo)) {
            auditInfo = new HashMap<>();
            getAttributes().put(auditInfoKey, auditInfo);
        }

        auditInfo.put(key, value);
    }

    /**
     * 获取审核信息
     *
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    default Map<String, Object> getAuditInfo() {
        String auditInfoKey = "__audit_info__";
        return getAttributeOrDefault(auditInfoKey, new HashMap<>());
    }

}
