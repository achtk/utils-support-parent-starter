package com.chua.common.support.modularity;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.function.Splitter;
import com.chua.common.support.json.JsonArray;
import com.chua.common.support.lang.any.Any;
import com.chua.common.support.lang.treenode.ArrangeTreeNode;
import com.chua.common.support.lang.treenode.TreeNode;
import com.chua.common.support.log.Log;
import com.chua.common.support.modularity.resolver.ModularityTypeResolver;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.task.lmax.DisruptorEventHandler;
import com.chua.common.support.task.lmax.DisruptorEventHandlerFactory;
import com.chua.common.support.task.lmax.DisruptorFactory;
import com.chua.common.support.task.lmax.DisruptorObjectFactory;
import com.chua.common.support.unit.TimeUnit;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * 无依赖执行器
 *
 * @author CH
 */
public class DependsModularityExecutor<T> implements ModularityExecutor<T> {
    private final Modularity modularity;
    private ModularityFactory modularityFactory;

    private static Map<String, Class<?>> DAG = new ConcurrentHashMap<>();
    private final Map<String, DisruptorEventHandler<MsgEvent>> cache = new LinkedHashMap<>();

    private static final Log log = Log.getLogger(ModularityExecutor.class);

    public DependsModularityExecutor(Modularity modularity, ModularityFactory modularityFactory) {
        this.modularity = modularity;
        this.modularityFactory = modularityFactory;
    }

    @Override
    @SuppressWarnings("ALL")
    public T execute(Map<String, Object> args) {
        MsgEvent msgEvent = new MsgEvent();
        DisruptorFactory<MsgEvent> disruptorFactory = null;
        AtomicReference<DisruptorFactory<MsgEvent>> temp = new AtomicReference<>();
        temp.set(disruptorFactory = new DisruptorFactory<>(new DisruptorEventHandlerFactory<MsgEvent>() {
            @Override
            public DisruptorEventHandler<MsgEvent> getEventHandler(String name) {
                return cache.computeIfAbsent(name, s -> (event, sequence, endOfBatch) -> {
                    Map<String, ModularityResult> param = event.getParam();
                    try {
                        Modularity factoryModularity = modularityFactory.getModularity(name);
                        String moduleDepends = factoryModularity.getModuleDepends();
                        log.info("当前执行任务: {}, 上一个任务: {}, 依赖任务: {}", name, event.getName(), moduleDepends);
                        String moduleType = factoryModularity.getModuleType();
                        ModularityTypeResolver modularityTypeResolver = ClassUtils.forObjectWithType(factoryModularity.getModuleResolver(), ModularityTypeResolver.class);
                        if (null == modularityTypeResolver) {
                            modularityTypeResolver = ServiceProvider.of(ModularityTypeResolver.class).getNewExtension(moduleType);
                        }

                        if (null == modularityTypeResolver) {
                            event.setName(name);
                            param.put(name, ModularityResult.INSTANCE);
                            return;
                        }

                        ModularityResult modularityResult = null;
                        if (StringUtils.isEmpty(moduleDepends)) {
                            modularityResult = modularityTypeResolver.execute(modularityFactory, factoryModularity, args);
                        } else {
                            Map<String, Object> newArgs = new LinkedHashMap<>(args);
                            List<String> strings = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(moduleDepends);
                            for (String string : strings) {
                                ModularityResult modularityResult1 = event.getParam().get(string);
                                Object data = modularityResult1.getData();
                                if(data instanceof byte[]) {
                                    data = StringUtils.utf8Str(data);
                                }

                                if (data instanceof Map) {
                                    newArgs.putAll((Map<? extends String, ?>) data);
                                } else if (data instanceof String) {
                                    Any any = Converter.convertIfNecessary(data.toString(), Any.class);
                                    newArgs.put(Splitter.on(":").limit(2).splitToList(string).get(1), any.getValue());
                                }
                            }
                            modularityResult = modularityTypeResolver.execute(modularityFactory, factoryModularity, newArgs);
                        }
                        param.put(name, modularityResult);
                        event.setName(factoryModularity.getModuleName());
                    } catch (Exception e) {
                        param.put(name, ModularityResult.INSTANCE);
                        log.error(e.getMessage());
                    }
                });
            }
        }, new DisruptorObjectFactory<MsgEvent>() {
            @Override
            public MsgEvent newInstance() {
                return msgEvent;
            }
        }));
        String moduleDepends = modularity.getModuleDepends();
        List<String> strings = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(moduleDepends);
        if (CollectionUtils.isEmpty(strings)) {
            disruptorFactory.handleEventsWith(modularity.getModuleId());
        } else {
            doDepends(disruptorFactory, strings);
            disruptorFactory.after(strings.toArray(new String[0])).handleEventsWith(modularity.getModuleId());
        }

        disruptorFactory.start();
        disruptorFactory.publish(0);
        disruptorFactory.waitFor(TimeUnit.parse(modularity.getModuleConnectionTimeout()), new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return null != msgEvent.getParam().get(modularity.getModuleId()) || !msgEvent.isRunning();
            }
        }, it -> msgEvent.setRunning(false));
        return (T) msgEvent.getParam().get(modularity.getModuleId());
    }

    private void doDepends(DisruptorFactory<MsgEvent> disruptorFactory, List<String> strings) {
        for (String string : strings) {
            Modularity modularity1 = modularityFactory.getModularity(string);
            if (null == modularity1) {
                throw new RuntimeException(string + " 任务不存在");
            }
            String moduleDepends = modularity1.getModuleDepends();
            if (StringUtils.isNotEmpty(moduleDepends)) {
                List<String> strings1 = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(moduleDepends);
                doDepends(disruptorFactory, strings1);
                disruptorFactory.after(strings1.toArray(new String[0])).handleEventsWith(modularity1.getModuleId());
                continue;
            }
            disruptorFactory.handleEventsWith(string);
        }
    }
}
