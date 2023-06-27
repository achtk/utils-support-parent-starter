package com.chua.common.support.lang.arrange;

import com.chua.common.support.function.Splitter;
import com.chua.common.support.log.Log;
import com.chua.common.support.task.lmax.DisruptorEventHandler;
import com.chua.common.support.task.lmax.DisruptorFactory;
import com.chua.common.support.unit.TimeUnit;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.chua.common.support.constant.CommonConstant.EMPTY_ARRAY;

/**
 * 查询依赖
 * @author CH
 */
public class DependsFindArrangeExecutor  implements ArrangeExecutor<ArrangeResult> {
    private ArrangeFactory arrangeFactory;
    private final Map<String, DisruptorEventHandler<ArrangeResult>> cache = new LinkedHashMap<>();
    private static final Log log = Log.getLogger(ArrangeExecutor.class);

    public DependsFindArrangeExecutor(DelegateArrangeFactory delegateArrangeFactory) {
        this.arrangeFactory = delegateArrangeFactory;
    }

    @Override
    public ArrangeResult execute(Map<String, Object> args) {
        ArrangeResult result = new ArrangeResult();
        DisruptorFactory<ArrangeResult> disruptorFactory = null;
        AtomicReference<DisruptorFactory<ArrangeResult>> temp = new AtomicReference<>();
        temp.set(disruptorFactory = DependsArrangeExecutor.newDisruptorFactory(cache, arrangeFactory, args, ()-> result, (name, event) -> {
        }));

        List<Arrange> arranges = arrangeFactory.list();
        Map<String, List<String>> depends = new LinkedHashMap<>();
        for (Arrange arrange : arranges) {
            List<String> strings = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(arrange.getArrangeDepends());
            depends.put(arrange.getArrangeId(), strings);
        }

        List<String> ids = new LinkedList<>();
        for (Map.Entry<String, List<String>> entry : depends.entrySet()) {
            if(entry.getValue().isEmpty()) {
                ids.add(entry.getKey());
                disruptorFactory.handleEventsWith(entry.getKey());
            }
        }

        doDepends(disruptorFactory, ids, arranges);

        disruptorFactory.start();
        disruptorFactory.publish(0);
        disruptorFactory.waitFor(TimeUnit.parse("1min"), () -> arranges.size() <= result.getParam().size() || !result.isRunning(), it -> {
            result.setRunning(false);
        });
        return result;
    }

    private void doDepends(DisruptorFactory<ArrangeResult> disruptorFactory, List<String> ids, List<Arrange> arranges) {
        if(arranges.isEmpty()) {
            return;
        }

        List<Arrange> collect = arranges.stream().filter(it -> {
            return !ids.contains(it.getArrangeType() + ":" + it.getArrangeName());
        }).collect(Collectors.toList());

        if(collect.isEmpty()) {
            return;
        }

        for (Arrange arrange : collect) {
            List<String> strings = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(arrange.getArrangeDepends());
            List<String> copy = new ArrayList<>(strings);
            copy.removeAll(ids);
            //需要的依赖都存在
            if(copy.isEmpty()) {
                String newId = arrange.getArrangeType() + ":" + arrange.getArrangeName();
                ids.add(newId);
                disruptorFactory.after(strings.toArray(EMPTY_ARRAY)).handleEventsWith(newId);
            }
        }

        doDepends(disruptorFactory, ids, collect);
    }
}
