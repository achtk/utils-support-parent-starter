package com.chua.agent.support.utils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 获取资源
 *
 * @author CH
 * @since 2021-08-18
 */
public class ResourceUtils {
    /**
     * 获取资源
     *
     * @param name 文件
     * @param args 参数
     * @return 资源
     */
    public static String getResource(String name, Map<String, Object> args) {
        URL resource = getResourceUrl(name);

        if (null == resource) {
            return null;
        }

        try (InputStreamReader isr = new InputStreamReader(resource.openStream(), UTF_8)) {
            char[] chars = new char[2048];
            int line = 0;
            StringWriter sw = new StringWriter();
            while ((line = isr.read(chars)) > -1) {
                sw.write(chars, 0, line);
            }
            sw.close();
            String string = sw.toString();
            for (Map.Entry<String, Object> entry : args.entrySet()) {
                try {
                    string = string.replaceAll("\\{\\{" + entry.getKey() + "\\}\\}", entry.getValue() + "");
                } catch (Exception e) {
                    string = string.replace("{{" + entry.getKey() + "}}", entry.getValue() + "");
                }
            }
            return string;
        } catch (IOException ignored) {
        }
        return "";
    }

    public static URL getResourceUrl(String name) {
        String test = "./utils-agentv-support/src/main/resources";
        if (new File(test, name).exists()) {
            try {
                return new File(test, name).toURI().toURL();
            } catch (MalformedURLException ignored) {
            }
        }
        ClassLoader classLoader = ResourceUtils.class.getClassLoader();
        Enumeration<URL> resources = null;
        try {
            resources = classLoader.getResources(name);
        } catch (IOException ignored) {
        }
        List<URL> url = new ArrayList<>();
        while (null != resources && resources.hasMoreElements()) {
            URL element = resources.nextElement();
            if (element.toExternalForm().contains("agentv2")) {
                url.add(element);
            }
        }


        addUrl(new File(".", name), url);

        String path = ResourceUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (path.contains("target")) {
            //idea
            List<URL> result1 = new ArrayList<>();
            path = path.substring(0, path.indexOf("target"));
            addUrl(new File(path, name), result1);
            if (!result1.isEmpty()) {
                return result1.get(0);
            }

            addUrl(new File(path + "/src/main/resources", name), result1);
            if (!result1.isEmpty()) {
                return result1.get(0);
            }
        }

        if (url.isEmpty()) {
            return classLoader.getResource(name);
        }
        for (URL url1 : url) {
            if ("file".equals(url1.getProtocol())) {
                return url1;
            }
        }
        return url.get(0);
    }

    /**
     * 添加url
     *
     * @param file 文件
     * @param url  结果
     */
    private static void addUrl(File file, List<URL> url) {
        if (file.exists()) {
            try {
                url.add(file.toURI().toURL());
            } catch (MalformedURLException ignored) {
            }
        }
    }


    /**
     * 获取文件
     *
     * @param s 文件名
     * @return 文件
     */
    public static byte[] getUrl(String s) {
        if (s.contains("?")) {
            s = s.substring(0, s.indexOf("?"));
        }

        if (s.startsWith("/")) {
            s = s.substring(1);
        }
        URL url = ResourceUtils.getResourceUrl(s);
        if (null == url) {
            return new byte[0];
        }
        int n;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        try (InputStream inputStream = url.openStream();) {
            while (-1 != (n = inputStream.read(buffer))) {
                output.write(buffer, 0, n);
            }
        } catch (IOException ignored) {
            return new byte[0];
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return output.toByteArray();
    }

    /**
     * 图片
     *
     * @param s 图片
     * @return 图片
     */
    public static byte[] getImage(String s) {
        if (s.contains("?")) {
            s = s.substring(0, s.indexOf("?"));
        }

        if (s.startsWith("/")) {
            s = s.substring(1);
        }
        URL url = null;
        try {
            url = ResourceUtils.class.getClassLoader().getResource(s);
        } catch (Exception ignored) {
        }
        if (null == url) {
            return new byte[0];
        }
        int n;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        try (InputStream inputStream = url.openStream()) {
            while (-1 != (n = inputStream.read(buffer))) {
                output.write(buffer, 0, n);
            }
        } catch (IOException ignored) {
            return new byte[0];
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return output.toByteArray();
    }
}
