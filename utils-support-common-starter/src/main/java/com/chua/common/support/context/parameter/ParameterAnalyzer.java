package com.chua.common.support.context.parameter;

import com.chua.common.support.lang.profile.Profile;
import com.chua.common.support.reflection.describe.ParameterDescribe;

/**
 * 参数解析
 *
 * @author CH
 */
public interface ParameterAnalyzer {
    /**
     * 解析值
     *
     * @param parameterDescribe 参数
     * @param profile              条件
     * @return 结果
     */
    Object analyzer(ParameterDescribe parameterDescribe, Profile profile);
}
