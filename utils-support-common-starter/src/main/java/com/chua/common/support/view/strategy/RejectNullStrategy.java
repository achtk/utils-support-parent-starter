package com.chua.common.support.view.strategy;


import com.chua.common.support.annotations.Spi;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 拒绝策略
 *
 * @author CH
 * @since 2022/8/3 15:07
 */
@Spi("null")
public class RejectNullStrategy implements RejectStrategy {

    @Override
    public void reject(String path, String mode, OutputStream os) {
        try {
            os.write(new byte[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
