package com.chua.common.support.view;

import com.chua.common.support.function.DisposableAware;
import com.chua.common.support.image.filter.ImageFilter;
import com.chua.common.support.value.Value;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

/**
 * 视图解析器
 *
 * @author CH
 */
public interface ViewResolver extends DisposableAware {
    /**
     * 配置
     *
     * @param config 配置
     * @return this
     */
    ViewResolver setConfig(ViewConfig config);

    /**
     * 过滤器
     *
     * @param name        名称
     * @param imageFilter 图片过滤器
     * @return this
     */
    ViewResolver addPlugin(String name, ImageFilter imageFilter);

    /**
     * 过滤器
     *
     * @param imageFilter 图片过滤器
     * @return this
     */
    ViewResolver setPlugin(Map<String, ImageFilter> imageFilter);

    /**
     * 预览
     *
     * @param bucket     bucket
     * @param path       预览的文件路径
     * @param mode       模式. download/preview
     * @param os         输出
     * @param pluginList 插件列表
     * @return ViewPreview
     */
    ViewPreview preview(String bucket, String path, String mode, OutputStream os, Set<String> pluginList);

    /**
     * 保存文件
     *
     * @param is     文件
     * @param bucket bucket
     * @param name   名称
     * @return 结果
     */
    Value<String> storage(InputStream is, String bucket, String name);
}
