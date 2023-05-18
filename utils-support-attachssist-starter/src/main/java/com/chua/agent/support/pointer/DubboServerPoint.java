package com.chua.agent.support.pointer;


import com.alibaba.json.JSON;
import com.alibaba.json.JSONObject;
import com.chua.agent.support.annotation.Spi;
import com.chua.agent.support.reflectasm.FieldAccess;
import com.chua.agent.support.span.span.Span;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * dubbo
 *
 * @author CH
 * @since 2022-02-09
 */
@Spi("dubbo")
public class DubboServerPoint implements ServerPoint {

    private static final Pattern HOST = Pattern.compile("host=\'(.*?)\'");
    private static final Pattern PORT = Pattern.compile("\"port\":(.*?)");
    private static final Pattern SERVICE_NAME = Pattern.compile("serviceName=\'(.*?)\'");
    private static final Pattern PARAM = Pattern.compile("dubbo.metadata-service.url-params=(.*?)\\}");
    @Override
    public List<Span> doAnalysis(Object[] objects, Method method, Object obj, Span span) {
        try {
            Object object = objects[0];
            Class<?> aClass = object.getClass();
            object = FieldAccess.get(aClass).get(object, 0);
            Object bean = FieldAccess.get(object.getClass()).get(object, 0);
            String s1 = bean.toString();
            Matcher matcher = HOST.matcher(s1);
            String host = null;
            if(matcher.find()) {
                host = matcher.group(1);
            }

            String port = null;
            Matcher matcher3 = PARAM.matcher(s1);
            if(matcher3.find()) {
                JSONObject jsonObject = JSON.parseObject(matcher3.group(1) + "}");
                port = jsonObject.getString("port");
            }

            Matcher matcher2 = SERVICE_NAME.matcher(s1);
            String serviceName = null;
            if(matcher2.find()) {
                serviceName = matcher2.group(1);
            }


            span.setMessage(host + ":" + port);
            span.setType("dubbo(" + span.getMessage() + ")["+ serviceName +"]");
            span.setId(span.getType());
            span.setEx("localhost");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String[] filterType() {
        return new String[]{"org.apache.dubbo.rpc.cluster.support.AbstractClusterInvoker"};
    }

    @Override
    public String[] filterMethod() {
        return new String[]{"invokeWithContext"};
    }
}
