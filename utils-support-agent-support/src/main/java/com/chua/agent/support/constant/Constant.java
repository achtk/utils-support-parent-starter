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
    String LOG_LEVEL = "log.level";
    String LOG_OPEN = "log.open";
    String SERVER_TYPE = "embed.server";

    String TRANS_SERVER_ADDRESS = "transport.address";
    String TRANS_SERVER_POINT = "transport.point";
    String TRANS_SERVER_TYPE = "transport.type";


    String WS_PORT = "ws.port";
    String WHITE_ADDRESS = "white.address";
    String LOG4J = "org.slf4j.LoggerFactory";
    String DEFAULT_CONTEXT = "/agent";
    String DEFAULT_ADDRESS = "0.0.0.0,0:0:0:0:0:0:0:1,127.0.0.1";

    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_DETAIL);
    String DEFAULT_HOST = "0.0.0.0";
    Logger logger = Logger.getLogger("premain");
}
