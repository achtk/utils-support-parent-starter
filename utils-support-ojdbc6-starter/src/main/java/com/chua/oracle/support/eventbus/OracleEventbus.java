package com.chua.oracle.support.eventbus;

import com.chua.common.support.eventbus.AbstractEventbus;
import com.chua.common.support.eventbus.EventbusType;
import com.chua.common.support.eventbus.SubscribeEventbus;
import com.chua.common.support.function.InitializingAware;

/**
 * oracle jms
 * @author CH
 */
public class OracleEventbus extends AbstractEventbus implements InitializingAware {

    @Override
    public SubscribeEventbus post(String name, Object message) {
        return null;
    }

    @Override
    public EventbusType event() {
        return null;
    }


    @Override
    public void afterPropertiesSet() {
//        DataSource datasource = profile.getType("datasource", null, DataSource.class);
//        if(null != datasource) {
//            javax.jms.QueueConnectionFactory queueConnectionFactory = AQjmsFactory.getQueueConnectionFactory(datasource);
//        }
    }
}
