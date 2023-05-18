package com.chua.htmlunit.support.crawler.parser;

import com.chua.common.support.crawler.node.PageParser;
import com.chua.common.support.jsoup.nodes.Document;
import com.chua.common.support.jsoup.nodes.Element;
import org.jsoup.Jsoup;
import org.seimicrawler.xpath.JXDocument;

import java.util.*;

import static com.chua.common.support.constant.NumberConstant.DEFAULT_INITIAL_CAPACITY;

/**
 * xparser
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/21
 */
public interface XpathParser extends PageParser {
    /**
     * 解析
     *
     * @param html          page html data
     * @param pageVoElement pageVo html data
     * @param pageVo        pageVo object
     * @return 结果信息
     */
    @Override
    default Object parse(Document html, Element pageVoElement, Object pageVo) {
        Set<String> xpath = xpath();
        if (null == xpath) {
            return pageVo;
        }
        Map<String, List<String>> param = new HashMap<>(DEFAULT_INITIAL_CAPACITY);
        JXDocument jxDocument = JXDocument.create(Jsoup.parse(html.html()));
        for (String s : xpath) {
            List<Object> list = jxDocument.sel(s);
            if (null == list || list.size() == 0) {
                continue;
            }
            List<String> value = new ArrayList<>(list.size());
            for (Object o : list) {
                if (o instanceof Element) {
                    value.add(((Element) o).text());
                }
                if (o instanceof String) {
                    value.add(o.toString());
                }
            }
            param.put(s, value);
        }
        parse(html, param);
        return param;
    }

    /**
     * 解析
     *
     * @param html    html
     * @param content 内容
     */
    void parse(Document html, Map<String, List<String>> content);

    /**
     * 表达式
     *
     * @return 表达式
     */
    Set<String> xpath();
}
