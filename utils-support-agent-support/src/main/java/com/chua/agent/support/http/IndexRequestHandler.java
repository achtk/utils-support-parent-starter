package com.chua.agent.support.http;


import com.chua.agent.support.plugin.HtmlPlugin;
import com.chua.agent.support.plugin.Plugin;
import com.chua.agent.support.store.PluginStore;
import com.chua.agent.support.utils.ClassUtils;
import com.chua.agent.support.utils.HttpUtils;
import com.chua.agent.support.utils.ResourceUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * index
 *
 * @author CH
 * @since 2021-08-26
 */
public class IndexRequestHandler implements RequestHandler<Object, Object> {

    @Override
    public void handle(Object request, Object response) throws IOException {
        if (!HttpUtils.isPass(request)) {
            HttpUtils.forbidden(request, response);
            return;
        }
        String url = ClassUtils.invoke("getRequestURI", request).toString();
        String contentPath = DEFAULT_CONTEXT;
        Map<String, Object> params = new HashMap<>();
        params.put("url", url);
        int index = url.indexOf(contentPath);
        if (index == 0) {
            params.put("contentPath", contentPath);
        } else {
            params.put("contentPath", url.substring(0, index) + contentPath);
        }

        StringBuilder stringBuilder = new StringBuilder();
        if (!PluginStore.isEmpty()) {
            stringBuilder.append("$('.dynamic').hide();\r\n");
            for (Plugin plugin : PluginStore.getPlugin(HtmlPlugin.class)) {
                stringBuilder.append("$('.dynamic#").append(plugin.name()).append("').show();\r\n");
            }
        }
        params.put("source", stringBuilder.toString());
        String resource = ResourceUtils.getResource("index.html", params);

        HttpUtils.sendIndexHtml(resource, response);
    }
}
