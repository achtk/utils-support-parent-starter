package com.chua.common.support.lang.spider.xsoup.xevaluator;


import com.chua.common.support.lang.spider.xsoup.Element;

/**
 * XPath result.
 *
 * @author code4crafter@gmail.com
 */
public class DefaultXElement implements Element {

    private com.chua.common.support.jsoup.nodes.Element element;

    private ElementOperator elementOperator;

    public DefaultXElement(com.chua.common.support.jsoup.nodes.Element element, ElementOperator elementOperator) {
        this.element = element;
        this.elementOperator = elementOperator;
    }

    @Override
    public String get() {
        return get(elementOperator);
    }

    protected String get(ElementOperator elementOperator) {
        if (elementOperator == null) {
            return element.toString();
        } else {
            return elementOperator.operate(element);
        }
    }

    public String toString() {
        return get();
    }

    @Override
    public com.chua.common.support.jsoup.nodes.Element getElement() {
        return element;
    }
}
