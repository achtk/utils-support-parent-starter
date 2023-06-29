package com.chua.common.support.bean;

import com.chua.common.support.constant.CommonConstant;

/**
 * 特性
 *
 * @author CH
 */
public enum ReadFeature implements ReadFeatureHandler {
    NULL_STRING() {
        @Override
        public Object handle(Object value) {
            if (value instanceof String && CommonConstant.NULL.equalsIgnoreCase(value.toString())) {
                return CommonConstant.EMPTY;
            }
            return super.handle(value);
        }
    };

    @Override
    public Object handle(Object value) {
        return value;
    }
}
