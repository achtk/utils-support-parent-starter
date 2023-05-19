package com.chua.common.support.spi.finder;

import com.chua.common.support.spi.ServiceDefinition;
import com.chua.common.support.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_LEFT_SLASH;


/**
 * 自定义spi查找器
 *
 * @author CH
 */
@Slf4j
public class CustomServiceFinder extends AbstractServiceFinder {

    private static final String PATH = "META-INF/extensions";


    @Override
    protected List<ServiceDefinition> find() {
        return loadFromFile(PATH);
    }

    /**
     * 加载文件配置
     *
     * @param path path必须以/结尾
     * @return List<ExtensionClass < T>>
     */
    protected synchronized List<ServiceDefinition> loadFromFile(String path) {
        if (log.isDebugEnabled()) {
            log.debug("Loading extension of extensible {} from path: {}", getInterfaceName(), path);
        }
        if (!path.endsWith(SYMBOL_LEFT_SLASH)) {
            path += SYMBOL_LEFT_SLASH;
        }
        // 默认如果不指定文件名字，就是接口名
        String fullFileName = path + getInterfaceName();
        try {
            return loadFromClassLoader(fullFileName);
        } catch (Throwable t) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to load extension of extensible {} from path: {}", getInterfaceName(), fullFileName, t);
            }
        }
        return null;
    }

    /**
     * 从类加载器中加载文件
     *
     * @param fullFileName 文件全称
     * @return List<ExtensionClass < T>
     * @throws Throwable Throwable
     */
    private List<ServiceDefinition> loadFromClassLoader(final String fullFileName) throws Throwable {
        Enumeration<URL> urls = getClassLoader().getResources(fullFileName);
        List<ServiceDefinition> allExtensionClass = new ArrayList<>();
        // 可能存在多个文件
        if (urls != null) {
            while (urls.hasMoreElements()) {
                // 读取一个文件
                URL url = urls.nextElement();
                if (log.isDebugEnabled()) {
                    log.debug("Loading extension of extensible {} from classloader: {} and file: {}", getInterfaceName(), getClassLoader(), url);
                }

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
                    String line;

                    while ((line = reader.readLine()) != null) {
                        if (log.isTraceEnabled()) {
                            log.trace("Loading extension of extensible {} line {}", getInterfaceName(), line);
                        }
                        allExtensionClass.addAll(readLine(line, url));
                    }
                } catch (Throwable t) {
                    if (log.isDebugEnabled()) {
                        log.debug("Failed to load extension of extensible {} from classloader: {} and file:", getInterfaceName(), getClassLoader());
                    }
                }
                if (log.isTraceEnabled()) {
                    log.trace("extensible  {}  Loaded ", getInterfaceName());
                }
            }
        }
        return allExtensionClass;
    }

    /**
     * 读取文件
     *
     * @param line 一行数据
     * @param url  链接
     * @return List<ExtensionClass < T>>
     */
    protected List<ServiceDefinition> readLine(final String line, URL url) {
        String[] aliasAndClassName = parseSpiNameAndClassName(line);
        int size = 2;
        if (aliasAndClassName == null || aliasAndClassName.length != size) {
            return null;
        }
        //Spi别名
        String alias = aliasAndClassName[0];
        //类名
        String className = aliasAndClassName[1];
        // 读取配置的实现类
        Class<?> tmp;
        try {
            tmp = Class.forName(className, false, getClassLoader());
        } catch (Throwable e) {
            if (log.isWarnEnabled()) {
                log.warn("Extension {} of extensible {} is disabled, cause by: {}", className, getInterfaceName(), e.getMessage());
            }
            if (log.isDebugEnabled()) {
                log.debug("Extension " + className + " of extensible " + getInterfaceName() + " is disabled.");
            }
            return null;
        }
        return buildDefinition(null, tmp, StringUtils.defaultString(alias, className), url);
    }

    /**
     * 解析名字以及类名
     *
     * @param line 一行数据
     * @return String[]{名称, 类, 优先级}
     */
    protected String[] parseSpiNameAndClassName(String line) {
        if (null == line || "".equals(line)) {
            return null;
        }

        line = line.trim();
        String name = "";
        String className = line;
        int i = line.indexOf('=');
        if (i > 0) {
            //Spi预处理名称
            name = line.substring(0, i).trim();
            //类名
            className = line.substring(i + 1).trim();
        }
        //类名无效
        if (className.length() == 0) {
            return null;
        }

        return new String[]{name, className};
    }

}
