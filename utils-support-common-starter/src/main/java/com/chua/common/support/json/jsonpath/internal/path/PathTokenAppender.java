package com.chua.common.support.json.jsonpath.internal.path;

/**
 * @author Administrator
 */
public interface PathTokenAppender {
    /**
     * append
     *
     * @param next nex
     * @return this
     */
    PathTokenAppender appendPathToken(PathToken next);
}
