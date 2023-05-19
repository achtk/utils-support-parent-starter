package com.chua.pytorch.support.biggan;

import com.chua.common.support.feature.DetectionConfiguration;

/**
 * biggan
 * @author CH
 */
public class BigGAN512 extends BigGAN128 {

    public BigGAN512(DetectionConfiguration configuration) {
        super(configuration, 512, 0.4f);
    }
}
