package com.chua.common.support.lang.environment;

import com.chua.common.support.context.factory.ApplicationContextBuilder;
import com.chua.common.support.context.factory.ConfigureApplicationContext;
import com.chua.common.support.eventbus.Eventbus;
import com.chua.common.support.lang.profile.Profile;

/**
 * 环境设置
 * @author CH
 */
public class EnvironmentProvider {
    private final ConfigureApplicationContext applicationContext;

    public EnvironmentProvider(Profile profile) {
        this.applicationContext = ApplicationContextBuilder.newBuilder()
                .environment(profile)
                .openScanner(false)
                .build();
    }

    /**
     * 刷新环境
     * @param value 环境
     */
    public void refresh(Eventbus value) {
        applicationContext.autowire(value);
    }
}
