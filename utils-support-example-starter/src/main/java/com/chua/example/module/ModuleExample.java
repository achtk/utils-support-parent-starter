package com.chua.example.module;

import com.chua.common.support.modularity.MsgEvent;
import com.chua.common.support.utils.ThreadUtils;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;

@Slf4j
public class ModuleExample {

    public static void main(String[] args) {
        Executor executor = ThreadUtils.newProcessorThreadExecutor();
        Disruptor<MsgEvent> disruptor = new Disruptor<MsgEvent>(new EventFactory<MsgEvent>() {
            @Override
            public MsgEvent newInstance() {
                return new MsgEvent();
            }
        }, 1024, executor, ProducerType.SINGLE, new YieldingWaitStrategy());
        disruptor.start();

        disruptor.handleEventsWith(new EventHandler<MsgEvent>() {
            @Override
            public void onEvent(MsgEvent event, long sequence, boolean endOfBatch) throws Exception {
                log.info("11");
            }
        }, new EventHandler<MsgEvent>() {
            @Override
            public void onEvent(MsgEvent event, long sequence, boolean endOfBatch) throws Exception {
                log.info("12");
            }
        }).handleEventsWith(new EventHandler<MsgEvent>() {
            @Override
            public void onEvent(MsgEvent event, long sequence, boolean endOfBatch) throws Exception {
                log.info("21");
            }
        }).handleEventsWith(new EventHandler<MsgEvent>() {
            @Override
            public void onEvent(MsgEvent event, long sequence, boolean endOfBatch) throws Exception {
                log.info("31");
            }
        }).handleEventsWith(new EventHandler<MsgEvent>() {
            @Override
            public void onEvent(MsgEvent event, long sequence, boolean endOfBatch) throws Exception {
                log.info("41");
            }
        }, new EventHandler<MsgEvent>() {
            @Override
            public void onEvent(MsgEvent event, long sequence, boolean endOfBatch) throws Exception {
                log.info("42");
            }
        });

        RingBuffer<MsgEvent> ringBuffer = disruptor.getRingBuffer();
        ringBuffer.publish(0);
        ringBuffer.publish(1);
    }
}
