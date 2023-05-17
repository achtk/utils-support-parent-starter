package com.chua.common.support.view;

import com.chua.common.support.function.Launcher;
import com.chua.common.support.image.filter.ImageFilter;


/**
 * sso服务器
 *
 * @author CH
 */
public interface ViewServer extends Launcher {

    /**
     * 添加配置
     *
     * @param bucket     bucket
     * @param viewConfig 配置
     * @return this
     */
    ViewServer addContext(String bucket, ViewConfig viewConfig);

    /**
     * 添加配置
     *
     * @param name        名称
     * @param imageFilter 过滤器
     * @return this
     */
    ViewServer addPlugin(String name, ImageFilter imageFilter);
}
