package com.chua.common.support.lang.spider.model.formatter;

/**
 * @author code4crafter@gmail.com
 */
public interface ObjectFormatter<T> {

    /**
     * 格式化
     * @param raw text
     * @return 对象
     * @throws Exception ex
     */
    T format(String raw) throws Exception;

    /**
     * 类型
     * @return 类型
     */
    Class<T> clazz();

    /**
     * 初始化参数
     * @param extra 参数
     */
    void initParam(String[] extra);

}
