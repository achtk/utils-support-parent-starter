package com.chua.common.support.crawler.url;

/**
 * url加载器
 * @author CH
 */
public interface UrlLoader extends AutoCloseable{

    /**
     * 添加
     * @param url url
     * @return this
     */
    boolean addUrl(String url);
    /**
     * 删除
     * @param url url
     * @return this
     */
    UrlLoader removeUrl(String url);
    /**
     * 获取Url
     * @return url
     */
    String getUrl();

    /**
     * 拜访数量
     * @return 拜访数量
     */
    long visited();
    /**
     * 未拜访数量
     * @return 未拜访数量
     */
    long visit();

    /**
     * 重置
     */
    void reset();
}
