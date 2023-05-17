package com.chua.common.support.file;

import com.chua.common.support.resource.ResourceConfiguration;

/**
 * @author CH
 */
public class TempFile extends AbstractResourceFile {

    public TempFile() {
        super(ResourceConfiguration.builder().build());
    }

    public TempFile(ResourceConfiguration resourceConfiguration) {
        super(resourceConfiguration);
    }
}
