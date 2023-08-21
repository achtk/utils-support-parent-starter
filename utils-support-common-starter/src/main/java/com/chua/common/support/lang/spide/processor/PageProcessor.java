package com.chua.common.support.lang.spide.processor;

import com.chua.common.support.lang.spide.page.Page;
import com.chua.common.support.lang.spide.setting.Setting;

/**
 * 进程处理器
 *
 * @author CH
 */
public interface PageProcessor {

    /**
     * 处理页面
     *
     * @param page page
     */
    void process(Page page);


    /**
     * setting
     *
     * @return site
     * @see Setting
     */
    default Setting getSetting() {
        return Setting.newSetting();
    }
}
