package com.chua.common.support.lang.environment;

import com.chua.common.support.eventbus.EventbusHandler;
import com.chua.common.support.lang.profile.Profile;

/**
 * 环境设置
 * @author CH
 */
public class EnvironmentProvider {
    private Profile profile;

    public EnvironmentProvider(Profile profile) {
        this.profile = profile;
    }

    /**
     * 刷新环境
     * @param value 环境
     */
    public void refresh(EventbusHandler value) {

    }
}
