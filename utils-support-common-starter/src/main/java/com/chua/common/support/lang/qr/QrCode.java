package com.chua.common.support.lang.qr;

import java.io.OutputStream;

/**
 * qr码
 *
 * @author CH
 * @since 2023/09/05
 */
public interface QrCode {

    /**
     * 输出
     *
     * @param outputStream 流
     */
    void out(OutputStream outputStream);
}
