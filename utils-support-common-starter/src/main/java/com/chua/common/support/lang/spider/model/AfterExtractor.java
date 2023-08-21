package com.chua.common.support.lang.spider.model;

import com.chua.common.support.lang.spider.Page;

/**
 * Interface to be implemented by page models that need to do something after fields are extracted.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
public interface AfterExtractor {

    void afterProcess(Page page);
}
