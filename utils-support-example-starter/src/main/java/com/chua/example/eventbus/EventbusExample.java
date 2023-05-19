package com.chua.example.eventbus;

import com.chua.common.support.eventbus.Eventbus;
import com.chua.common.support.eventbus.EventbusProvider;

/**
 * @author CH
 */
public class EventbusExample {

    public static void main(String[] args) throws Exception {
        EventbusProvider eventbusProvider = new EventbusProvider();
        eventbusProvider.register(new SubscribeEntity());
        eventbusProvider.register(new SubscribeEntity1());
        eventbusProvider.register(new SubscribeEntity2());

        eventbusProvider.post("demo", 1);
        eventbusProvider.post("demo", "2");

        eventbusProvider.close();
    }
}
