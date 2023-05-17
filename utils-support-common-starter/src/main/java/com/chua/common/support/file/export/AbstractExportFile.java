package com.chua.common.support.file.export;

import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.file.export.resolver.ValueResolver;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.json.Json;
import com.chua.common.support.value.Pair;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * abstract
 *
 * @author CH
 */
public abstract class AbstractExportFile implements ExportFile, InitializingAware {

    protected final String separator;
    private final Pair[] header;
    protected String[] headers;
    protected final Pair[] pairs;
    protected ExportConfiguration configuration;

    public AbstractExportFile(ExportConfiguration configuration) {
        this.configuration = configuration;
        this.separator = configuration.separator();
        this.header = configuration.header();
        this.pairs = configuration.header();
        if(null != pairs) {
            this.headers = new String[pairs.length];
            for (int i = 0; i < pairs.length; i++) {
                Pair pair = pairs[i];
                headers[i] = pair.getLabel();
            }
        }
        afterPropertiesSet();
    }

    /**
     * 创建数据
     *
     * @param item       数据
     * @param onlyFormat 只保留格式化数据
     * @return 结果
     */
    protected Object[] createArray(Object item, boolean onlyFormat) {
        if (null == item) {
            return null;
        }
        Class<?> aClass = item.getClass();
        if (aClass.isArray()) {
            return (Object[]) item;
        }

        Object[] rs = new Object[header.length];
        BeanMap beanMap = BeanMap.create(item);
        for (int i = 0; i < header.length; i++) {
            Pair pair = header[i];
            if (onlyFormat) {
                rs[i] = converterType(pair, beanMap.get(pair.getName()));
            } else {
                rs[i] = converter(pair, converterType(pair, beanMap.get(pair.getName())));
            }
        }

        return rs;
    }

    protected static Object converterType(Pair pair, Object o) {
        ValueResolver valueResolver = pair.getValueResolver();
        if (valueResolver == null) {
            if (o instanceof Collection || o instanceof Map) {
                return Json.toJson(o);
            }

            return o;
        }

        return valueResolver.resolve(o);
    }

    protected Object converter(Pair pair, Object o) {
        return Converter.convertIfNecessary(o, pair.getJavaType());
    }


    @Override
    public <T> void export(OutputStream outputStream, T data) {
        export(outputStream, Collections.singletonList(data));
    }

}
