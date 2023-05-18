package com.chua.common.support.crawler.process;

import com.chua.common.support.crawler.node.Parser;
import com.chua.common.support.crawler.url.UrlLoader;

/**
 * 解析器进程
 *
 * @author CH
 */
public interface ParserProcessor {
    /**
     * 是否允许解析
     *
     * @param parser 解析器
     * @return 是否允许解析
     */
    boolean matcher(Parser parser);

    /**
     * 解析
     *
     * @param parser    解析器
     * @param urlLoader
     * @return 解析
     */
    boolean processor(Parser parser, UrlLoader urlLoader);
}
