package com.chua.common.support.file.export;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.file.export.resolver.DateValueResolver;
import com.chua.common.support.file.export.resolver.ValueResolver;
import com.chua.common.support.objects.definition.element.FieldDescribe;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.ObjectUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.value.Pair;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * xml
 *
 * @author CH
 */
@Spi("xml")
public class XmlExportFile extends AbstractExportFile {

    private OutputStreamWriter writer;

    public XmlExportFile(ExportConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public <T> void export(OutputStream outputStream, List<T> data) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"").append(configuration.charset()).append("\"?>");
        buffer.append("<data>");
        for (T datum : data) {
            buffer.append("<item>");
            doAnalysis(buffer, datum);
            buffer.append("</item>");
        }


        try {
            this.writer = new OutputStreamWriter(outputStream, configuration.charset());
            writer.write(buffer.toString());
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public <T> void append(List<T> records) {
        StringBuffer buffer = new StringBuffer();
        for (T datum : records) {
            buffer.append("<item>");
            doAnalysis(buffer, datum);
            buffer.append("</item>");
        }
        try {
            writer.write(buffer.toString());
            writer.flush();
        } catch (IOException ignored) {
        }
    }

    @Override
    public void close() throws Exception {
        writer.write("</data>");
        writer.flush();
    }

    public void doAnalysis(StringBuffer buffer, Object datum) {
        if (datum instanceof Map) {
            ((Map<?, ?>) datum).forEach((k, v) -> {
                doAnalysisValue(buffer, new Pair(k.toString(), v == null ? null : v.toString()), v);
            });
            return;
        }
        doAnalysisBean(buffer, datum);
    }

    private void doAnalysisBean(StringBuffer buffer, Object datum) {
        BeanMap mapping = BeanMap.create(datum);
        ClassUtils.doWithFields(datum.getClass(), it -> {
            int modifiers = it.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) {
                return;
            }
            String name = it.getName();
            if(!isNeed(name)) {
                return;
            }
            Class<?> type = it.getType();
            Pair pair = createPair(it);
            if (Collection.class.isAssignableFrom(type)) {
                doAnalysisCollection(buffer, pair, (Collection) mapping.get(name));
                return;
            }

            if (ClassUtils.isJavaType(type)) {
                doAnalysisValue(buffer, pair, mapping.get(name));
                return;
            }

            if (Map.class.isAssignableFrom(type)) {
                Map fieldValue = (Map) mapping.get(name);
                doAnalysisMap(buffer, pair, fieldValue);
            }

            doAnalysisValue(buffer, pair, mapping.get(name));
        });

    }

    /**
     * 生成xml
     *
     * @param buffer 结果
     * @param pair   详情
     * @param value  数据
     */
    public void doAnalysisValue(StringBuffer buffer, Pair pair, Object value) {
        if(!isNeed(pair.getName())) {
            return;
        }
        buffer.append("<").append(pair.getName());
        String label = pair.getLabel();
        if (!StringUtils.isNullOrEmpty(label) && !pair.getName().equals(label)) {
            buffer.append(" describe=\"").append(pair.getLabel()).append("\"");
        }
        buffer.append(">");
        buffer.append(ObjectUtils.defaultIfNull(converterType(pair, value), ""));
        buffer.append("</").append(pair.getName());
        buffer.append(">");
    }

    private <T> void doAnalysisMap(StringBuffer buffer, Pair pair, Map datum) {
        datum.forEach((k, v) -> {
            if (null == v) {
                doAnalysisValue(buffer, pair, null);
                return;
            }

            if (v instanceof Collection) {
                doAnalysisCollection(buffer, pair, (Collection) datum);
                return;
            }

            if (v instanceof Map) {
                doAnalysisMap(buffer, pair, (Map) v);
                return;
            }

            doAnalysisValue(buffer, pair, v);
        });
    }

    private <T> void doAnalysisCollection(StringBuffer buffer, Pair pair, Collection datum) {
        for (Object o : datum) {
            doAnalysis(buffer, o);
        }
    }

    private static Pair createPair(Field it) {
        Pair pair = new Pair(it.getName(), null);
        pair.setJavaType(it.getType());

        FieldDescribe fieldDescribe = new FieldDescribe(it);
        ExportProperty exportProperty = fieldDescribe.getAnnotation(ExportProperty.class);

        ValueResolver valueResolver = null;
        String value = null;
        if (null != exportProperty && !StringUtils.isNullOrEmpty(exportProperty.format())) {
            valueResolver = new DateValueResolver(exportProperty.format());
            value = exportProperty.value();
        }

        ExportConverter exportConverter = fieldDescribe.getAnnotation(ExportConverter.class);
        if (null != exportConverter) {
            valueResolver = (ValueResolver) ClassUtils.forObject(exportConverter.value());
        }

        pair.setLabel(value);
        pair.setValueResolver(valueResolver);

        return pair;
    }


}
