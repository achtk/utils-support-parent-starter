package com.chua.common.support.lang.arrange;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.function.Splitter;
import com.chua.common.support.lang.any.Any;
import com.chua.common.support.utils.StringUtils;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 编排
 *
 * @author CH
 */
@Data
public class ArrangeResult {
    public static final ArrangeResult INSTANCE = new ArrangeResult();
    /**
     * 当前处理结束任务
     */
    private String name;
    /**
     * 数据
     */
    private Object data;
    /**
     * 是否运行
     */
    private boolean isRunning = true;
    /**
     * 各个任务结果
     */
    private Map<String, ArrangeResult> param = new LinkedHashMap<>();

    public void add(String name, ArrangeResult instance) {
        setName(name);
        param.put(name, instance);
    }

    public ArrangeResult get(String string) {
        return param.getOrDefault(string, ArrangeResult.INSTANCE);
    }

    public void writeTo(Map<String, Object> newArgs) {
        if(null == data) {
            return;
        }

        if(data instanceof byte[]) {
            data = StringUtils.utf8Str(data);
        }

        if (data instanceof Map) {
            newArgs.putAll((Map<? extends String, ?>) data);
        } else if (data instanceof String) {
            Any any = Converter.convertIfNecessary(data.toString(), Any.class);
            newArgs.put(Splitter.on(":").limit(2).splitToList(data.toString()).get(1), any.getValue());
        }
    }
}
