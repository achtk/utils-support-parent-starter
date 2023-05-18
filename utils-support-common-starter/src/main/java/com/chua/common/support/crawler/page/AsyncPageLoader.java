package com.chua.common.support.crawler.page;


import com.chua.common.support.crawler.request.Request;
import com.chua.common.support.jsoup.nodes.Document;

/**
 * 异步页面加载器
 *
 * @author chenhua
 */
public interface AsyncPageLoader extends PageLoader {

    /**
     * load page
     *
     * @param pageRequest 请求
     * @return Document
     */
    @Override
    Document load(Request pageRequest);

    /**
     * 获取文件
     *
     * @return 获取文件
     */
    Document getDocument();
}
