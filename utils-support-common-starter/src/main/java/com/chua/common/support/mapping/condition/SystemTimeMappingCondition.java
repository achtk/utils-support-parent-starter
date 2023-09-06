package com.chua.common.support.mapping.condition;

import com.chua.common.support.lang.profile.Profile;

/**
 * 条件
 *
 * @author CH
 */
public class SystemTimeMappingCondition implements MappingCondition {

    @Override
    public String resolve(Profile profile, String name, String url) {
        return System.currentTimeMillis() + "";
    }
}
