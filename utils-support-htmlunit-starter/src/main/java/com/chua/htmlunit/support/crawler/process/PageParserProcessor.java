package com.chua.htmlunit.support.crawler.process;

import com.chua.common.support.crawler.CrawlerBuilder;
import com.chua.common.support.crawler.FieldReflectionUtil;
import com.chua.common.support.crawler.JsoupUtil;
import com.chua.common.support.crawler.annotations.XpathQuery;
import com.chua.common.support.crawler.node.PageParser;
import com.chua.common.support.crawler.node.Parser;
import com.chua.common.support.crawler.page.AsyncPageLoader;
import com.chua.common.support.crawler.page.PageLoader;
import com.chua.common.support.crawler.process.ParserProcessor;
import com.chua.common.support.crawler.request.Request;
import com.chua.common.support.crawler.url.UrlLoader;
import com.chua.common.support.jsoup.nodes.Document;
import com.chua.common.support.jsoup.nodes.Element;
import com.chua.common.support.jsoup.select.Elements;
import com.chua.common.support.utils.ClassUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 页面解析器
 *
 * @author CH
 * @version 1.0.0
 */
@SuppressWarnings("ALL")
@Slf4j
public class PageParserProcessor implements ParserProcessor {

    private PageParser pageParser;
    private Request pageRequest;
    private Document html;
    private Class<?> classType;
    private CrawlerBuilder config;


    @Override
    public boolean matcher(Parser parser) {
        if (parser instanceof PageParser) {
            this.pageParser = (PageParser) parser;
            this.pageRequest = parser.getPageRequest();
            this.config = parser.getCrawlerBuilder();
            return true;
        }
        return false;
    }

    @Override
    public boolean processor(Parser parser, UrlLoader urlLoader) {
        PageLoader pageLoader = config.pageLoader();
        Document document = null;
        if (pageLoader instanceof AsyncPageLoader) {
            pageLoader.load(pageRequest);
            document = ((AsyncPageLoader) pageLoader).getDocument();
        } else {
            document = pageLoader.load(pageRequest);
        }
        if (document == null) {
            return false;
        }
        this.html = document;
        // ------- child link list (FIFO队列,广度优先) ----------
        //limit child spread
        if (config.allowSpread()) {
            Set<String> links = JsoupUtil.findLinks(document);
            if (links != null && links.size() > 0) {
                for (String item : links) {
                    //limit unvalid-child spread
                    if (config.validWhiteUrl(item)) {
                        urlLoader.addUrl(item);
                    }
                }
            }
        }

        // ------- pagevo ----------
        // limit unvalid-page parse, only allow spread child, finish here
        if (!config.validWhiteUrl(pageRequest.getUrl())) {
            return true;
        }

        return parseThePage();
    }


    /**
     * 解析页面
     *
     * @return
     */
    private boolean parseThePage() {
        // pagevo class-field info
        Class<?> annotationType = Object.class;
        Class<? extends Parser> aClass = pageParser.getClass();
        Type[] pageVoParserClasses = ClassUtils.getActualTypeArguments(aClass);
        if (pageVoParserClasses.length > 0) {
            Type pageVoParserClass = pageVoParserClasses[0];
            if (pageVoParserClass instanceof ParameterizedType) {
                Type[] pageVoClassTypes = ((ParameterizedType) pageVoParserClass).getActualTypeArguments();
                annotationType = (Class) pageVoClassTypes[0];
            } else {
                annotationType = (Class<?>) pageVoParserClass;
            }
        }

        if (ClassUtils.isObject(annotationType)) {
            Object parse = pageParser.parse(html, null, null);
            JsoupUtil.output("", pageParser, parse);
            return true;
        }

        XpathQuery pageVoSelect = annotationType.getAnnotation(XpathQuery.class);
        String value = (pageVoSelect != null && pageVoSelect.value() != null && pageVoSelect.value().trim().length() > 0) ? pageVoSelect.value() : "html";

        // pagevo document 2 object
        Elements pageVoElements = html.select(value);
        if (pageVoElements != null && pageVoElements.hasText()) {
            this.classType = annotationType;
            return parsePageNode(pageVoElements);
        }
        return true;
    }


    /**
     * 解析节点
     *
     * @param pageVoElements 节点
     * @return 是否完成
     */
    private boolean parsePageNode(Elements pageVoElements) {
        for (Element pageVoElement : pageVoElements) {
            Object pageVo;
            try {
                pageVo = classType.newInstance();
            } catch (Exception e) {
                return false;
            }
            Field[] fields = classType.getDeclaredFields();
            for (Field field : fields) {
                parseFieldAnnotations(pageVo, pageVoElement, field);
            }
            JsoupUtil.output(pageVoElement.baseUri(), pageParser, pageVo);
            pageParser.parse(html, pageVoElement, pageVo);
        }
        return true;
    }

    private void parseFieldAnnotations(Object pageVo, Element pageVoElement, Field field) {
        if (Modifier.isStatic(field.getModifiers())) {
            return;
        }

        // field origin value
        XpathQuery fieldSelect = field.getAnnotation(XpathQuery.class);
        String cssQuery = null;
        String selectVal = null;
        if (fieldSelect != null) {
            cssQuery = fieldSelect.value();
        }
        if (cssQuery == null || cssQuery.trim().length() == 0) {
            return;
        }

        // field value
        Object fieldValue = null;

        Elements elements = new Elements(pageVoElement);
        JXDocument jxDocument = JXDocument.create(Jsoup.parse(elements.html()));
        if (field.getGenericType() instanceof ParameterizedType) {
            ParameterizedType fieldGenericType = (ParameterizedType) field.getGenericType();
            if (fieldGenericType.getRawType().equals(List.class)) {
                //Type gtATA = fieldGenericType.getActualTypeArguments()[0];
                List<JXNode> jxNodes = jxDocument.selN(cssQuery);
                if (jxNodes != null && jxNodes.size() > 0) {

                    List<Object> fieldValueTmp = new ArrayList<>();
                    for (JXNode jxNode : jxNodes) {

                        String fieldElementOrigin = parseElement(jxNode);
                        if (fieldElementOrigin == null || fieldElementOrigin.length() == 0) {
                            continue;
                        }
                        try {
                            fieldValueTmp.add(FieldReflectionUtil.parseValue(field, fieldElementOrigin));
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }

                    if (fieldValueTmp.size() > 0) {
                        fieldValue = fieldValueTmp;
                    }
                }
            }
        } else {

            List<JXNode> jxNodes = jxDocument.selN(cssQuery);
            String fieldValueOrigin = null;
            if (jxNodes != null && jxNodes.size() > 0) {
                fieldValueOrigin = parseElement(jxNodes.get(0));
            }

            if (fieldValueOrigin == null || fieldValueOrigin.length() == 0) {
                return;
            }

            try {
                fieldValue = FieldReflectionUtil.parseValue(field, fieldValueOrigin);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        if (fieldValue != null) {
            field.setAccessible(true);
            try {
                field.set(pageVo, fieldValue);
            } catch (IllegalAccessException ignored) {
            }
        }
    }



    /**
     * 抽取元素数据
     *
     * @param fieldElement 节点
     * @return String 数据
     */
    public static String parseElement(JXNode fieldElement) {
        return fieldElement.asString();
    }
}
