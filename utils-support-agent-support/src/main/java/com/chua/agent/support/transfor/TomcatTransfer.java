package com.chua.agent.support.transfor;

import com.chua.agent.support.span.Span;
import com.chua.agent.support.utils.ClassUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

/**
 * tomcat
 *
 * @author CH
 */
public class TomcatTransfer implements Transfer {
    @Override
    public String name() {
        return "org.apache.catalina.core.StandardHostValve";
    }

    @Override
    public void transfer(Object[] params, List<Span> spans1) {
        List<String> stackTrace = new ArrayList<>();
        Optional<Span> optionalSpan = spans1.stream().filter(it -> name().equalsIgnoreCase(it.getType())).findAny();
        if(!optionalSpan.isPresent()) {
            return;
        }

        Span span = optionalSpan.get();
        String desc = "[Tomcat (";
        if (params.length == 2 && "Request".equals(params[0].getClass().getSimpleName())) {
            Object obj1 = params[0];

            desc += ClassUtils.invoke("getMethod", obj1).toString() + ") ";
            desc += ClassUtils.invoke("getProtocol", obj1).toString() + " " + ClassUtils.invoke("getRequestURI", obj1).toString();
            desc += "] [Content-Type: " + (null == ClassUtils.invoke("getContentType", obj1) ? "none" : ClassUtils.invoke("getContentType", obj1).toString()) + " ]";

            Enumeration<String> headerNames = (Enumeration<String>) ClassUtils.invoke("getHeaderNames", obj1);
            stackTrace.add("<strong>" + ClassUtils.invoke("getRequestURI", obj1) + "</strong>");

            stackTrace.add("<strong>Request Header</strong>");
            while (headerNames.hasMoreElements()) {
                String element = headerNames.nextElement();
                String header = (String) ClassUtils.invoke("getHeader", obj1, element);
                stackTrace.add(element + ": " + header.replace("\"", "'"));
            }

            Object param = params[0];
            Object getInputStream = ClassUtils.invoke("getInputStream", param);

            try ( ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                  ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);){
                outputStream.writeObject(getInputStream);
                byte[] bytes = byteArrayOutputStream.toByteArray();
                try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
                    Object o = objectInputStream.readObject();
                    System.out.println();
                }
            } catch (Exception e) {
            }

            stackTrace.add("<strong>Location</strong>");
            Enumeration<Locale> locales = (Enumeration<Locale>) ClassUtils.invoke("getLocales", obj1);
            while (locales.hasMoreElements()) {
                Locale locale = locales.nextElement();
                stackTrace.add("locale: " + locale.toString());
            }

            stackTrace.add("<strong>Query</strong>");
            String queryString = (String) ClassUtils.invoke("getQueryString", obj1);
            if (null != queryString) {
                String[] split = queryString.split("&");
                for (String s : split) {
                    String[] split1 = s.split("=");
                    if (split1.length == 1) {
                        stackTrace.add(split1[0]);
                    } else if (split1.length == 2) {
                        stackTrace.add(split1[0] + ": " + split1[1]);
                    } else {
                        StringJoiner stringJoiner = new StringJoiner("=");
                        for (int i = 1; i < split1.length; i++) {
                            String s1 = split1[i];
                            stringJoiner.add(s1);
                        }
                        stackTrace.add(split1[0] + ": " + stringJoiner.toString());
                    }
                }
            }
            stackTrace.add("<strong>Params</strong>");
            Enumeration<String> parameterNames = (Enumeration<String>) ClassUtils.invoke("getParameterNames", obj1);
            while (parameterNames.hasMoreElements()) {
                String element = parameterNames.nextElement();
                stackTrace.add(element + ": " + ClassUtils.invoke("getParameter", obj1, element));
            }
        } else {
            desc += "]";
        }

        span.setHeader(stackTrace);

        span.setEx(desc);
    }
}
