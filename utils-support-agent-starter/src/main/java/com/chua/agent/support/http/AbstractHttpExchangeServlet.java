package com.chua.agent.support.http;

import com.chua.agent.support.json.JSON;
import com.chua.agent.support.json.JSONObject;
import com.chua.agent.support.plugin.HtmlPlugin;
import com.chua.agent.support.utils.HttpUtils;
import com.chua.agent.support.utils.NetAddress;
import com.chua.agent.support.utils.ResourceUtils;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.chua.agent.support.store.AgentStore.getIntegerValue;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * spring servlet
 *
 * @author CH
 */
public abstract class AbstractHttpExchangeServlet extends AbstractServlet<HttpExchange, Object> {

    protected ThreadLocal<HttpExchange> request = new InheritableThreadLocal<>();

    public AbstractHttpExchangeServlet(HtmlPlugin plugin) {
        super(plugin);
    }


    @Override
    public HttpExchange getRequest() {
        return request.get();
    }

    @Override
    public void handle(HttpExchange exchange, Object response) throws IOException {
        if (!HttpUtils.isPass(exchange)) {
            HttpUtils.forbidden(exchange);
            return;
        }

        this.request.remove();
        this.request.set(exchange);
        JSONObject jsonObject = new JSONObject(NetAddress.of(exchange.getRequestURI().toString()).parametric());
        this.plugin.setParameter(jsonObject);

        String url = exchange.getRequestURI().getPath();
        Method method = methodMap.get(url);
        String contentPath = DEFAULT_CONTEXT;
        if (null == method) {
            method = methodMap.get(url.replace(contentPath, "").substring(1));
        }

        if (null == method) {
            HttpUtils.sendText("404".getBytes(StandardCharsets.UTF_8), exchange);
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.putAll(jsonObject);
        params.put("url", url);
        int index1 = url.indexOf(contentPath);
        if (index1 == 0) {
            params.put("contentPath", contentPath);
        } else {
            params.put("contentPath", url.substring(0, index1) + contentPath);
        }
        params.put("ws", getIntegerValue(WS_PORT, -1));

        Object invoke = null;
        try {
            invoke = method.invoke(plugin.resolve());
        } catch (Exception ignored) {
            HttpUtils.sendText("404".getBytes(UTF_8), exchange);
            return;
        }
        if (invoke instanceof String) {
            if (!((String) invoke).endsWith(".html")) {
                HttpUtils.sendText(((String) invoke).getBytes(UTF_8), exchange);
                return;
            }

            String resource = ResourceUtils.getResource(invoke.toString(), params);
            if (null == resource) {
                HttpUtils.sendText("404".getBytes(UTF_8), exchange);
                return;
            }
            HttpUtils.sendHtml(invoke.toString(), params, exchange);
            return;
        }


        if (invoke instanceof byte[]) {
            if (event.contains(method)) {
                HttpUtils.sendEvent((byte[]) invoke, exchange);
            } else {
                HttpUtils.sendText((byte[]) invoke, exchange);
            }
            return;
        }

        HttpUtils.sendJson(JSON.toJSONBytes(invoke), exchange);
    }

}
