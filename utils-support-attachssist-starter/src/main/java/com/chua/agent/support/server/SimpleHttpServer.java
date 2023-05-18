package com.chua.agent.support.server;

import com.chua.agent.support.Agent;
import com.chua.agent.support.http.IndexHttpExchangeHandler;
import com.chua.agent.support.http.ResourceHttpExchangeHandler;
import com.chua.agent.support.http.SimpleHttpExchangeHttpHandler;
import com.chua.agent.support.http.SimpleHttpHandler;
import com.chua.agent.support.plugin.HtmlAgentPlugin;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.*;
import java.util.logging.Level;

/**
 * 简单服务器
 *
 * @author CH
 */
public class SimpleHttpServer {
    private final String host;
    private final int port;
    private final String context;
    private HttpServer httpServer;
    final ExecutorService es = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(), r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    public SimpleHttpServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.context = Agent.getStringValue(Agent.CONTENT_PATH, Agent.DEFAULT_CONTEXT);
    }


    public void start() {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(host, port), 100);
            Agent.log(Level.INFO, "open sun server port: {}", port);
            httpServer.setExecutor(es);
            Agent.log(Level.INFO, "---------------------------------------------------------------", port);

            ResourceHttpExchangeHandler resourceHttpExchangeHandler = new ResourceHttpExchangeHandler();
            httpServer.createContext(context + "/resources/", httpExchange -> resourceHttpExchangeHandler.handle(httpExchange, null));
            httpServer.createContext(context + "/resource/", httpExchange -> resourceHttpExchangeHandler.handle(httpExchange, null));

            IndexHttpExchangeHandler indexHttpExchangeHandler = new IndexHttpExchangeHandler();
            httpServer.createContext(context + "/index", httpExchange -> indexHttpExchangeHandler.handle(httpExchange, null));
            Agent.log(Level.INFO, "注册地址: {}/resource/*", context);
            Agent.log(Level.INFO, "注册地址: {}/index", context);

            for (HtmlAgentPlugin htmlAgentPlugin : Agent.getPlugin(HtmlAgentPlugin.class)) {
                SimpleHttpExchangeHttpHandler httpHandler = new SimpleHttpExchangeHttpHandler(htmlAgentPlugin);
                String[] paths = httpHandler.getPath();
                if (null == paths) {
                    continue;
                }
                for (String path : paths) {
                    httpServer.createContext(context + "/" + path, httpExchange -> httpHandler.handle(httpExchange, null));
                    Agent.log(Level.INFO, "注册地址: {}/{}", context, path);
                }
            }
            Agent.log(Level.INFO, "---------------------------------------------------------------", context);

            httpServer.start();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                es.shutdownNow();
                try {
                    httpServer.stop(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                es.shutdownNow();
            }));
        } catch (IOException ignored) {
        }
    }
}
