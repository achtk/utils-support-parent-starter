package com.chua.common.support.function.strategy.name;


import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;
import com.chua.common.support.pojo.OssSystem;

/**
 * 拒绝策略
 *
 * @author CH
 * @since 2022/8/3 15:07
 */
@Spi("null")
@SpiOption("空置")
public class RejectNullStrategy implements RejectStrategy {


    @Override
    public byte[] reject(OssSystem ossSystem) {
        return new byte[0];
    }
}
