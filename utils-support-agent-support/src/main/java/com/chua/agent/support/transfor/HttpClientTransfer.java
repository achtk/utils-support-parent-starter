package com.chua.agent.support.transfor;

import com.chua.agent.support.json.JSON;
import com.chua.agent.support.json.JSONArray;
import com.chua.agent.support.json.JSONObject;
import com.chua.agent.support.reflectasm.FieldAccess;
import com.chua.agent.support.span.Span;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * httpclient
 *
 * @author CH
 */
public class HttpClientTransfer implements Transfer {
    @Override
    public String name() {
        return "org.apache.http.impl.client.InternalHttpClient";
    }

    @Override
    public void transfer(Object[] params, List<Span> spans1) {
        List<String> stackTrace = new ArrayList<>();
        Span span = spans1.get(spans1.size() - 1);
        String desc = "[Apache-HttpClient (";
        if (params.length == 2) {
            desc += params[0].toString() + ") ";
            Object param = params[1];
            if (null != param) {
                analysis2(param, stackTrace);
            } else {
                analysis1(params[0], stackTrace);
            }
        } else if (params.length == 3) {
            desc += params[1].toString() + ") ";
            analysis1(params[1], stackTrace);
        }
        desc += "]";

        span.setStack(stackTrace);
        span.setMethod(span.getMethod() + "  <span style='color: blue'>" + desc + "</span>");
        span.setEx(desc);
    }

    private void analysis1(Object param, List<String> stackTrace) {
        FieldAccess fieldAccess = null;
        if(null != param && "org.apache.http.client.methods.HttpPost".equalsIgnoreCase(param.getClass().getTypeName())) {
            try {
                fieldAccess = FieldAccess.get(param.getClass());
                Field field = fieldAccess.getFields()[5];
                field.setAccessible(true);
                for(String item : field.get(param).toString()
                        .replace("[", "")
                        .replace("]", "")
                        .split(",")) {
                    stackTrace.add(item.trim());
                }
            } catch (Exception ignored) {
                return;
            }
            return;
        }
        try {
            fieldAccess = FieldAccess.get(Class.forName("org.apache.http.message.AbstractHttpMessage"));
        } catch (ClassNotFoundException ignored) {
            return;
        }
        JSONObject jsonObject = (JSONObject) JSON.toJSON(fieldAccess.get(param, 0));
        JSONObject params1 = (JSONObject) JSON.toJSON(fieldAccess.get(param, 1));

        stackTrace.add("<strong class='node-details__name collapse-handle'>Header</strong>");
        JSONArray allHeaders = jsonObject.getJSONArray("allHeaders");
        allHeaders.forEach(o1 -> {
            JSONObject jsonObject1 = (JSONObject) o1;
            stackTrace.add(jsonObject1.getString("name") + ": " + jsonObject1.getString("value"));
        });

        stackTrace.add("<strong class='node-details__name collapse-handle'>Params</strong>");
        params1.forEach((k, v) -> {
            Object value = v;
            if (v instanceof JSONObject) {
                value = ((JSONObject) v).toJSONString();
            } else if (v instanceof JSONArray) {
                value = ((JSONArray) v).toJSONString();
            }
            stackTrace.add(k + ": " + value);
        });


    }

    private void analysis2(Object param, List<String> stackTrace) {
        Class<?> aClass = param.getClass();
        try {
            FieldAccess fieldAccess = FieldAccess.get(aClass.getSuperclass());
            Object o = fieldAccess.get(param, "context");
            FieldAccess access = FieldAccess.get(o.getClass());
            Object map1 = access.get(o, "map");
            JSONObject jsonObject = new JSONObject((Map) map1);
            JSONObject object = jsonObject.getJSONObject("http.request");
            JSONObject requestConfig = jsonObject.getJSONObject("http.request-config");
            stackTrace.add("<strong class='node-details__name collapse-handle'>Header</strong>");
            JSONArray allHeaders = object.getJSONArray("allHeaders");
            allHeaders.forEach(o1 -> {
                JSONObject jsonObject1 = (JSONObject) o1;
                stackTrace.add(jsonObject1.getString("name") + ": " + jsonObject1.getString("value"));
            });


            stackTrace.add("<strong class='node-details__name collapse-handle'>Params</strong>");
            JSONObject params1 = object.getJSONObject("params");
            params1.forEach((k, v) -> {
                Object value = v;
                if (v instanceof JSONObject) {
                    value = ((JSONObject) v).toJSONString();
                } else if (v instanceof JSONArray) {
                    value = ((JSONArray) v).toJSONString();
                }
                stackTrace.add(k + ": " + value);
            });


            stackTrace.add("<strong class='node-details__name collapse-handle'>Request-Config</strong>");

            requestConfig.forEach((k, v) -> {
                stackTrace.add(k + ": " + v);
            });
        } catch (Exception ignored) {
        }
    }
}
