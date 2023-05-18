package com.chua.agent.support.pointer;


import com.alibaba.json.JSON;
import com.alibaba.json.JSONObject;
import com.chua.agent.support.annotation.Spi;
import com.chua.agent.support.span.span.Span;

import java.lang.reflect.Method;
import java.util.List;

/**
 * mysql
 *
 * @author CH
 */
@Spi("mysql")
public class MysqlServerPoint implements ServerPoint {
    @Override
    public List<Span> doAnalysis(Object[] objects, Method method, Object obj, Span span) {
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(objects[0]));
        span.setMessage(jsonObject.getString("host") + ":" + jsonObject.getString("port"));
        span.setType("mysql(" + span.getMessage() + ")["+ objects[3].toString() +"]");
        span.setId(span.getType());
        span.setDb(objects[3].toString());
        span.setEx("localhost");

        span.setMethod(objects[1].toString());
        span.setTypeMethod(objects[2].toString());


        return null;
    }

    @Override
    public String[] filterType() {
        return new String[]{"com.mysql.cj.Session"};
    }

    @Override
    public String[] filterMethod() {
        return new String[]{"connect", "bind", "initializeSocket"};
    }
}
