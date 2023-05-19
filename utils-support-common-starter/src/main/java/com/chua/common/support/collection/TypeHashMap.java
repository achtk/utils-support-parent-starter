package com.chua.common.support.collection;

import com.chua.common.support.lang.profile.DelegateProfile;
import com.chua.common.support.lang.profile.Profile;

import java.util.Map;

/**
 * 类型集合
 *
 * @author CH
 * @version 1.0.0
 */
public class TypeHashMap extends DelegateProfile implements Profile {

    public TypeHashMap() {
    }

    public TypeHashMap(Map args) {
        super();
        addProfile(args);
    }
}
