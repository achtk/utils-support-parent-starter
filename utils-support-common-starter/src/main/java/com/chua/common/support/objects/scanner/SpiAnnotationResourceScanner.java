package com.chua.common.support.objects.scanner;

import com.chua.common.support.annotations.Spi;

/**
 * 资源扫描器
 *
 * @author CH
 * @since 2023/09/02
 */
@Spi("Spi")
public class SpiAnnotationResourceScanner extends BaseAnnotationResourceScanner<Spi> {

    public SpiAnnotationResourceScanner(String[] packages) {
        super(packages);
    }
}
