package com.chua.agent.support.plugin;

import com.alibaba.json.JSONArray;
import com.alibaba.json.JSONObject;
import com.chua.agent.support.annotation.Path;
import com.chua.agent.support.reflectasm.FieldAccess;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * SpringBeanPlugin
 * @author CH
 */
public class SpringBeanPlugin implements HtmlAgentPlugin{

    private static final String NAME = "org.springframework.context.ApplicationContext";
    private static FieldAccess fieldAccess;
    public static Object beanFactory;
    protected JSONArray jsonArray;

    @Path("spring-bean")
    public String springBean() {
        return "spring-bean.html";
    }

    @Path("spring-bean-data")
    public String springBeanData() {
//        jsonArray = null;
        if (null == jsonArray) {
            synchronized (this) {
                if (null == jsonArray) {
                    Field field = fieldAccess.getFields()[6];
                    jsonArray = new JSONArray();
                    try {
                        Map map = (Map) field.get(beanFactory);
                        map.forEach((k, v) -> {
                            JSONObject item = new JSONObject();
                            jsonArray.add(item);

                            FieldAccess fieldAccess = FieldAccess.get(v.getClass());
                            Field[] fields = fieldAccess.getFields();
                            for (Field field1 : fields) {
                                String name1 = field1.getName();
                                Object o = null;
                                try {
                                    o = field1.get(v);
                                    item.put(name1, null == o ? null : o.toString());
                                } catch (Exception ignored) {
                                }
                                if (field1.getType().getTypeName().contains("org.springframework")) {
                                    if (null == o) {
                                        continue;
                                    }
                                    FieldAccess fieldAccess1 = null;
                                    try {
                                        fieldAccess1 = FieldAccess.get(o.getClass());
                                    } catch (Exception ignored) {
                                        continue;
                                    }
                                    for (Field access1Field : fieldAccess1.getFields()) {
                                        if (access1Field.getType().getTypeName().contains("java.lang")) {
                                            continue;
                                        }
                                        try {
                                            Object o1 = access1Field.get(o);
                                            if (null == o1) {
                                                continue;
                                            }
                                            item.put(name1 + "_" + access1Field.getName(), o1.toString());
                                        } catch (IllegalAccessException ignored) {
                                        }
                                    }
                                }
                            }
                            item.put("bean", k);
                        });
                    } catch (IllegalAccessException e) {
                        return "";
                    }
                }
            }
        }
        return jsonArray.toJSONString();
    }


    @RuntimeType
    public static Object before(@Origin Method method,
                                @This Object obj,
                                @AllArguments Object[] allArguments,
                                @SuperCall Callable<?> callable) throws Exception {

        return getBeanFactory(method, obj, allArguments, callable);
    }

    private static Object getBeanFactory(Method method, Object obj, Object[] allArguments, Callable<?> callable) throws Exception {
        Object call = callable.call();
        if (null == beanFactory) {
            Class<?> aClass = call.getClass();
            fieldAccess = FieldAccess.get(aClass);
            beanFactory = call;
        }
        return call;
    }


    @Override
    public void setAddress(String address) {

    }

    @Override
    public void setParameter(JSONObject parameter) {

    }

    @Override
    public String name() {
        return "spring-bean";
    }

    @Override
    public DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<?> transform(DynamicType.Builder<?> builder) {
        return builder.method(
                ElementMatchers.named("getBeanFactory")
        ).intercept(MethodDelegation.to(SpringBeanPlugin.class));
    }

    @Override
    public ElementMatcher<? super TypeDescription> type() {
        return ElementMatchers.hasSuperType(ElementMatchers.named(NAME));
    }
}
