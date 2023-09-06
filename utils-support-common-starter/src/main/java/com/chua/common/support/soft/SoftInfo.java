package com.chua.common.support.soft;

import lombok.Builder;
import lombok.Data;

/**
 * 软件信息
 *
 * @author CH
 */
@Data
@Builder
public class SoftInfo {

    /**
     * 名称
     */
    private String name;
    /**
     * 版本
     */
    public String version;
}
