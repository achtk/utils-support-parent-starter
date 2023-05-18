package com.chua.agent.support.handler;

import com.chua.agent.support.utils.ClassUtils;
import com.chua.agent.support.utils.StringUtils;

import static com.chua.agent.support.constant.Constant.LINK_ID;
import static com.chua.agent.support.constant.Constant.LINK_PID;

/**
 * DubboProxyInterceptorHandler
 * @author CH
 */
public class SpringInterceptorHandler {

    public static boolean hasLinkId(Object[] objects) {
        return !StringUtils.isNullOrEmpty(getLinkId(objects));
    }

    public static String getLinkId(Object[] objects) {
        if(objects.length > 0) {
            Object object = objects[0];
            if ("org.springframework.web.context.request.ServletWebRequest".equals(object.getClass().getTypeName())) {
                object = ClassUtils.getObject("request", object);
            }

            if (null != object && "javax.servlet.http.HttpServletRequest".equals(object.getClass().getTypeName())) {
                return (String) ClassUtils.invoke("getHeader", object, LINK_ID);
            }


        }

        return null;
    }

    public static Object getResponse(Object[] objects) {
        if (objects.length > 1) {
            Object object = objects[1];
            if (null != object && "javax.servlet.http.HttpServletResponse".equals(object.getClass().getTypeName())) {
                return object;
            }
            object = objects[0];
            if ("org.springframework.web.context.request.ServletWebRequest".equals(object.getClass().getTypeName())) {
                object = ClassUtils.getObject("response", object);
            }

            if (null != object && "javax.servlet.http.HttpServletResponse".equals(object.getClass().getTypeName())) {
                return object;
            }
        }

        return null;
    }

    public static boolean hasLinkParentId(Object[] args) {
        return !StringUtils.isNullOrEmpty(getLinkParentId(args));

    }

    public static String getLinkParentId(Object[] args) {
        if(args.length > 0) {
            Object object = args[0];
            if (null == object) {
                return null;
            }
            if ("org.springframework.web.context.request.ServletWebRequest".equals(object.getClass().getTypeName())) {
                object = ClassUtils.getObject("request", object);
            }

            if (null != object && "javax.servlet.http.HttpServletRequest".equals(object.getClass().getTypeName())) {
                return ClassUtils.invoke("getHeader", object, LINK_PID).toString();
            }


        }

        return null;
    }
}
