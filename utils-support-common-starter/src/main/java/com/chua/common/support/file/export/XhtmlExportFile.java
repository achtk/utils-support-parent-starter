package com.chua.common.support.file.export;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.file.export.resolver.DateValueResolver;
import com.chua.common.support.file.export.resolver.ValueResolver;
import com.chua.common.support.objects.definition.element.FieldDescribe;
import com.chua.common.support.utils.ClassUtils;
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

import static com.chua.common.support.constant.CommonConstant.HTTP;

/**
 * xml
 *
 * @author CH
 */
@Spi("html")
public class XhtmlExportFile extends AbstractExportFile {

    private OutputStreamWriter writer;

    public XhtmlExportFile(ExportConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public <T> void export(OutputStream outputStream, List<T> data) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<html><head><style>")
                .append("p { max-width: 165px;\n" +
                        "    overflow: hidden;\n" +
                        "    white-space: nowrap;\n" +
                        "    text-overflow: ellipsis;" +
                        "}\n" +
                        "table{\n" +
                        "border-collapse: collapse;\n" +
                        "width: 100%;\n" +
                        "}\n" +
                        "th, td{\n" +
                        "text-align: left;\n" +
                        "padding: 8px;\n" +
                        "}\n" +
                        "tr:nth-child(even){\n" +
                        "background-color: #fafafa;\n" +
                        "}\n" +
                        "th{\n" +
                        "background-color: #7799AA;\n" +
                        "color: white;\n" +
                        "}.table {\n" +
                        "    --bs-table-bg: transparent;\n" +
                        "    --bs-table-accent-bg: transparent;\n" +
                        "    --bs-table-striped-color: #212529;\n" +
                        "    --bs-table-striped-bg: rgba(0, 0, 0, 0.05);\n" +
                        "    --bs-table-active-color: #212529;\n" +
                        "    --bs-table-active-bg: rgba(0, 0, 0, 0.1);\n" +
                        "    --bs-table-hover-color: #212529;\n" +
                        "    --bs-table-hover-bg: rgba(0, 0, 0, 0.075);\n" +
                        "    width: 100%;\n" +
                        "    margin-bottom: 1rem;\n" +
                        "    color: #212529;\n" +
                        "    vertical-align: top;\n" +
                        "    border-color: #dee2e6;\n" +
                        "}table {\n" +
                        "    caption-side: bottom;\n" +
                        "    border-collapse: collapse;\n" +
                        "}.table-bordered>:not(caption)>*>* {\n" +
                        "    border-width: 0 1px;\n" +
                        "}\n" +
                        "table tbody {\n" +
                        "\tdisplay: block;\n" +
                        "\theight: calc(800px - 39px);\n" +
                        "\toverflow-y: scroll;\n" +
                        "}\n" +
                        " \n" +
                        "table thead, tbody tr {\n" +
                        "\tdisplay: table;\n" +
                        "\twidth: 100%;\n" +
                        "\ttable-layout: fixed;\n" +
                        "}\n" +
                        " \n" +
                        "table thead {\n" +
                        "\twidth: calc(100% - 1em)\n" +
                        "}" +
                        ".table>:not(caption)>*>* {\n" +
                        "    padding: 0.5rem 0.5rem;\n" +
                        "    border-bottom-width: 1px;\n" +
                        "    box-shadow: inset 0 0 0 9999px var(--bs-table-accent-bg);\n" +
                        "}.table>:not(:first-child) {\n" +
                        "    border-top: 2px solid currentColor;\n" +
                        "}\n" +
                        "\n" +
                        ".table>tbody {\n" +
                        "    vertical-align: inherit;\n" +
                        "}\n" +
                        "tbody, td, tfoot, th, thead, tr {\n" +
                        "    border-color: inherit;\n" +
                        "    border-style: solid;\n" +
                        "    border-width: 0;\n" +
                        "}tbody, td, tfoot, th, thead, tr {\n" +
                        "    border-color: inherit;\n" +
                        "    border-style: solid;\n" +
                        "    border-width: 0;\n" +
                        "}")
                .append("</style></head><body><table class='table'><thead><tr>");
        for (String header : headers) {
            buffer.append("<th>").append(header).append("</th>");
        }
        buffer.append("</tr></thead><tbody>");
        for (T datum : data) {
            buffer.append("<tr>");
            doAnalysis(buffer, datum);
            buffer.append("</tr>");
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
            buffer.append("<tr>");
            doAnalysis(buffer, datum);
            buffer.append("</tr>");
        }
        try {
            writer.write(buffer.toString());
            writer.flush();
        } catch (IOException ignored) {
        }
    }

    @Override
    public void close() throws Exception {
        StringBuffer buffer = new StringBuffer();
        buffer.append("</tbody></table></body>");
        buffer.append("</html>");
        writer.write(buffer.toString());
        writer.flush();

    }

    protected void doAnalysis(StringBuffer buffer, Object datum) {
        BeanMap mapping = BeanMap.create(datum);
        ClassUtils.doWithFields(datum.getClass(), it -> {
            int modifiers = it.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) {
                return;
            }
            String name = it.getName();
            if(!isNeed(name)) {
                return ;
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
        Object converterType = converterType(pair, value);
        boolean isHttp = converterType instanceof String ? converterType.toString().contains(HTTP) : false;
        buffer.append("<td>");
        if(isHttp) {
            buffer.append("<a href='").append(converterType).append("'>");
        }
        if(isHttp) {
            buffer.append("<img style='width:21px;height:21px;float:left' src='").append(converterType).append("'/>");
        }
        buffer.append("<p title='");
        buffer.append(converterType(pair, value)).append("'>");
        buffer.append(converterType);
        buffer.append("</p>");
        if(isHttp) {
            buffer.append("</a>");
        }
        buffer.append("</td>");
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

    private Pair createPair(Field it) {
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
