package com.chua.example.eventbus;

import com.chua.common.support.eventbus.Eventbus;

/**
 * @author CH
 */
public class EventbusExample {

    public static void main(String[] args) throws Exception {
        Eventbus eventbus = Eventbus.newDefault();
        eventbus.register(new SubscribeEntity());
        eventbus.register(new SubscribeEntity1());
        eventbus.register(new SubscribeEntity2());

        eventbus.post("demo", 1);
        eventbus.post("demo", "2");
    }
}
