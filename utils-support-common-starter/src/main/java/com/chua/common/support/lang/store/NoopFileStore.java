package com.chua.common.support.lang.store;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiDefault;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 文件存储
 *
 * @author CH
 */
@Spi("noop")
@SpiDefault
public class NoopFileStore implements FileStore {

    @Override
    public void write(String applicationName, String message, String parent) {

    }

    @Override
    public List<Map<String, Object>> search(String keyword) {
        return Collections.emptyList();
    }

    @Override
    public void close() throws Exception {

    }
}
