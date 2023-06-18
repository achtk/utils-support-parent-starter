package com.chua.common.support.oss.preview;

import com.chua.common.support.media.MediaType;
import com.chua.common.support.pojo.Mode;
import com.chua.common.support.pojo.OssSystem;
import com.chua.common.support.range.Range;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 预览
 *
 * @author CH
 */
@Builder
@Data
@Accessors(chain = true)
public class Preview {
    /**
     * contentType
     */
    private MediaType contentType;
    /**
     * 原始文件路径
     */
    private String path;
    /**
     * 下载区间
     */
    private Range<Long> range;
    private Mode mode;

    private OssSystem ossSystem;

    private byte[] bytes;
}
