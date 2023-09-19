package com.chua.example.eventbus;

import com.chua.common.support.eventbus.BinlogSubscribeEventbus;
import com.chua.common.support.eventbus.Eventbus;
import com.chua.common.support.utils.ThreadUtils;

/**
 * @author CH
 */
public class EventbusExample {

    public static void main(String[] args) throws Exception {
        Eventbus eventbus = Eventbus.newDefault();
//        eventbus.registerSubscriber("zbus" , new ZbusSubscribeEventbus());
        eventbus.registerSubscriber("binlog", new BinlogSubscribeEventbus("xxxx", "root", "root"));
//        eventbus.registerObject(new SubscribeEntity());
//        eventbus.registerObject(new SubscribeEntity1());
        eventbus.registerObject(new BinlogSubscribeEntity());

        eventbus.post("demo", 23331);
        eventbus.post("demo", "2");

        ThreadUtils.sleepSecondsQuietly(2);

//        eventbus.shutdown();
    }
}
