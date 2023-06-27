package com.chua.common.support.lang.arrange;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.function.Splitter;
import com.chua.common.support.lang.any.Any;
import com.chua.common.support.log.Log;
import com.chua.common.support.task.lmax.DisruptorEventHandler;
import com.chua.common.support.task.lmax.DisruptorEventHandlerFactory;
import com.chua.common.support.task.lmax.DisruptorFactory;
import com.chua.common.support.task.lmax.DisruptorObjectFactory;
import com.chua.common.support.unit.TimeUnit;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.StringUtils;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

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
        List<String> isReady = new LinkedList<>();
        for (Arrange arrange : arranges) {
            List<String> strings = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(arrange.getArrangeDepends());
            if (CollectionUtils.isEmpty(strings)) {
                if(isReady.contains(arrange.getArrangeId())) {
                    continue;
                }
                disruptorFactory.handleEventsWith(arrange.getArrangeId());
            } else {
                for (String string : strings) {
                    if(!isReady.contains(string)) {
                        isReady.add(string);
                        disruptorFactory.handleEventsWith(string);
                        continue;
                    }
                }
                disruptorFactory.after(strings.toArray(new String[0])).handleEventsWith(arrange.getArrangeId());
            }
        }


        disruptorFactory.start();
        disruptorFactory.publish(0);
        disruptorFactory.waitFor(TimeUnit.parse("1min"), () -> arranges.size() <= result.getParam().size() || !result.isRunning(), it -> {
            result.setRunning(false);
        });
        return result;
    }
}
