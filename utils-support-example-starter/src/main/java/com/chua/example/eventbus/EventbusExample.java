package com.chua.example.eventbus;

import com.chua.common.support.eventbus.Eventbus;
import com.chua.common.support.utils.ThreadUtils;
import com.chua.zbus.support.subscribe.ZbusSubscribeEventbus;

/**
 * @author CH
 */
public class EventbusExample {

    public static void main(String[] args) throws Exception {
        Eventbus eventbus = Eventbus.newDefault();
        eventbus.registerSubscriber("zbus" , new ZbusSubscribeEventbus());
//        eventbus.registerObject(new SubscribeEntity());
//        eventbus.registerObject(new SubscribeEntity1());
        eventbus.registerObject(new SubscribeEntity2());

        eventbus.post("demo", 23331);
        eventbus.post("demo", "2");

        ThreadUtils.sleepSecondsQuietly(2);

        eventbus.shutdown();
    }
}
