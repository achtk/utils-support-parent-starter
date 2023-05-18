package com.chua.agent.support.http;


import com.chua.agent.support.Agent;
import com.chua.agent.support.plugin.AgentPlugin;
import com.chua.agent.support.plugin.HtmlAgentPlugin;
import com.chua.agent.support.utils.HttpUtils;
import com.chua.agent.support.utils.ResourceUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.chua.agent.support.Agent.CONTENT_PATH;
import static com.chua.agent.support.Agent.DEFAULT_CONTEXT;

/**
 * index
 *
 * @author CH
 * @since 2021-08-26
 */
public class IndexHttpExchangeHandler implements RequestHandler<HttpExchange, Object>, HttpHandler {

    @Override
    public void handle(HttpExchange exchange, Object response) throws IOException {
        if (!HttpUtils.isPass(exchange)) {
            HttpUtils.forbidden(exchange);
            return;
        }
        String url = exchange.getRequestURI().toString();
        String contentPath = Agent.getStringValue(CONTENT_PATH, DEFAULT_CONTEXT);
        Map<String, Object> params = new HashMap<>();
        params.put("url", url);
        params.put("contentPath", contentPath);
        StringBuilder stringBuilder = new StringBuilder();
        if (!Agent.plugins.isEmpty()) {
            stringBuilder.append("$('.dynamic').hide();\r\n");
            for (AgentPlugin plugin : Agent.plugins.values()) {
                if (plugin instanceof HtmlAgentPlugin) {
                    stringBuilder.append("$('.dynamic#").append(plugin.name()).append("').show();\r\n");
                }
            }
        }
        params.put("source", stringBuilder.toString());
        String resource = ResourceUtils.getResource("index.html", params);

        HttpUtils.sendIndexHtml(resource, exchange);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        handle(httpExchange, null);
    }
}
