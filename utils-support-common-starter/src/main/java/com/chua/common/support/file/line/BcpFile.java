package com.chua.common.support.file.line;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.resource.ResourceConfiguration;

/**
 * bcp
 *
 * @author CH
 */
@Spi("bcp")
public class BcpFile extends TsvFile {

    public BcpFile(ResourceConfiguration resourceConfiguration) {
        super(resourceConfiguration);
    }

}
