package com.chua.common.support.file.export.resolver;

import com.chua.common.support.file.export.ExportConverter;
import com.chua.common.support.file.export.ExportIgnore;
import com.chua.common.support.file.export.ExportProperty;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.value.Pair;

import java.lang.reflect.Field;

/**
 * 名称解析
 *
 * @author CH
 */
public class SimpleNamedResolver implements NamedResolver {

    protected final String EXCEL_IGNORE = "com.alibaba.excel.annotation.ExcelIgnore";
    protected final String EXCEL_PROPERTY = "com.alibaba.excel.annotation.ExcelProperty";

    @Override
    public Pair name(Field field) {
        ExportIgnore exportIgnore = field.getDeclaredAnnotation(ExportIgnore.class);
        if (null != exportIgnore) {
            return null;
        }

        ExportProperty exportProperty = field.getDeclaredAnnotation(ExportProperty.class);
        ValueResolver valueResolver = null;
        if (null != exportProperty && !StringUtils.isNullOrEmpty(exportProperty.format())) {
            valueResolver = new DateValueResolver(exportProperty.format());
        }

        ExportConverter exportConverter = field.getDeclaredAnnotation(ExportConverter.class);
        if (null != exportConverter) {
            valueResolver = (ValueResolver) ClassUtils.forObject(exportConverter.value());
        }

        return new Pair(field.getName(),
                null == exportProperty ? field.getName() : exportProperty.value(),
                field.getType(),
                valueResolver);
    }
}
