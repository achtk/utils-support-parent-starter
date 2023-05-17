package com.chua.common.support.file.export;

import com.chua.common.support.file.export.resolver.NamedResolver;
import com.chua.common.support.file.export.resolver.SimpleNamedResolver;
import com.chua.common.support.value.Pair;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 导出配置
 *
 * @author CH
 */
@Data
@Accessors(fluent = true)
public class ExportConfiguration {
    /**
     * 表头
     */
    private Pair[] header;
    /**
     * 数据分隔符
     */
    private String separator;
    /**
     * 行分隔符
     */
    private String lineSeparator = "\r\n";
    /**
     * 空值默认值
     */
    private String emptyValue;
    /**
     * 跳过空行
     */
    private boolean skipEmptyLines = true;
    /**
     * 编码
     */
    private String charset = "UTF-8";
    /**
     * 导入跳过的行数
     */
    private int skip = 1;
    /**
     * rdf
     */
    private String rdfUri;
    /**
     * excel
     */
    private Integer sheetNo = 0;
    /**
     * 名称解析器
     */
    private NamedResolver namedResolver = new SimpleNamedResolver();


}
