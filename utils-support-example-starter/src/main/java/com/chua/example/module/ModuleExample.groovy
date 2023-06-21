package com.chua.example.module;

import com.chua.common.support.collection.ImmutableBuilder;
import com.chua.common.support.json.JsonObject;
import com.chua.common.support.modularity.Modularity;
import com.chua.common.support.modularity.ModularityFactory;
import com.chua.common.support.modularity.ModularityResult;
import com.chua.common.support.modularity.MsgEvent;
import com.chua.common.support.utils.ThreadUtils;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;

@Slf4j
 class ModuleExample {

     static void main(String[] args) {
        ModularityFactory modularityFactory = ModularityFactory.create();
        modularityFactory.register(Modularity.builder()
                .moduleType("http")
                .moduleName("static")
                .moduleScript("GET https://ip-moe.zerodream.net/")
                .moduleResponse("""{country: "#{country}"}""")
                .build());
        ModularityResult modularityResult = modularityFactory.execute("http", "static", ImmutableBuilder.builderOfStringMap()
                .put("ip", "221.12.111.18").build());

        JsonObject jsonObject = modularityResult.getData(JsonObject.class);
        System.out.println();

    }

    private static void test() {
        Executor executor = ThreadUtils.newProcessorThreadExecutor();
        Disruptor<MsgEvent> disruptor = new Disruptor<>(MsgEvent::new, 1024, executor, ProducerType.SINGLE, new YieldingWaitStrategy());
        ExampleEventHandler handler11 = ExampleEventHandler.of("11");
        ExampleEventHandler handler12 = ExampleEventHandler.of("12");
        ExampleEventHandler handler21 = ExampleEventHandler.of("21");
        ExampleEventHandler handler31 = ExampleEventHandler.of("31");
        ExampleEventHandler handler32 = ExampleEventHandler.of("32");

        disruptor.handleEventsWith(handler11, handler12);
        disruptor.after(handler11).handleEventsWith(handler21);
        disruptor.after(handler21).handleEventsWith(handler31, handler32);
        disruptor.after(handler32).handleEventsWith(handler11);
        disruptor.start();


        RingBuffer<MsgEvent> ringBuffer = disruptor.getRingBuffer();
        ringBuffer.publish(0);
    }
}
