package com.chua.common.support.lang.depends;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 资源
 * @author CH
 */
@Data
@AllArgsConstructor
public class SimpleArtifactRepository implements ArtifactRepository {

    private String id;
    private String url;
    private String cachePath;

    @Override
    public String id() {
        return id;
    }

    @Override
    public String url() {
        return url;
    }

    @Override
    public String cachePath() {
        return cachePath;
    }
}