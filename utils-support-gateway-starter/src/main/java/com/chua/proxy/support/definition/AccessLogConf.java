package com.chua.proxy.support.definition;

import lombok.Data;

/**
 * 访问日志conf
 *
 * @author CH
 */
@Data
public class AccessLogConf {

    public static final long BODY_LIMIT_MAX = 1L << 20;

    private boolean reqHeadersEnabled = false;

    private boolean respHeadersEnabled = false;

    private boolean reqBodyEnabled = false;

    private boolean respBodyEnabled = false;

    private long bodyLimit = BODY_LIMIT_MAX;

    public void setBodyLimit(long bodyLimit) {
        if (bodyLimit <= 0) {
            throw new IllegalArgumentException("body limit can not must bigger than 0.");
        }
        this.bodyLimit = Math.min(bodyLimit, BODY_LIMIT_MAX);
    }

}
