package com.chua.agent.support.server;

import java.time.LocalDateTime;

/**
 * http
 *
 * @author CH
 */
public class EmbedHttpServer implements EmbedServer {
    @Override
    public void start() {
        System.out.print(DATE_TIME_FORMATTER.format(LocalDateTime.now()) + " INFO  [Console] [1/main] open standard server port \r\n");

    }

    @Override
    public void stop() {

    }
}
