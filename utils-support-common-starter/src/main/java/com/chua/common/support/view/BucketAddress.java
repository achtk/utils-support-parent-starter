package com.chua.common.support.view;

import com.chua.common.support.function.Splitter;
import com.chua.common.support.utils.StringUtils;
import lombok.Data;

import java.util.Collections;
import java.util.Map;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * bucket
 *
 * @author CH
 */
@Data
public class BucketAddress {
    private final Map<String, String> param;
    private String bucket;

    private String url;

    private String path;

    public BucketAddress(String url) {
        this.url = initialUrl(url);
        this.bucket = initialBucket();
        this.path = this.url.substring(bucket.length() + 1);
        this.param = createParam(url);
    }

    /**
     * 路径
     *
     * @return 路径
     */
    private Map<String, String> createParam(String url) {
        url = StringUtils.startWithMove(url, SYMBOL_LEFT_SLASH).replace(bucket, "");
        if (url.contains(SYMBOL_QUESTION)) {
            int index = url.indexOf(SYMBOL_QUESTION);
            return Splitter.on(SYMBOL_AND).withKeyValueSeparator(SYMBOL_EQUALS).split(url.substring(index + 1));
        }
        return Collections.emptyMap();
    }

    private String initialUrl(String url) {
        if (url.contains(SYMBOL_QUESTION)) {
            int index = url.indexOf(SYMBOL_QUESTION);
            return url.substring(0, index);
        }
        return url;
    }

    private String initialBucket() {
        if (url.contains(SYMBOL_QUESTION)) {
            int index = url.indexOf(SYMBOL_QUESTION);
            return StringUtils.startWithMove(url.substring(0, index), SYMBOL_LEFT_SLASH);
        }
        int index = StringUtils.startWithMove(url, SYMBOL_LEFT_SLASH).indexOf(SYMBOL_LEFT_SLASH);
        return StringUtils.startWithMove(url.substring(0, index + 1), SYMBOL_LEFT_SLASH);
    }
}
