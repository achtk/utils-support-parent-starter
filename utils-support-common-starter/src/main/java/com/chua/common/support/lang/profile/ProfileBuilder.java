package com.chua.common.support.lang.profile;

import lombok.NoArgsConstructor;

/**
 * 配置
 *
 * @author CH
 */
@NoArgsConstructor(staticName = "newBuilder")
public class ProfileBuilder {
    private static final String[] DEFAULT = new String[]{
            "properties"
    };

    /**
     * 构建配置
     *
     * @return 配置
     */
    public Profile build() {
        return new ConfigurationProfile();
    }

    /**
     * 构建配置
     *
     * @return 配置
     */
    public Profile build(String name) {
        Profile profile = build();
        for (String s : DEFAULT) {
            profile.addProfile(name + "." + s);
        }

        return profile;
    }
}
