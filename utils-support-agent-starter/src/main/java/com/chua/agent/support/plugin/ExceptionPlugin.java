package com.chua.agent.support.plugin;

import com.chua.agent.support.span.NewTrackManager;
import com.chua.agent.support.span.Span;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;

/**
 * 异常检测插件
 *
 * @author CH
 */
public class ExceptionPlugin implements Plugin {
    @Override
    public String name() {
        return "exception";
    }

    //@RuntimeType
    public static void register(Object bean) {
        if (bean instanceof ClassNotFoundException) {
            return;
        }
        Span span = NewTrackManager.getLastSpan();

        if (null == span) {
            return;
        }

        Exception exception = (Exception) bean;
        if (exception.getClass().getTypeName().equalsIgnoreCase(span.getType())) {
            return;
        }


        if (exception instanceof ArrayIndexOutOfBoundsException) {
            return;
        }

        String s1 = exception.toString();
        if (s1.contains("xrebel")) {
            return;
        }

        if (s1.contains("java.lang.NoSuch")) {
            return;
        }


        if (s1.contains("javax.") || s1.contains("java.security.SignatureException")) {
            return;
        }


        Span sql1 = new Span();
        sql1.setLinkId(span.getLinkId());
        sql1.setPid(span.getId());
        sql1.setEnterTime(new Date());
        sql1.setId(UUID.randomUUID().toString());
        sql1.setMessage(exception.toString());
        sql1.setMethod(sql1.getMessage());
        sql1.setHeader(Collections.singletonList(exception.getLocalizedMessage()));
        String s = exception.toString();
        if (s.contains("<") || s.contains(">") || s.contains("=") || s.contains("\n")) {
            sql1.setTypeMethod(s);
        } else {
            sql1.setTypeMethod("<span style='color: red; font-size:1000;'>" + s + "</span>");
        }
        sql1.setModel("exception");
        sql1.setType(exception.getClass().getTypeName());
        if (sql1.getTypeMethod().equals(span.getTypeMethod())) {
            return;
        }
        NewTrackManager.registerSpan(sql1);
    }

    @Override
    public DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<?> transform(DynamicType.Builder<?> builder) {
        return null;
    }

    @Override
    public ElementMatcher<? super TypeDescription> type() {
        return null;
    }
}
