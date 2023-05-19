package com.chua.example.eventbus;

import com.chua.common.support.eventbus.EventbusType;
import com.chua.common.support.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;

/**
 * @author CH
 * @version 1.0.0
 * @since 2021/5/25
 */
@Slf4j
public class SubscribeEntity {

    private int index = 0;

    @Subscribe(value = "demo", type = EventbusType.GUAVA)
    public void guava(String holiday) {
        log.info("{} -> guava: {}", ++index, holiday);
    }
}
