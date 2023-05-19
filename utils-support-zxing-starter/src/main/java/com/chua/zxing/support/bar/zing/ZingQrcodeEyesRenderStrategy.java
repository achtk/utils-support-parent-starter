package com.chua.zxing.support.bar.zing;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * zing strategy
 *
 * @author CH
 * @since 2022-05-16
 */
@AllArgsConstructor
@Getter
public enum ZingQrcodeEyesRenderStrategy {
    /**
     * p
     */
    POINT(2, 5),
    /**
     * pb
     */
    POINT_BORDER(0, 7);

    private final int start;

    private final int end;

}