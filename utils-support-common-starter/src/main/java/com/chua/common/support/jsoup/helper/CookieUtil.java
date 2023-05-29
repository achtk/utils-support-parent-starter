package com.chua.common.support.jsoup.helper;

import com.chua.common.support.jsoup.Connection;
import com.chua.common.support.utils.StringUtils;

import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 Helper functions to support the Cookie Manager / Cookie Storage in HttpConnection.

 @since 1.14.1 */
class CookieUtil {
        private static final Map<String, List<String>> EMPTY_REQUEST_HEADERS = Collections.unmodifiableMap(new HashMap<>());
    private static final String SEP = "; ";
    private static final String COOKIE_NAME = "Cookie";
    private static final String COOKIE_2_NAME = "Cookie2";

    /**
     Pre-request, get any applicable headers out of the Request cookies and the Cookie Store, and add them to the request
     headers. If the Cookie Store duplicates any Request cookies (same name and value), they will be discarded.
     */
    static void applyCookiesToRequest(HttpJsoupConnection.Request req, HttpURLConnection con) throws IOException {
        Set<String> cookieSet = requestCookieSet(req);
        Set<String> cookies2 = null;

        Map<String, List<String>> storedCookies = req.cookieManager().get(asUri(req.url), EMPTY_REQUEST_HEADERS);
        for (Map.Entry<String, List<String>> entry : storedCookies.entrySet()) {
            List<String> cookies = entry.getValue();
            if (cookies == null || cookies.size() == 0) {
                continue;
            }

            String key = entry.getKey();
            Set<String> set;
            if (COOKIE_NAME.equals(key)) {
                set = cookieSet;
            } else if (COOKIE_2_NAME.equals(key)) {
                set = new HashSet<>();
                cookies2 = set;
            } else {
                continue;
            }
            set.addAll(cookies);
        }

        if (cookieSet.size() > 0) {
            con.addRequestProperty(COOKIE_NAME, StringUtils.join(cookieSet, SEP));
        }
        if (cookies2 != null && cookies2.size() > 0) {
            con.addRequestProperty(COOKIE_2_NAME, StringUtils.join(cookies2, SEP));
        }
    }

    private static LinkedHashSet<String> requestCookieSet(Connection.Request req) {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        for (Map.Entry<String, String> cookie : req.cookies().entrySet()) {
            set.add(cookie.getKey() + "=" + cookie.getValue());
        }
        return set;
    }

    static URI asUri(URL url) throws IOException {
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            MalformedURLException ue = new MalformedURLException(e.getMessage());
            ue.initCause(e);
            throw ue;
        }
    }

    static void storeCookies(HttpJsoupConnection.Request req, URL url, Map<String, List<String>> resHeaders) throws IOException {
        req.cookieManager().put(CookieUtil.asUri(url), resHeaders);
    }
}
