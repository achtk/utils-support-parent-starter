package com.chua.email.support.store;

import javax.mail.Store;

/**
 * mail listener
 *
 * @author CH
 */
@FunctionalInterface
public interface StoreListener {

    /**
     * 监听
     *
     * @param store store
     */
    void listen(Store store);
}
