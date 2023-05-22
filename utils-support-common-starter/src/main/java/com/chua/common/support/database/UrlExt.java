package com.chua.common.support.database;

import lombok.Builder;
import lombok.Data;

/**
 * url扩展信息
 *
 * @author CH
 */
@Data
@Builder
public class UrlExt {


    @Builder.Default
    private boolean allowMultiQueries = true;

    @Builder.Default
    private boolean useUnicode = true;

    @Builder.Default
    private boolean useSSL = false;
    @Builder.Default
    private String characterEncoding = "UTF-8";
}
