package com.chua.common.support.lang.pool;

import java.sql.Connection;

/**
 * 连接池
 *
 * @author CH
 */
public class ConnectionPool extends BoundBlockingPool<Connection> {
    public ConnectionPool(ObjectFactory<Connection> objectFactory) {
        super(objectFactory);
    }

}
