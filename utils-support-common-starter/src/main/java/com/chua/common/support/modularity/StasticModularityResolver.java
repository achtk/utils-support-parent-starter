package com.chua.common.support.modularity;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;

/**
 * 解释器
 * @author CH
 */
@Spi("default:stastic")
@SpiOption("测试")
public class StasticModularityResolver implements ModularityResolver {
    @Override
    public void onEvent(MsgEvent msgEvent, long l, boolean b) throws Exception {
        System.out.println("测试");
    }
}
