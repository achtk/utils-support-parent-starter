package com.chua.common.support.lang.code;

import static com.chua.common.support.lang.code.ReturnResultCode.*;

/**
 * @author haoxr
 **/
public interface ResultCode {
    /**
     * 转化编码
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
        if(status == 200) {
            return OK;
        }
        if(status == 403) {
            return RESOURCE_OAUTH_DENIED;
        }

        if(status.toString().startsWith("40")) {
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
