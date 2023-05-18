package com.chua.agent.support.utils;

import com.chua.agent.support.Agent;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.chua.agent.support.Agent.DEFAULT_ADDRESS;
import static com.chua.agent.support.Agent.WHITE_ADDRESS;

/**
 * http工具
 *
 * @author CH
 * @since 2021-08-18
 */
public class HttpUtils {

    /**
     * 是否允许
     *
     * @param request 请求
     * @return 是否允许
     */
    public static boolean isPass(Object request) {
        String hostName = ClassUtils.invoke("getRemoteHost", request).toString();
        String s = Agent.getStringValue(WHITE_ADDRESS, DEFAULT_ADDRESS);
        String[] split = s.split(",");

        return Arrays.asList(split).contains(hostName);
    }

    /**
     * 是否允许
     *
     * @param exchange 交换机
     * @return 是否允许
     */
    public static boolean isPass(HttpExchange exchange) {
        String hostName = exchange.getRemoteAddress().getAddress().getHostAddress();
        String s = Agent.getStringValue(WHITE_ADDRESS, DEFAULT_ADDRESS);
        String[] split = s.split(",");

        return Arrays.asList(split).contains(hostName);
    }

    /**
     * 拒绝
     *
     * @param exchange 交换机
     */
    public static void forbidden(HttpExchange exchange) {
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set("Cache-Control", "no-cache");
        responseHeaders.set("Access-Control-Allow-Origin", "*");

        try (OutputStream os = exchange.getResponseBody()) {
            responseHeaders.set("Keep-Alive", "timeout=30");
            responseHeaders.set("Date", new Date().toString());
            responseHeaders.set("Accept-Encoding", "gzip,deflate");
            responseHeaders.set("Content-Type", "text/plain;charset=UTF-8");
            exchange.sendResponseHeaders(403, 0);
        } catch (IOException ignored) {
        }
    }

    /**
     * 拒绝
     *
     * @param request 交换机
     */
    public static void forbidden(Object request, Object response) {
        ClassUtils.invoke("setHeader", response, "Cache-Control", "no-cache");
        ClassUtils.invoke("setHeader", response, "Access-Control-Allow-Origin", "*");
        ClassUtils.invoke("setHeader", response, "Keep-Alive", "timeout=30");
        ClassUtils.invoke("setHeader", response, "Date", new Date().toString());
        ClassUtils.invoke("setHeader", response, "Accept-Encoding", "gzip,deflate");
        ClassUtils.invoke("setHeader", response, "Content-Type", "text/plain;charset=UTF-8");
        ClassUtils.invoke("setStatus", response, 403);
    }

    /**
     * 获取参数
     *
     * @param exchange 交换机
     * @param key      索引
     * @return 值
     */
    public static String getRequestParameter(HttpExchange exchange, String key) {
        return getRequestParameter(exchange).get(key);
    }

    /**
     * 获取参数
     *
     * @param exchange 交换机
     * @return 值
     */
    public static Map<String, String> getRequestParameter(HttpExchange exchange) {
        Map<String, String> params = new HashMap<>();
        String query = exchange.getRequestURI().getQuery();
        if (null == query) {
            return null;
        }
        for (String s : query.split("&")) {
            String[] split = s.split("=");
            if (split.length == 1) {
                params.put(split[0], null);
            } else {
                params.put(split[0], split[1]);
            }
        }
        return params;
    }

    /**
     * 發送消息
     *
     * @param bytes    數據
     * @param exchange 交換機
     */
    public static void sendEvent(byte[] bytes, HttpExchange exchange) {
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set("Cache-Control", "no-cache");
        responseHeaders.set("Access-Control-Allow-Origin", "*");
        try (OutputStream os = exchange.getResponseBody()) {
            responseHeaders.set("Keep-Alive", "timeout=30");
            responseHeaders.set("Date", new Date().toString());
            responseHeaders.set("Accept-Encoding", "gzip,deflate");
            responseHeaders.set("Content-Type", "text/event-stream;charset=UTF-8");
            exchange.sendResponseHeaders(200, 0);
            os.write(bytes);
        } catch (IOException ignored) {
        }

    }

    /**
     * 發送消息
     *
     * @param bytes    數據
     * @param response 响应
     */
    public static void sendEvent(byte[] bytes, Object response) {
        ClassUtils.invoke("setHeader", response, "Cache-Control", "no-cache");
        ClassUtils.invoke("setHeader", response, "Access-Control-Allow-Origin", "*");


        try (OutputStream os = (OutputStream) ClassUtils.invoke("getOutputStream", response)) {
            ClassUtils.invoke("setHeader", response, "Keep-Alive", "timeout=30");
            ClassUtils.invoke("setHeader", response, "Date", new Date().toString());
            ClassUtils.invoke("setHeader", response, "Accept-Encoding", "gzip,deflate");
            ClassUtils.invoke("setHeader", response, "Content-Type", "text/event-stream;charset=UTF-8");
            ClassUtils.invoke("setStatus", response, 200);
            os.write(bytes);
        } catch (IOException ignored) {
        }

    }

    /**
     * 发送html
     *
     * @param html     html
     * @param args     参数
     * @param response 交换机
     */
    public static void sendHtml(String html, Map<String, Object> args, Object response) {
        ClassUtils.invoke("setHeader", response, "Cache-Control", "no-cache");
        ClassUtils.invoke("setHeader", response, "Access-Control-Allow-Origin", "*");


        try (OutputStream os = (OutputStream) ClassUtils.invoke("getOutputStream", response)) {
            ClassUtils.invoke("setHeader", response, "Keep-Alive", "timeout=30");
            ClassUtils.invoke("setHeader", response, "Date", new Date().toString());
            ClassUtils.invoke("setHeader", response, "Accept-Encoding", "gzip,deflate");
            ClassUtils.invoke("setHeader", response, "Content-Type", "text/html;charset=UTF-8");
            ClassUtils.invoke("setStatus", response, (int) 200);
            os.write(ResourceUtils.getResource(html, args).getBytes(StandardCharsets.UTF_8));
        } catch (IOException ignored) {
        }

    }

    /**
     * 發送消息
     *
     * @param bytes    數據
     * @param exchange 交換機
     */
    public static void sendIndexHtml(String content, HttpExchange exchange) {
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set("Cache-Control", "no-cache");
        responseHeaders.set("Access-Control-Allow-Origin", "*");
        try (OutputStream os = exchange.getResponseBody()) {
            responseHeaders.set("Keep-Alive", "timeout=30");
            responseHeaders.set("Date", new Date().toString());
            responseHeaders.set("Accept-Encoding", "gzip,deflate");
            responseHeaders.set("Content-Type", "text/html;charset=UTF-8");
            exchange.sendResponseHeaders(200, 0);
            os.write(content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException ignored) {
        }

    }
    /**
     * 发送html
     *
     * @param html     html
     * @param response 交换机
     */
    public static void sendIndexHtml(String html, Object response) {
        ClassUtils.invoke("setHeader", response, "Cache-Control", "no-cache");
        ClassUtils.invoke("setHeader", response, "Access-Control-Allow-Origin", "*");


        try (OutputStream os = (OutputStream) ClassUtils.invoke("getOutputStream", response)) {
            ClassUtils.invoke("setHeader", response, "Keep-Alive", "timeout=30");
            ClassUtils.invoke("setHeader", response, "Date", new Date().toString());
            ClassUtils.invoke("setHeader", response, "Accept-Encoding", "gzip,deflate");
            ClassUtils.invoke("setHeader", response, "Content-Type", "text/html;charset=UTF-8");
            ClassUtils.invoke("setStatus", response, (int) 200);
            os.write(html.getBytes(StandardCharsets.UTF_8));
        } catch (IOException ignored) {
        }

    }

    /**
     * 发送html
     *
     * @param html     html
     * @param args     参数
     * @param exchange 交换机
     */
    public static void sendHtml(String html, Map<String, Object> args, HttpExchange exchange) {
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set("Cache-Control", "no-cache");
        responseHeaders.set("Access-Control-Allow-Origin", "*");
        try (OutputStream os = exchange.getResponseBody()) {
            responseHeaders.set("Keep-Alive", "timeout=30");
            responseHeaders.set("Date", new Date().toString());
            responseHeaders.set("Accept-Encoding", "gzip,deflate");
            responseHeaders.set("Content-Type", "text/html;charset=UTF-8");
            exchange.sendResponseHeaders(200, 0);
            os.write(ResourceUtils.getResource(html, args).getBytes(StandardCharsets.UTF_8));
        } catch (IOException ignored) {
        }

    }

    /**
     * 发送text
     *
     * @param bytes    数据
     * @param exchange 交换机
     */
    public static void sendText(byte[] bytes, HttpExchange exchange) {
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set("Cache-Control", "no-cache");
        responseHeaders.set("Access-Control-Allow-Origin", "*");

        try (OutputStream os = exchange.getResponseBody()) {
            responseHeaders.set("Keep-Alive", "timeout=30");
            responseHeaders.set("Date", new Date().toString());
            responseHeaders.set("Accept-Encoding", "gzip,deflate");
            responseHeaders.set("Content-Type", "text/plain;charset=UTF-8");
            exchange.sendResponseHeaders(200, 0);

            os.write(bytes);
        } catch (IOException ignored) {
        }
    }

    /**
     * 发送 json
     *
     * @param bytes    数据
     * @param exchange 交换机
     */
    public static void sendJson(byte[] bytes, HttpExchange exchange) {
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set("Cache-Control", "no-cache");
        responseHeaders.set("Access-Control-Allow-Origin", "*");

        try (OutputStream os = exchange.getResponseBody()) {
            responseHeaders.set("Keep-Alive", "timeout=30");
            responseHeaders.set("Date", new Date().toString());
            responseHeaders.set("content-length", bytes.length + "");
            responseHeaders.set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, 0);

            os.write(bytes);
        } catch (IOException ignored) {
        }
    }

    /**
     * 发送 json
     *
     * @param bytes    数据
     * @param response 交换机
     */
    public static void sendJson(byte[] bytes, Object response) {
        ClassUtils.invoke("setHeader", response, "Cache-Control", "no-cache");
        ClassUtils.invoke("setHeader", response, "Access-Control-Allow-Origin", "*");


        try (OutputStream os = (OutputStream) ClassUtils.invoke("getOutputStream", response)) {
            ClassUtils.invoke("setHeader", response, "Keep-Alive", "timeout=30");
            ClassUtils.invoke("setHeader", response, "Date", new Date().toString());
            ClassUtils.invoke("setHeader", response, "Accept-Encoding", "gzip,deflate");
            ClassUtils.invoke("setHeader", response, "Content-Type", "application/json;charset=UTF-8");
            ClassUtils.invoke("setStatus", response, 200);
            os.write(bytes);
        } catch (IOException ignored) {
        }
    }

    /**
     * 发送text
     *
     * @param bytes    数据
     * @param response 响应
     */
    public static void sendText(byte[] bytes, Object response) {

        ClassUtils.invoke("setHeader", response, "Cache-Control", "no-cache");
        ClassUtils.invoke("setHeader", response, "Access-Control-Allow-Origin", "*");


        try (OutputStream os = (OutputStream) ClassUtils.invoke("getOutputStream", response)) {
            ClassUtils.invoke("setHeader", response, "Keep-Alive", "timeout=30");
            ClassUtils.invoke("setHeader", response, "Date", new Date().toString());
            ClassUtils.invoke("setHeader", response, "Accept-Encoding", "gzip,deflate");
            ClassUtils.invoke("setHeader", response, "Content-Type", "text/plain;charset=UTF-8");
            ClassUtils.invoke("setStatus", response, 200);
            os.write(bytes);
        } catch (IOException ignored) {
        }
    }
}
