package com.chua.pytorch.support.biggan;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.feature.DetectionConfiguration;

/**
 * biggan
 *
 * @author CH
 */
@Spi("gan256")
public class BigGAN256 extends BigGAN128 {

    public BigGAN256(DetectionConfiguration configuration) {
        super(configuration, 256, 0.4f);
    }
}
