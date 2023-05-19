package com.chua.common.support.protocol.server;

import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.collection.TypeHashMap;

/**
 * 服务器设置
 *
 * @author CH
 */
public class ServerRequest extends TypeHashMap {

    public ServerRequest(ServerOption serverOption) {
        BeanMap beanMap = BeanMap.of(serverOption);
        super.addProfile(beanMap);
    }

}
