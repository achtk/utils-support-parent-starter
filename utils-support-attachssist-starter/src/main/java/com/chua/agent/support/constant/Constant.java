package com.chua.agent.support.constant;

/**
 * Constant
 * @author CH
 */
public interface Constant {
    String LINK_ID = "x-request-link-id";
    String LINK_PID = "x-request-pid";
    String LINK_RES_SPAN = "x-response-span";

    /**
     * 发送指令：连接
     */
    String WEBSSH_OPERATE_CONNECT = "connect";
    /**
     * 发送指令：命令
     */
    String WEBSSH_OPERATE_COMMAND = "command";
}
