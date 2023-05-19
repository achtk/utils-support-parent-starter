package com.chua.common.support.spi.finder;

import com.chua.common.support.spi.ServiceDefinition;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_LEFT_SLASH;

/**
 * @author CH
 */
@Slf4j
public class ScriptServiceFinder extends AbstractServiceFinder{

    private static final String PATH = "META-INF/plugins";

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

                if (log.isTraceEnabled()) {
                    log.trace("extensible  {}  Loaded ", getInterfaceName());
                }
            }
        }
        return allExtensionClass;
    }

}
