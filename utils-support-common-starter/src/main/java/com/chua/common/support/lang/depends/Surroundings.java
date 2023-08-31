package com.chua.common.support.lang.depends;

/**
 * 环境
 * @author CH
 */
public interface Surroundings{

    /**
     * 类加载器
     * @return 类加载器
     */
    ClassLoader getClassLoader();

    /**
     * 记载
     *
     * @param name 名称
     * @return 类
     * @throws ClassNotFoundException cnf
     */
    Class<?> loadClass(String name) throws ClassNotFoundException;

}
