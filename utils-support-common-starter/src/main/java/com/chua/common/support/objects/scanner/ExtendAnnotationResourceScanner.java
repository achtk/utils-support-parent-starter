package com.chua.common.support.objects.scanner;

import com.chua.common.support.annotations.Extension;
import com.chua.common.support.annotations.Spi;

/**
 * 资源扫描器
 *
 * @author CH
 * @since 2023/09/02
 */
@Spi("Extension")
public class ExtendAnnotationResourceScanner extends BaseAnnotationResourceScanner<Extension> {

    public ExtendAnnotationResourceScanner(String[] packages) {
        super(packages);
    }
}
