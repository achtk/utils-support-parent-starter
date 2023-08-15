package com.chua.agent.support.http;

import com.chua.agent.support.json.JSON;
import com.chua.agent.support.json.JSONArray;
import com.chua.agent.support.json.JSONObject;
import com.chua.agent.support.plugin.HtmlPlugin;
import com.chua.agent.support.utils.ClassUtils;
import com.chua.agent.support.utils.HttpUtils;
import com.chua.agent.support.utils.ResourceUtils;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.chua.agent.support.store.AgentStore.getIntegerValue;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * spring servlet
 *
 * @author CH
 */
public abstract class AbstractSpringServlet extends AbstractServlet<Object, Object> {


    private final ThreadLocal<Object> request = new InheritableThreadLocal<>();
    private final ThreadLocal<JSONObject> parameter = new InheritableThreadLocal<>();


    public AbstractSpringServlet(HtmlPlugin plugin) {
        super(plugin);
    }

    @Override
    public Object getRequest() {
        return request.get();
    }

    @Override
    public JSONObject getParameter() {
        return super.getParameter();
    }

    @Override
    public void handle(Object request, Object response) throws IOException {
        if (!HttpUtils.isPass(request)) {
            HttpUtils.forbidden(request, response);
            return;
        }

        this.request.remove();
        this.request.set(request);
        this.plugin.setAddress(ClassUtils.invoke("getRemoteHost", request).toString());
        this.parameter.remove();
        JSONObject jsonObject = createJsonObject(request);
        this.parameter.set(jsonObject);
        this.plugin.setParameter(jsonObject);

        String url = ClassUtils.invoke("getRequestURI", request).toString();
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        Method method = methodMap.get(url);

        String contentPath = DEFAULT_CONTEXT;
        if (null == method) {
            String newUrl = url.substring(url.indexOf(contentPath) + contentPath.length() + 1);
            method = methodMap.get(newUrl);
        }

        if (null == method) {
            HttpUtils.sendText("404".getBytes(StandardCharsets.UTF_8), response);
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

        params.put("ws", getIntegerValue(WS_PORT, 10101));
        Object invoke = null;
        try {
            invoke = method.invoke(plugin.resolve());
        } catch (Exception ignored) {
            HttpUtils.sendText("404".getBytes(UTF_8), response);
            return;
        }

        if (invoke instanceof String) {
            if (!((String) invoke).endsWith(".html")) {
                HttpUtils.sendText(((String) invoke).getBytes(UTF_8), response);
                return;
            }

            String resource = ResourceUtils.getResource(invoke.toString(), Collections.emptyMap());
            if (null == resource) {
                HttpUtils.sendText("404".getBytes(UTF_8), response);
                return;
            }
            HttpUtils.sendHtml(invoke.toString(), params, response);
            return;
        }

        if (invoke instanceof byte[]) {
            if (event.contains(method)) {
                HttpUtils.sendEvent((byte[]) invoke, response);
            } else {
                HttpUtils.sendText((byte[]) invoke, response);
            }
            return;
        }


        HttpUtils.sendJson(JSON.toJSONBytes(invoke), response);
    }

    private JSONObject createJsonObject(Object request) {
        JSONObject jsonObject = new JSONObject();
        Map<String, String[]> parameterMap = (Map<String, String[]>) ClassUtils.invoke("getParameterMap", request);
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String[] value = entry.getValue();

            if (value.length == 0) {
                continue;
            }

            if (value.length > 1) {
                jsonObject.put(entry.getKey(), value);
                continue;
            }

            jsonObject.put(entry.getKey(), value[0]);
        }


        String contentType = (String) ClassUtils.invoke("getContentType", request);
        if (null != contentType && contentType.contains("application/json")) {
            try (InputStreamReader reader = new InputStreamReader((InputStream) ClassUtils.invoke("getInputStream", request));
                 Writer writer = new StringWriter();
            ) {
                int line = 0;
                char[] chars = new char[4096];
                while ((line = reader.read(chars)) > 0) {
                    writer.write(chars, 0, line);
                }

                writer.flush();
                String string = writer.toString();
                if (string.startsWith("{")) {
                    JSONObject parseObject = JSONObject.parseObject(string);
                    jsonObject.putAll(parseObject);
                } else {
                    JSONArray jsonArray = JSONObject.parseArray(string);
                    jsonObject.put("array", jsonArray);
                }

            } catch (Throwable ignored) {
            }
        }

        return jsonObject;
    }


}
