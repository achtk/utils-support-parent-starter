package com.chua.common.support.lang.spider.model;

import com.chua.common.support.lang.spider.utils.Experimental;

/**
 * Interface to be implemented by page mode.<br>
 * Can be used to identify a page model, or be used as name of file storing the object.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
@Experimental
public interface HasKey {

    /**
     * key
     * @return key key
     */
    String key();
}
