package com.chua.common.support.crawler;

import com.chua.common.support.crawler.request.Request;
import com.chua.common.support.json.Json;
import com.chua.common.support.utils.NetAddress;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * url工具类<br />
 * 部分工具来自于HuTool系列
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/12/19
 */
@Slf4j
public class UrlCrawUtils {


    /**
     * 获取pageRequest
     *
     * @param config 爬虫配置
     * @param url      地址
     * @return PageRequest
     */
    public static Request createPageRequest(CrawlerBuilder config, String url) {
        String userAgent = config.userAgent().size() > 1
                ? config.userAgent().get(new SecureRandom().nextInt(config.userAgent().size()))
                : config.userAgent().size() == 1 ? config.userAgent().get(0) : null;

        Request pageRequest = Request.builder().build();
        pageRequest.setOriginal(url);
        if(url.toUpperCase().startsWith("POST")) {
            pageRequest.setIfPost(true);
            pageRequest.setUrl(url.replace("POST", "").trim());
        } else {
            pageRequest.setUrl(url);
        }

        NetAddress address = NetAddress.of(pageRequest.getUrl());
        Object param = address.getParameter("param", null);
        if(null != param) {
            Map<String, Object> rpl = new LinkedHashMap<>();
            Map<String, Object> stringObjectMap = Json.toMapStringObject(param.toString());
            if(null != stringObjectMap) {
                rpl.putAll(stringObjectMap);
            }if(null != config.param()) {
                rpl.putAll(config.param());
            }
            pageRequest.setParam(rpl);
            String url1 = pageRequest.getUrl();
            int indexOf = url1.indexOf("&param");
            int indexOf1 = url1.indexOf("&", indexOf + 1);
            pageRequest.setUrl(url1.substring(0, indexOf) + (indexOf1 == -1 ? "" : url1.substring(indexOf1)));
        } else {
            pageRequest.setParam(config.param());
        }

        pageRequest.setCookie(config.cookie());
        pageRequest.setHeader(config.header());
        pageRequest.setUserAgent(userAgent);
        pageRequest.setReferrer(config.referrer());
        pageRequest.setIfPost(config.ifPost());
        pageRequest.setTimeout(config.connectionTimeoutMillis());
        pageRequest.setProxy(null);
        pageRequest.setValidateTlsCertificates(config.isValidateTlsCertificates());
        pageRequest.setEvent(config.event());

        return pageRequest;
    }

}
