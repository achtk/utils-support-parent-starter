package com.chua.common.support.lang.spider.xsoup.xevaluator;

import com.chua.common.support.lang.spider.xsoup.Element;
import com.chua.common.support.lang.spider.xsoup.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * XPath results.
 *
 * @author code4crafter@gmail.com
 */
public class DefaultElements extends ArrayList<Element> implements Elements {

    private com.chua.common.support.jsoup.select.Elements elements;

    private ElementOperator elementOperator;

    public DefaultElements(com.chua.common.support.jsoup.select.Elements elements, ElementOperator elementOperator) {
        this.elements = elements;
        this.elementOperator = elementOperator;
        initList();
    }

    private void initList() {
        for (com.chua.common.support.jsoup.nodes.Element element : elements) {
            this.add(new DefaultXElement(element, elementOperator));
        }
    }

    @Override
    public String get() {
        if (size() < 1) {
            return null;
        } else {
            return get(0).get();
        }
    }

    @Override
    public List<String> list() {
        List<String> resultStrings = new ArrayList<String>();
        for (Element xElement : this) {
            String text = xElement.get();
            if (text != null) {
                resultStrings.add(text);
            }
        }
        return resultStrings;
    }

    @Override
    public String toString() {
        return get();
    }

    @Override
    public com.chua.common.support.jsoup.select.Elements getElements() {
        return elements;
    }
}
