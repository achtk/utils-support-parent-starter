package com.chua.common.support.http;

import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.json.Json;
import com.chua.common.support.utils.IoUtils;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 请求
 *
 * @author CH
 */
@Data
@Accessors(fluent = true)
@Builder
public class HttpResponse {
    /**
     * 消息头
     */
    private HttpHeader httpHeader;
    /**
     * 错误码
     */
    private int code;
    /**
     * 数据
     */
    private Object content;
    /**
     * 错误消息
     */
    private String message;

    /**
     * 获取结果
     *
     * @param target 转化类型
     * @param <T>    类型
     * @return 结果
     */
    public <T> T content(Class<T> target) {
        try {
            return Converter.convertIfNecessary(content, target);
        } catch (Exception ignored) {
        }

        if (content instanceof byte[]) {
            return Json.fromJson((byte[]) content, target);
        }

        if (content instanceof String) {
            return Json.fromJson((String) content, target);
        }

        return BeanUtils.copyProperties(content, target);
    }

    /**
     * 流写入
     *
     * @param file 文件
     */
    public void receive(File file) {
        if (content instanceof byte[]) {
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                IoUtils.write((byte[]) content, outputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
