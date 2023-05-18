package com.chua.common.support.crawler;

import com.chua.common.support.collection.ConcurrentReferenceHashMap;
import com.chua.common.support.crawler.listener.Listener;
import com.chua.common.support.crawler.node.Parser;
import com.chua.common.support.crawler.request.Request;
import com.chua.common.support.crawler.request.Response;
import com.chua.common.support.jsoup.Connection;
import com.chua.common.support.jsoup.Jsoup;
import com.chua.common.support.jsoup.nodes.Document;
import com.chua.common.support.jsoup.nodes.Element;
import com.chua.common.support.jsoup.select.Elements;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.UrlUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

/**
 * jsoup工具
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/21
 */
@Slf4j
public class JsoupUtil {

    /**
     * 加载页面
     *
     * @param request 请求
     * @return Document
     */
    public static Document load(Request request) {
        if (!UrlUtils.isUrl(request.getUrl())) {
            return null;
        }
        try {
            // 请求设置
            Connection conn = Jsoup.connect(request.getUrl());
            if (request.getParam() != null && !request.getParam().isEmpty()) {
                conn.data(MapUtils.asStringMap(request.getParam()));
            }
            if (request.getCookie() != null && !request.getCookie().isEmpty()) {
                conn.cookies(request.getCookie());
            }
            if (request.getHeader() != null && !request.getHeader().isEmpty()) {
                conn.headers(request.getHeader());
            }
            if (request.getUserAgent() != null) {
                conn.userAgent(request.getUserAgent());
            }
            if (request.getReferrer() != null) {
                conn.referrer(request.getReferrer());
            }
            conn.timeout(request.getTimeout());
            conn.maxBodySize(0);

            //代理
            if (request.getProxy() != null) {
                conn.proxy(request.getProxy());
            }

            if (log.isDebugEnabled()) {
                log.debug("开始解析远程地址: {}", request.getUrl());
            }
            // 发出请求
            return request.isIfPost() ? conn.post() : conn.get();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 非页面
     *
     * @param request 请求
     * @return 源码
     */
    public static String loadPageSource(Request request) {
        if (!UrlUtils.isUrl(request.getUrl())) {
            return null;
        }
        try {
            // 请求设置
            Connection conn = Jsoup.connect(request.getUrl());
            if (request.getParam() != null && !request.getParam().isEmpty()) {
                conn.data(MapUtils.asStringMap(request.getParam()));
            }
            if (request.getCookie() != null && !request.getCookie().isEmpty()) {
                conn.cookies(request.getCookie());
            }
            if (request.getHeader() != null && !request.getHeader().isEmpty()) {
                conn.headers(request.getHeader());
            }
            if (request.getUserAgent() != null) {
                conn.userAgent(request.getUserAgent());
            }
            if (request.getReferrer() != null) {
                conn.referrer(request.getReferrer());
            }
            conn.timeout(request.getTimeout());
            conn.maxBodySize(0);

            // 代理
            if (request.getProxy() != null) {
                conn.proxy(request.getProxy());
            }

            conn.ignoreContentType(true);
            conn.method(request.isIfPost() ? Connection.Method.POST : Connection.Method.GET);

            // 发出请求
            Connection.Response resp = conn.execute();
            return resp.body();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 获取页面上所有超链接地址 （<a>标签的href值）
     *
     * @param html 页面文档
     * @return Set<String>
     */
    public static Set<String> findLinks(Document html) {

        if (html == null) {
            return null;
        }

        // element
//        *
//         *
//         * Elements resultSelect = html.select(tagName);	// 选择器方式
//         * Element resultId = html.getElementById(tagName);	// 元素ID方式
//         * Elements resultClass = html.getElementsByClass(tagName);	// ClassName方式
//         * Elements resultTag = html.getElementsByTag(tagName);	// html标签方式 "body"
//         *
        Elements hrefElements = html.select("a[href]");

        // 抽取数据
        Set<String> links = new HashSet<String>();
        if (hrefElements != null && hrefElements.size() > 0) {
            for (Element item : hrefElements) {
                //href、abs:href
                String href = item.attr("abs:href");
                if (UrlUtils.isUrl(href)) {
                    links.add(href);
                }
            }
        }
        return links;
    }

    /**
     * 获取页面上所有图片地址 （<a>标签的href值）
     *
     * @param html html
     * @return Set<String>
     */
    public static Set<String> findImages(Document html) {

        Elements imgs = html.getElementsByTag("img");

        Set<String> images = new HashSet<String>();
        if (imgs != null && imgs.size() > 0) {
            for (Element element : imgs) {
                String imgSrc = element.attr("abs:src");
                images.add(imgSrc);
            }
        }

        return images;
    }
    private static final Map<Object, Map<String, Object>> CACHE = new ConcurrentReferenceHashMap<>(128);

    /**
     * 保存对象
     * @param baseUri uri
     * @param parser 解析器
     * @param parse 结果
     */
    @SuppressWarnings("ALL")
    public static void output(String baseUri, Parser parser, Object parse) {
        Listener listener = parser.getCrawlerBuilder().listener();
        if(null != listener) {
            listener.listen(new Response(baseUri, parser, parse));
        }

        List<Field> fields = ClassUtils.getFields(parser);
        Field tpl = null;
        for (Field field : fields) {
            if(Map.class.isAssignableFrom(field.getType())) {
                tpl = field;
                break;
            }
        }

        if(null == tpl) {
            return;
        }
        Map<String, Object> absorb = (Map<String, Object>) ClassUtils.getFieldValue(tpl, parser);
        if (null == absorb) {
            absorb = new LinkedHashMap<>();
            synchronized (absorb) {
                absorb.put(baseUri, parse);
            }
        }
        Field finalTpl = tpl;
        Map<String, Object> ifAbsent = CACHE.computeIfAbsent(parser, new Function<Object, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(Object o) {
                Map<String, Object> absorb = (Map<String, Object>) ClassUtils.getFieldValue(finalTpl, parser);
                if (null == absorb) {
                    return new LinkedHashMap<>();
                }
                return absorb;
            }
        });
        ifAbsent.put(baseUri, parse);
    }
}
