package com.chua.datasource.support.file;

import com.chua.common.support.lang.profile.Profile;
import com.chua.datasource.support.TableUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

/**
 * 文件适配器
 *
 * @author CH
 */
public abstract class AbstractFileSupport implements FileSupport {


    protected final Profile profile;
    protected final String file;
    protected final boolean mem;
    protected String suffix;

    public AbstractFileSupport(Profile profile, String file, String suffix) {

        this.profile = profile;
        this.file = file;
        this.suffix = suffix;
        String mode = profile.getString("mode");
        this.mem = "mem".equalsIgnoreCase(mode);
    }

    /**
     * 映射
     *
     * @param column 字段
     * @return 映射
     */
    String mapping(String column) {
        return TableUtils.mapping(profile.getType("mapping", Collections.emptyMap(), Map.class), column);
    }

    /**
     * 构建流
     *
     * @return 流
     * @throws IOException ex
     */
    abstract protected InputStream getStream() throws IOException;
}
