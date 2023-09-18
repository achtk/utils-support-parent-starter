package com.chua.example.eventbus;

import com.chua.common.support.eventbus.Eventbus;
import com.chua.common.support.utils.ThreadUtils;
import com.chua.redis.support.eventbus.RedisionEventbus;

/**
 * @author CH
 */
public class EventbusExample {

    public static void main(String[] args) throws Exception {
        Eventbus eventbus = Eventbus.newDefault();
        eventbus.registerSubscriber("redis" , new RedisionEventbus());
        eventbus.registerObject(new SubscribeEntity());
        eventbus.registerObject(new SubscribeEntity1());
        eventbus.registerObject(new SubscribeEntity2());

        eventbus.post("demo", 1);
        eventbus.post("demo", "2");

        ThreadUtils.sleepSecondsQuietly(2);

        eventbus.shutdown();

    }
}
