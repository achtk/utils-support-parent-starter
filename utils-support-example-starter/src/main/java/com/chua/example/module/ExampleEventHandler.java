package com.chua.example.module;

import com.chua.common.support.modularity.MsgEvent;
import com.lmax.disruptor.EventHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author CH
 */
@Slf4j
@AllArgsConstructor(staticName = "of")
public class ExampleEventHandler implements EventHandler<MsgEvent> {

    private String name;

    @Override
    public void onEvent(MsgEvent msgEvent, long l, boolean b) throws Exception {
        log.info(name);
    }
}
