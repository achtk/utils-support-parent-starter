package com.chua.common.support.resource.repository.resolver;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.matcher.PathMatcher;
import com.chua.common.support.resource.repository.Metadata;
import com.chua.common.support.utils.FileUtils;

import java.net.URL;
import java.util.Collections;
import java.util.List;

import static com.chua.common.support.constant.CommonConstant.COMPRESS_URL_PREFIX;

/**
 * 解释器
 *
 * @author CH
 */
@Spi("compress")
public final class CompressResolver implements Resolver {

    private PathMatcher pathMatcher = PathMatcher.INSTANCE;

    @Override
    public List<Metadata> resolve(URL root, String name) {
        String url = root.toExternalForm().replace(COMPRESS_URL_PREFIX, "");
        String suffix = FileUtils.getSimpleExtension(url);


        return Collections.emptyList();
    }
}
