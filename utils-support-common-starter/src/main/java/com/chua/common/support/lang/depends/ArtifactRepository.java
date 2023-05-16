package com.chua.common.support.lang.depends;

/**
 * 资源
 * @author CH
 */
public interface ArtifactRepository {
    /**
     * id
     * @return id
     */
    String id();
    /**
     * url
     * @return url
     */
    String url();

    /**
     * 临时目录
     * @return 临时目录
     */
    String cachePath();
}
