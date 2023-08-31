package com.chua.common.support.lang.spider.utils;

/**
 * Some constants of Http protocal.
 *
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
public abstract class BaseHttpConstant {

    public static abstract class BaseMethod {

        public static final String GET = "GET";

        public static final String HEAD = "HEAD";

        public static final String POST = "POST";

        public static final String PUT = "PUT";

        public static final String DELETE = "DELETE";

        public static final String TRACE = "TRACE";

        public static final String CONNECT = "CONNECT";

    }

    public static abstract class BaseStatusCode {

        public static final int CODE_200 = 200;

    }

    public static abstract class BaseHeader {

        public static final String REFERER = "Referer";

        public static final String USER_AGENT = "User-Agent";
    }

}
