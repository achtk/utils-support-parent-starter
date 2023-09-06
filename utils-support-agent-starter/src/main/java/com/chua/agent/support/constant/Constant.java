package com.chua.agent.support.constant;

import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

/**
 * 常量
 *
 * @author CH
 */
public interface Constant {

    String DATE_FORMAT_DETAIL = "yyyy-MM-dd HH:mm:ss";
    /**
     * 日志级别(默认: SEVERE)
     */
    String LOG_LEVEL = "log.level";
    /**
     * 日志是否开启(默认: true)
     */
    String LOG_OPEN = "log.open";
    /**
     * 嵌入服务器(默认: NONE)
     */
    String SERVER_TYPE = "embed.server";
    /**
     * 推送服务器地址(默认: 127.0.0.1:23579)
     */
    String TRANS_SERVER_ADDRESS = "transport.address";
    /**
     * 应用名称
     */
    String TRANS_SERVER_NAME = "transport.name";
    /**
     * 推送服务器的端点(默认: uniform)
     */
    String TRANS_SERVER_POINT = "transport.point";
    /**
     * 推送服务器方式(默认: MQ)
     */
    String TRANS_SERVER_PROTOCOL = "transport.protocol";
    /**
     * 推送是否开启(默认: true)
     */
    String TRANS_SERVER_OPEN = "transport.open";


    String WS_PORT = "ws.port";
    String WHITE_ADDRESS = "white.address";
    String LOG4J = "org.slf4j.LoggerFactory";
    String DEFAULT_CONTEXT = "/agent";
    String DEFAULT_ADDRESS = "0.0.0.0,0:0:0:0:0:0:0:1,127.0.0.1";

    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_DETAIL);
    String DEFAULT_HOST = "0.0.0.0";
    Logger logger = Logger.getLogger("premain");

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
