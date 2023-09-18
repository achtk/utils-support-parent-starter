package com.chua.common.support.lang.code;

import lombok.AllArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * @author Administrator
 */
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public enum ReturnResultCode implements ResultCode {
    /**
     * 操作成功
     **/
    OK("00000", "操作成功"),
    /**
     * 操作成功
     **/
    SUCCESS("00000", "操作成功"),
    /**
     * 其它错误
     **/
    OTHER("Z0000", "其它错误"),
    //*******************************************客户端端******************************************************
    /**
     * 请求必填参数为空
     */
    PARAM_IS_NULL("C0400", "请求必填参数为空"),
    /**
     * 请求参数错误
     */
    PARAM_ERROR("C0400", "请求参数错误"),
    /**
     * 请求资源不存在
     */
    RESOURCE_NOT_FOUND("C0404", "请求资源不存在"),
    /**
     * 无访问权限,请联系管理员授予权限
     */
    RESOURCE_OAUTH_DENIED("A0403", "无访问权限,请联系管理员授予权限"),


    //*******************************************服务端******************************************************
    /**
     * 服务繁忙
     **/
    SYSTEM_SERVER_BUSINESS("S0103", "服务繁忙,请稍后再试!"),
    /**
     * 服务器资源不存在
     */
    SYSTEM_SERVER_NOT_FOUND("S0404", "服务器资源不存在!"),
    /**
     * 系统错误
     */
    SERVER_ERROR("A9999", "系统错误"),
    ;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    private final String code;

    private final String msg;

    /**
     * 结果
     * @param code 状态码
     * @return 结果
     */

    public static ReturnResultCode getValue(String code) {
        for (ReturnResultCode value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return ReturnResultCode.SYSTEM_SERVER_BUSINESS;
    }
}