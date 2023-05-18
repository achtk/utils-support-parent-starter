package com.chua.agent.support.log;

import com.chua.agent.support.span.span.Span;
import com.chua.agent.support.utils.StringUtils;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * file存储
 * @author CH
 */
public class FileLogResolver implements LogResolver {

    private static final String LOG_FILE = System.getProperty("agent.log", "./.log") + "/agent.log";
    private static final Path PATH = Paths.get(LOG_FILE);

    static {
        if(!Files.exists(PATH.getParent())) {
            try {
                Files.createDirectories(PATH.getParent());
            } catch (IOException ignored) {
            }
        }
    }

    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void register(Span span) {
        try {
            if(Files.exists(PATH)) {
                Files.write(PATH, StringUtils.utf8Bytes(LocalDateTime.now().format(DATE_TIME_FORMATTER) + " [Agent] " + createMessage(span)+ "\r\n"), StandardOpenOption.APPEND);
            } else {
                Files.write(PATH, StringUtils.utf8Bytes(LocalDateTime.now().format(DATE_TIME_FORMATTER) + " [Agent] " + createMessage(span)+ "\r\n"), StandardOpenOption.CREATE);
            }
        } catch (IOException ignored) {
        }
    }

    @Override
    public void register(Object message) {
        try {
            Files.write(PATH, StringUtils.utf8Bytes(LocalDateTime.now().format(DATE_TIME_FORMATTER) + " [Agent] " + message.toString()  + "\r\n"), StandardOpenOption.APPEND);
        } catch (IOException ignored) {
        }
    }

    /**
     * 消息
     * @param span span
     * @return 消息
     */
    private String createMessage(Span span) {
        return span.getMessage();
    }
}
