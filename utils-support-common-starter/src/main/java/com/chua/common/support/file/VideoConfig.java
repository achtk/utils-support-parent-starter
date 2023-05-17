package com.chua.common.support.file;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 视频配置
 *
 * @author CH
 */
@Data
@Accessors(fluent = true)
public class VideoConfig {
    /**
     * 宽度
     */
    private int width;
    /**
     * 高度
     */
    private int height;
    /**
     * 速率
     */
    private float frameRate;
    /**
     * 视频比特率
     */
    private int videoBitrate;
    /**
     * 像素格式
     */
    private int pixelFormat;
    /**
     * 视频编解码器
     */
    private int videoCodec;
    /**
     * 音频编解码器
     */
    private int audioCodec;
    /**
     * 选项
     */
    private Map<String, String> option;
}
