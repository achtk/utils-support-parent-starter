package com.chua.common.support.lang.spider.selector;

import com.chua.common.support.jsoup.Jsoup;
import com.chua.common.support.jsoup.nodes.Document;
import com.chua.common.support.jsoup.nodes.Element;
import com.chua.common.support.lang.spider.utils.BaseSelectorUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author code4crafter@gmail.com
 * @since 0.3.0
 */
public abstract class BaseElementSelector implements Selector, ElementSelector {
    private Document parse(String text) {
        // Jsoup could not parse <tr></tr> or <td></td> tag directly
        // https://stackoverflow.com/questions/63607740/jsoup-couldnt-parse-tr-tag
        text = BaseSelectorUtils.preParse(text);
        return Jsoup.parse(text);
    }

    @Override
    public String select(String text) {
        if (text != null) {
            return select(parse(text));
        }
        return null;
    }

    @Override
    public List<String> selectList(String text) {
        if (text != null) {
            return selectList(parse(text));
        } else {
            return new ArrayList<String>();
        }
    }

    public Element selectElement(String text) {
        if (text != null) {
            return selectElement(parse(text));
        }
        return null;
    }

    public List<Element> selectElements(String text) {
        if (text != null) {
            return selectElements(parse(text));
        } else {
            return new ArrayList<Element>();
        }
    }

    /**
     * 选择节点
     * @param element 元素
     * @return 元素
     */
    public abstract Element selectElement(Element element);
    /**
     * 选择节点
     * @param element 元素
     * @return 元素
     */
    public abstract List<Element> selectElements(Element element);

    /**
     * 是否存在属性
     * @return 是否存在属性
     */
    public abstract boolean hasAttribute();

}
