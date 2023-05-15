package com.chua.common.support.converter.definition;

import com.chua.common.support.utils.ClassUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;

import static com.chua.common.support.constant.CommonConstant.FILE;
import static com.chua.common.support.constant.CommonConstant.HTTP;

/**
 * 文件
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/5/24
 */
public class FileTypeConverter implements TypeConverter<File> {

    private static final String[] TEMP = new String[]{"Documents", "Downloads", "Desktop"};
    private static final String CHANNEL = "java.nio.channels.Channels$1";
    private static final String CHANNEL_STREAM = "sun.nio.ch.ChannelInputStream";

    @Override
    public File convert(Object value) {
        if (null == value) {
            return null;
        }

        if (value instanceof File) {
            return (File) value;
        }

        if (value instanceof Path) {
            return ((Path) value).toFile();
        }

        if (value instanceof String) {
            return stringToFile(value.toString());
        }

        if (value instanceof URL) {
            return new File(((URL) value).getFile());
        }

        if (value instanceof URI) {
            try {
                return new File(((URI) value).toURL().getFile());
            } catch (MalformedURLException ignored) {
            }
        }


        return null;
    }

    /**
     * str -> file
     *
     * @param str str
     * @return file
     */
    private File stringToFile(String str) {
        if (str.startsWith(HTTP)) {
            try {
                return new File(new URL(str).getFile());
            } catch (Exception ignored) {
            }
        }

        File temp = new File(str);
        if (temp.exists()) {
            return temp;
        }

        temp = new File("src/main/resources", str);
        if (temp.exists()) {
            return temp;
        }

        URL resource = ClassLoader.getSystemClassLoader().getResource(str);
        if (null != resource && FILE.equals(resource.getProtocol())) {
            return new File(resource.getFile());
        }

        String userHome = System.getProperty("user_home");
        temp = new File(userHome, str);
        if (temp.exists()) {
            return temp;
        }

        for (String s : TEMP) {
            temp = new File(userHome + "/" + s, str);
            if (temp.exists()) {
                return temp;
            }
        }

        try {
            File file = new File(str);
            if(file.exists()) {
                return file;
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    @Override
    public Class<File> getType() {
        return File.class;
    }
}
