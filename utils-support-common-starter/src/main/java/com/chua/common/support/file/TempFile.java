package com.chua.common.support.file;

import com.chua.common.support.resource.ResourceConfiguration;

/**
 * @author CH
 */
public class TempFile extends AbstractResourceFile {

    public TempFile() {
        super(ResourceFileConfiguration.builder().build());
    }

    public TempFile(ResourceFileConfiguration resourceConfiguration) {
        super(resourceConfiguration);
    }
}
