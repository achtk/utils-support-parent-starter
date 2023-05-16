package com.chua.common.support.context.environment.property;

import com.chua.common.support.lang.profile.Profile;

/**
 * 配置
 *
 * @author CH
 */
public class ProfilePropertySource implements PropertySource {

    private final Profile profile;

    public ProfilePropertySource(Profile profile) {
        this.profile = profile;
    }

    @Override
    public String getName() {
        return "profile$" + System.nanoTime();
    }

    @Override
    public String getProperty(String name) {
        return profile.get(name, "").toString();
    }
}
