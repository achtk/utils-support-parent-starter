package com.chua.common.support.crawler.node;

import com.chua.common.support.jsoup.Jsoup;
import com.chua.common.support.jsoup.nodes.Document;
import com.chua.common.support.jsoup.nodes.Element;
import com.chua.common.support.jsoup.select.Elements;

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
        Document document = Jsoup.parse(html.html());
        Map<String, List<String>> param = new HashMap<>(DEFAULT_INITIAL_CAPACITY);
        for (String s : xpath) {
            Elements elements = document.selectXpath(s);
            if (elements.isEmpty()) {
                continue;
            }
            List<String> value = new ArrayList<>(elements.size());
            for (Element o : elements) {
                value.add(o.text());
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
