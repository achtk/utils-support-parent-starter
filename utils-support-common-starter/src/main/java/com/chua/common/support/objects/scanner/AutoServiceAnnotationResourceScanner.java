package com.chua.common.support.objects.scanner;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.objects.scanner.annotations.AutoService;

/**
 * 资源扫描器
 *
 * @author CH
 * @since 2023/09/02
 */
@Spi("AutoService")
public class AutoServiceAnnotationResourceScanner extends BaseAnnotationResourceScanner<AutoService> {

    public AutoServiceAnnotationResourceScanner(String[] packages) {
        super(packages);
    }
}
