package com.chua.example.match;

import com.chua.common.support.matcher.AntPathMatcher;
import com.chua.common.support.matcher.PathMatcher;
import lombok.extern.slf4j.Slf4j;

/**
 * @author CH
 */
@Slf4j
public class MatchExample {

    public static void main(String[] args) {
        String v = "https://xingzhengquhua.bmcx.com/330700000000__xingzhengquhua/";
        String p = "*";
        PathMatcher pathMatcher = new AntPathMatcher();
        log.info("{} -> {} = {}", new Object[]{v, p, pathMatcher.match(v, v)});
    }
}
