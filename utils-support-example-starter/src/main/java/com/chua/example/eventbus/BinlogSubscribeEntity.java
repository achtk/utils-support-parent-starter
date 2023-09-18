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
public class BinlogSubscribeEntity {

    private int index = 0;

    @Subscribe(value = "*", type = EventbusType.BINLOG)
    public void local(Object holiday) {
        log.info("{} -> binlog: {}", ++index, holiday);
    }
}
