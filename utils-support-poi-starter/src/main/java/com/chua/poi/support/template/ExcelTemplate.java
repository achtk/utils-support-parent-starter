package com.chua.poi.support.template;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.collection.TypeHashMap;
import com.chua.common.support.lang.template.Template;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

/**
 * excel
 * @author CH
 */
@Spi({"xlsx", "xls"})
public class ExcelTemplate implements Template {
    @Override
    public void resolve(InputStream inputStream, OutputStream outputStream, Map<String, Object> templateData) {
        ExcelWriter writer = EasyExcel.write(outputStream)
                .autoCloseStream(true)
                .withTemplate(inputStream)
                .build();

        templateData.forEach((k, v) -> {
            if (v instanceof Collection) {
                writer.fill(new FillWrapper(k, (Collection) v), EasyExcel.writerSheet().build());
            } else {
                writer.fill(new TypeHashMap().addProfile(k, v), EasyExcel.writerSheet().build());
            }
        });
        writer.finish();
    }
}
