package com.chua.common.support.objects.scanner;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.mapping.annotations.MappingAddress;

/**
 * 资源扫描器
 *
 * @author CH
 * @since 2023/09/02
 */
@Spi("mapping")
public class MappingAnnotationResourceScanner extends BaseAnnotationResourceScanner<MappingAddress> {

    public MappingAnnotationResourceScanner(String[] packages) {
        super(packages);
    }
}
