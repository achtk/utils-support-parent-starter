package com.chua.common.support.lang.spider.xsoup.xevaluator;


import com.chua.common.support.jsoup.nodes.Element;
import com.chua.common.support.jsoup.select.Collector;
import com.chua.common.support.jsoup.select.Elements;
import com.chua.common.support.jsoup.select.Evaluator;
import com.chua.common.support.lang.spider.xsoup.XElements;
import com.chua.common.support.lang.spider.xsoup.XPathEvaluator;

/**
 * @author code4crafter@gmail.com
 */
public class DefaultXPathEvaluator implements XPathEvaluator {

    private Evaluator evaluator;

    private ElementOperator elementOperator;

    public DefaultXPathEvaluator(Evaluator evaluator, ElementOperator elementOperator) {
        this.evaluator = evaluator;
        this.elementOperator = elementOperator;
    }

    @Override
    public XElements evaluate(Element element) {
        Elements elements;
        if (evaluator != null) {
            elements = Collector.collect(evaluator, element);
        } else {
            elements = new Elements();
            elements.add(element);
        }
        return new DefaultXElements(elements, elementOperator);
    }

    @Override
    public boolean hasAttribute() {
        return elementOperator != null;
    }

    public Evaluator getEvaluator() {
        return evaluator;
    }

    public String getAttribute() {
        if (elementOperator == null) {
            return null;
        }
        return elementOperator.toString();
    }

    public ElementOperator getElementOperator() {
        return elementOperator;
    }
}