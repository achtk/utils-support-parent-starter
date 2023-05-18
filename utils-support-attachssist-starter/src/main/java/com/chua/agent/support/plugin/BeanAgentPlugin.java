package com.chua.agent.support.plugin;

import com.alibaba.json.JSON;
import com.alibaba.json.JSONArray;
import com.alibaba.json.JSONObject;
import com.chua.agent.support.annotation.Path;
import lombok.Data;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;

import javax.management.*;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 句柄
 *
 * @author CH
 */
public class BeanAgentPlugin implements HtmlAgentPlugin {

    public static final BeanAgentPlugin INSTANCE = new BeanAgentPlugin();

    MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();

    public static DecimalFormat format = new DecimalFormat("###.000");
    private JSONObject parameter;


    @Path("mbean")
    public String html() {
        return "mbean.html";
    }


    @Path("method")
    public String method() {
        return "method.html";
    }


    @Path("mbean_info")
    public String info() {
        List<Node> rs = new LinkedList<>();
        String[] domains = beanServer.getDomains();
        for (String domain : domains) {
            Node item = new Node();
            item.setId(domain);
            item.setName(domain);
            rs.add(item);

            analysisChildren(beanServer, domain, domain, rs);
        }
        return JSON.toJSONString(rs);
    }

    @Path("mbean_invoke")
    public String invoke() {
        String objectName = null;
        try {
            objectName = URLDecoder.decode(URLDecoder.decode(getParam("objectName"), "UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
        }
        String name = getParam("name");
        String value = null;
        try {
            value = URLDecoder.decode(URLDecoder.decode(getParam("value"), "UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
        }
        String type = null;
        try {
            type = URLDecoder.decode(URLDecoder.decode(getParam("type"), "UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
        }
        try {
            MBeanOperationInfo[] operations = beanServer.getMBeanInfo(new ObjectName(objectName)).getOperations();
            MBeanOperationInfo operationInfo = findOperation(operations, name, value, type);
            Object[] values = analysisValue(value, operationInfo);
            return Optional.ofNullable(beanServer.invoke(new ObjectName(objectName), name, values, Arrays.stream(operationInfo.getSignature()).map(MBeanParameterInfo::getType).toArray(String[]::new))).orElse("").toString();
        } catch (Exception ignored) {
        }
        return "false";
    }

    @Path("mbean_get_property")
    public byte[] getProperty() {
        String objectName = null;
        try {
            objectName = URLDecoder.decode(URLDecoder.decode(getParam("objectName"), "UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
        }
        String name = getParam("name");
        try {
            return JSON.toJSONBytes(beanServer.getAttribute(new ObjectName(objectName), name));
        } catch (Exception e) {
            return new byte[0];
        }
    }

    @Path("mbean_set_property")
    public String setProperty() {
        String objectName = null;
        try {
            objectName = URLDecoder.decode(URLDecoder.decode(getParam("objectName"), "UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
        }
        String name = getParam("name");
        String value = null;
        try {
            value = URLDecoder.decode(URLDecoder.decode(getParam("value"), "UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
        }
        try {

            beanServer.setAttribute(new ObjectName(objectName), new Attribute(name, value));
            return "true";
        } catch (Exception e) {
            return "false";
        }
    }

    private Object[] analysisValue(String value, MBeanOperationInfo operationInfo) {
        String[] split = value.split(",");
        MBeanParameterInfo[] signature = operationInfo.getSignature();
        Object[] rs = new Object[signature.length];
        for (int i = 0, signatureLength = signature.length; i < signatureLength; i++) {
            MBeanParameterInfo info = signature[i];
            String type = info.getType();
            Class<?> aClass = null;
            try {
                aClass = Class.forName(type);
            } catch (ClassNotFoundException ignored) {
            }
            rs[i] = transfer(split[i], aClass);
        }
        return rs;
    }


    public static <T> T transfer(Object value, Class<T> type) {
        if (null == value) {
            return null;
        }

        if (type.isAssignableFrom(value.getClass())) {
            return (T) value;
        }

        if (type == int.class || type == Integer.class) {
            try {
                return (T) Integer.valueOf(value.toString());
            } catch (NumberFormatException ignored) {
            }
        }


        if (type == long.class || type == Long.class) {
            try {
                return (T) Long.valueOf(value.toString());
            } catch (NumberFormatException ignored) {
            }
        }


        if (type == short.class || type == Short.class) {
            try {
                return (T) Short.valueOf(value.toString());
            } catch (NumberFormatException ignored) {
            }
        }


        if (type == float.class || type == Float.class) {
            try {
                return (T) Float.valueOf(value.toString());
            } catch (NumberFormatException ignored) {
            }
        }


        if (type == double.class || type == Double.class) {
            try {
                return (T) Double.valueOf(value.toString());
            } catch (NumberFormatException ignored) {
            }
        }


        if (type == boolean.class || type == Boolean.class) {
            try {
                return (T) Boolean.valueOf(value.toString());
            } catch (NumberFormatException ignored) {
            }
        }

        if (type == byte.class || type == Byte.class) {
            try {
                return (T) Byte.valueOf(value.toString());
            } catch (NumberFormatException ignored) {
            }
        }

        return (T) value.toString();
    }

    /**
     * 查找
     *
     * @param operations
     * @param name
     * @param objectName
     * @param value
     * @return
     */
    private MBeanOperationInfo findOperation(MBeanOperationInfo[] operations, String name, String objectName, Object value) {
        List<MBeanOperationInfo> rs = new ArrayList<>();

        for (MBeanOperationInfo operation : operations) {
            if (!operation.getName().equals(name)) {
                continue;
            }

            rs.add(operation);
        }

        if (rs.size() == 1) {
            return rs.get(0);
        }

        MBeanOperationInfo res = null;
        String[] split = value.toString().split(",");
        for (MBeanOperationInfo operationInfo : rs) {
            MBeanParameterInfo[] signature = operationInfo.getSignature();
            loop:
            for (int i = 0; i < signature.length; i++) {
                MBeanParameterInfo info = signature[i];
                String type = info.getType();
                if (!split[i].equals(type)) {
                    break loop;
                }
            }
            res = operationInfo;
        }
        return res;
    }

    /**
     * 分析子节点
     *
     * @param beanServer bean
     * @param domain     域名
     * @param pid        pid
     * @param rs         结果
     */
    private void analysisChildren(MBeanServer beanServer, String domain, String pid, List<Node> rs) {
        Set<ObjectInstance> objectNames = null;
        try {
            objectNames = beanServer.queryMBeans(new ObjectName(domain + ":*"), null);
        } catch (MalformedObjectNameException ignored) {
            return;
        }
        for (ObjectInstance objectName : objectNames) {
            ObjectName objectName1 = objectName.getObjectName();
            String type = objectName1.getKeyProperty("name");

            Node item1 = new Node();
            item1.setId(objectName1.getCanonicalName());
            item1.setName(type);
            item1.setPid(pid);

            rs.add(item1);

            analysisProperties(beanServer, objectName.getObjectName(), item1.getId(), rs);
        }
    }

    /**
     * 分析属性
     *
     * @param beanServer bean
     * @param objectName name
     * @param pid        pid
     * @param rs         结果
     */
    private void analysisProperties(MBeanServer beanServer, ObjectName objectName, String pid, List<Node> rs) {
        MBeanInfo mBeanInfo = null;
        try {
            mBeanInfo = beanServer.getMBeanInfo(objectName);
        } catch (Exception ignored) {
        }
        analysisProperty(mBeanInfo, pid, rs);
        analysisMethod(mBeanInfo, pid, rs);
    }

    /**
     * 添加方法
     *
     * @param mBeanInfo bean info
     * @param pid       pid
     * @param rs        结果
     */
    private void analysisMethod(MBeanInfo mBeanInfo, String pid, List<Node> rs) {
        if (null == mBeanInfo) {
            return;
        }
        Node item = new Node();
        item.setId(pid + "-method");
        item.setName("操作");
        item.setPid(pid);

        rs.add(item);

        MBeanOperationInfo[] operations = mBeanInfo.getOperations();
        for (MBeanOperationInfo operation : operations) {
            JSONObject jsonObject = new JSONObject();
            Node item1 = new Node();
            item1.setId(operation.getName());
            item1.setName(operation.getName());
            item1.setPid(item.getId());
            item1.setType("method");
            item1.setObjectName(pid);
            item1.setMethodDesc(jsonObject);

            jsonObject.put("returnType", operation.getReturnType());
            MBeanParameterInfo[] signature = operation.getSignature();
            JSONArray array = new JSONArray();
            for (MBeanParameterInfo mBeanParameterInfo : signature) {
                JSONObject itemObject = new JSONObject();
                itemObject.put("name", mBeanParameterInfo.getName());
                itemObject.put("type", mBeanParameterInfo.getType());
                itemObject.put("desc", mBeanParameterInfo.getDescription());
                array.add(itemObject);
            }

            jsonObject.put("paramType", array);

            rs.add(item1);
        }
    }

    /**
     * 添加属性
     *
     * @param mBeanInfo bean info
     * @param pid       pid
     * @param rs        结果
     */
    private void analysisProperty(MBeanInfo mBeanInfo, String pid, List<Node> rs) {
        if (null == mBeanInfo) {
            return;
        }

        Node item = new Node();
        item.setId(pid + "-property");
        item.setName("属性");
        item.setPid(pid);

        rs.add(item);


        MBeanAttributeInfo[] attributes = mBeanInfo.getAttributes();
        for (MBeanAttributeInfo attribute : attributes) {
            Node item1 = new Node();
            item1.setId(attribute.getType() + "@" + attribute.getName());
            item1.setName(attribute.getName());
            item1.setPid(item.getId());
            item1.setObjectName(pid);
            item1.setType("property");

            rs.add(item1);
        }
    }

    @Path("mbean_gc")
    public String gc() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        memoryMXBean.gc();
        return "true";
    }


    /**
     * 文件大小
     *
     * @param size   大小
     * @param format 格式
     * @return 大小
     */
    public static String getNetFileSizeDescription(long size, DecimalFormat format) {
        StringBuilder bytes = new StringBuilder();
        int s1024 = 1024;
        if (size >= s1024 * s1024 * s1024) {
            double i = (size / (1024.0 * 1024.0 * 1024.0));
            bytes.append(format.format(i)).append("GB");
        } else if (size >= s1024 * s1024) {
            double i = (size / (1024.0 * 1024.0));
            bytes.append(format.format(i)).append("MB");
        } else if (size >= s1024) {
            double i = (size / (1024.0));
            bytes.append(format.format(i)).append("KB");
        } else {
            if (size <= 0) {
                bytes.append("0B");
            } else {
                bytes.append((int) size).append("B");
            }
        }
        return bytes.toString();
    }


    @Data
    public static class Node {
        private String id;
        private String pid;
        private String name;
        private String type;
        private String objectName;
        private JSONObject methodDesc;
    }

    @Override
    public String name() {
        return "mbean";
    }


    @Override
    public Class<?> pluginType() {
        return BeanAgentPlugin.class;
    }

    @Override
    public DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<?> transform(DynamicType.Builder<?> builder) {
        return null;
    }

    @Override
    public ElementMatcher<? super TypeDescription> type() {
        return null;
    }

    @Override
    public void setAddress(String address) {

    }

    @Override
    public void setParameter(JSONObject parameter) {
        this.parameter = parameter;
    }


    public String getParam(String name) {
        return Optional.ofNullable(parameter.getString(name)).orElse("");
    }
}
