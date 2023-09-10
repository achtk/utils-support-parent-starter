package com.chua.common.support.lang.code;

import static com.chua.common.support.lang.code.ReturnResultCode.*;

/**
 * @author haoxr
 **/
public interface ResultCode {
    int V_200 = 200;
    int V_403 = 403;

    /**
     * 转化编码
     *
     * @param status 编码
     * @return 结果
     */
    static String transferForHttpCodeStatus(Integer status) {
        return transferForHttpCode(status).getMsg();
    }

    /**
     * 转化编码
     * @param status 编码
     * @return 结果
     */
    static ResultCode transferForHttpCode(Integer status) {
        int v300 = 300;
        if (status >= V_200 && status < v300) {
            return OK;
        }
        if (status == V_403) {
            return RESOURCE_OAUTH_DENIED;
        }

        String str = "40";
        if (status.toString().startsWith(str)) {
            return PARAM_ERROR;
        }

        return SERVER_ERROR;
    }

    /**
     * 状态码
     * @return 状态码
     */
    String getCode();

    /**
     * 信息
     * @return 信息
     */
    String getMsg();

    /**
     * 是否存在异常
     * @return 是否存在异常
     */
    default boolean hasError() {
        return this != OK && this != SUCCESS;
    }
}
