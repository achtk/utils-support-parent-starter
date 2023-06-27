package com.chua.common.support.lang.arrange;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.function.Splitter;
import com.chua.common.support.lang.any.Any;
import com.chua.common.support.log.Log;
import com.chua.common.support.task.lmax.DisruptorEventHandler;
import com.chua.common.support.task.lmax.DisruptorFactory;
import com.chua.common.support.task.lmax.DisruptorObjectFactory;
import com.chua.common.support.unit.TimeUnit;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 无依赖执行器
 *
 * @author CH
 */
public class DependsArrangeExecutor implements ArrangeExecutor<ArrangeResult> {
    private final Arrange arrange;
    private ArrangeFactory arrangeFactory;

    private static Map<String, Class<?>> DAG = new ConcurrentHashMap<>();
    private final Map<String, DisruptorEventHandler<ArrangeResult>> cache = new LinkedHashMap<>();

    private static final Log log = Log.getLogger(ArrangeExecutor.class);

    public DependsArrangeExecutor(Arrange arrange, ArrangeFactory arrangeFactory) {
        this.arrange = arrange;
        this.arrangeFactory = arrangeFactory;
    }

    public static DisruptorFactory<ArrangeResult> newDisruptorFactory(Map<String, DisruptorEventHandler<ArrangeResult>> cache,
                                                                      ArrangeFactory arrangeFactory,
                                                                      Map<String, Object> args,
                                                                      DisruptorObjectFactory<ArrangeResult> factory,
                                                                      BiConsumer<String, ArrangeResult> consumer) {
        return new DisruptorFactory<>(name -> cache.computeIfAbsent(name, s -> (event, sequence, endOfBatch) -> {
            try {
                Arrange factoryModularity = arrangeFactory.getArrange(name);
                String moduleDepends = factoryModularity.getArrangeDepends();
                log.info("当前执行任务: {}, 上一个任务: {}, 依赖任务: {}", name, event.getName(), moduleDepends);
                ArrangeHandler arrangeHandler = factoryModularity.getHandler();
                if (null == arrangeHandler) {
                    event.add(name, ArrangeResult.INSTANCE);
                    return;
                }
                ArrangeResult result = null;
                if (StringUtils.isEmpty(moduleDepends)) {
                    result = arrangeHandler.execute(args);
                } else {
                    Map<String, Object> newArgs = new LinkedHashMap<>(args);
                    List<String> strings = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(moduleDepends);
                    for (String string : strings) {
                        ArrangeResult arrangeResult = event.get(string);
                        arrangeResult.writeTo(newArgs);
                    }
                    result = arrangeHandler.execute(newArgs);
                }
                event.add(factoryModularity.getArrangeName(), result);
            } catch (Exception e) {
                event.add(name, ArrangeResult.INSTANCE);
                log.error(e.getMessage());
            } finally {
                consumer.accept(name, event);
            }

        }), factory);
    }

    @Override
    @SuppressWarnings("ALL")
    public ArrangeResult execute(Map<String, Object> args) {
        ArrangeResult arrangeResult = new ArrangeResult();
        DisruptorFactory<ArrangeResult> disruptorFactory = null;
        AtomicReference<DisruptorFactory<ArrangeResult>> temp = new AtomicReference<>();
        temp.set(disruptorFactory = newDisruptorFactory(cache, arrangeFactory, args, () -> arrangeResult, (name, event) -> {
            if (arrange.getArrangeId().equals(name)) {
                event.setRunning(false);
            }
        }));
        String moduleDepends = arrange.getArrangeDepends();
        List<String> strings = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(moduleDepends);
        if (CollectionUtils.isEmpty(strings)) {
            disruptorFactory.handleEventsWith(arrange.getArrangeId());
        } else {
            doDepends(disruptorFactory, strings, arrangeFactory);
            disruptorFactory.after(strings.toArray(new String[0])).handleEventsWith(arrange.getArrangeId());
        }

        disruptorFactory.start();
        disruptorFactory.publish(0);
        disruptorFactory.waitFor(TimeUnit.parse(arrange.getArrangeConnectionTimeout()), new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return !arrangeResult.isRunning();
            }
        }, it -> arrangeResult.setRunning(false));
        return arrangeResult.getParam().get(arrange.getArrangeId());
    }

    protected static void doDepends(DisruptorFactory<ArrangeResult> disruptorFactory, List<String> strings, ArrangeFactory arrangeFactory) {
        for (String string : strings) {
            Arrange arrange1 = arrangeFactory.getArrange(string);
            if (null == arrange1) {
                throw new RuntimeException(string + " 任务不存在");
            }
            String moduleDepends = arrange1.getArrangeDepends();
            if (StringUtils.isNotEmpty(moduleDepends)) {
                List<String> strings1 = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(moduleDepends);
                doDepends(disruptorFactory, strings1, arrangeFactory);
                disruptorFactory.after(strings1.toArray(new String[0])).handleEventsWith(arrange1.getArrangeId());
                continue;
            }
            disruptorFactory.handleEventsWith(string);
        }
    }
}
