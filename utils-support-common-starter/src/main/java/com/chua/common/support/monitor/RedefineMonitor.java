package com.chua.common.support.monitor;

import java.lang.reflect.Method;

/**
 * 定义处理器
 *
 * @author CH
 */
public class RedefineMonitor {

    private Method method;

    public RedefineMonitor() {
        try {
            this.method = Class.forName("com.chua.plugin.support.content.PrePluginDefineTransformer").getDeclaredMethod("redefine", Class.class, byte[].class);
            method.setAccessible(true);
        } catch (Exception ignored) {
        }
    }

    /**
     * 重新加载
     *
     * @param loadedType 已被加载过的类
     * @param bytes      新的类的字节码
     */
    public void redefine(Class<?> loadedType, byte[] bytes) {
        if (null == method) {
            return;
        }

        try {
            method.invoke(null, loadedType, bytes);
        } catch (Exception ignored) {
        }
    }
}
