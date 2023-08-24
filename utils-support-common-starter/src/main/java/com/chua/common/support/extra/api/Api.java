package com.chua.common.support.extra.api;

import com.chua.common.support.reflection.describe.TypeDescribe;
import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.utils.ClassUtils;

/**
 * 接口
 * @author CH
 */
public interface Api {
    /**
     * 获取token
     * @return token
     */
    MessageResponse getAccessToken();

    /**
     * 清除token
     */
    void refreshAccessToken();
    /**
     * 获取结果
     * @param args 参数
     * @return 结果
     */
    default MessageResponse get(Object... args) {
        TypeDescribe typeDescribe = TypeDescribe.create(this);
        Object[] objects = ArrayUtils.subArray(args, 1);
        return (MessageResponse) typeDescribe.getMethodDescribe(args[0].toString(), ClassUtils.toType(objects))
                .invoke(this, objects);
    }
}
