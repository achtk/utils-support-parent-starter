package com.chua.common.support.lang.spider.xsoup.xevaluator;


import com.chua.common.support.jsoup.nodes.Element;
import com.chua.common.support.jsoup.select.AbstractEvaluator;
import com.chua.common.support.jsoup.select.Collector;
import com.chua.common.support.lang.spider.xsoup.Elements;
import com.chua.common.support.lang.spider.xsoup.PathEvaluator;

/**
 * @author code4crafter@gmail.com
 */
public class DefaultXPathEvaluator implements PathEvaluator {

    private AbstractEvaluator evaluator;

    private ElementOperator elementOperator;

    public DefaultXPathEvaluator(AbstractEvaluator evaluator, ElementOperator elementOperator) {
        this.evaluator = evaluator;
        this.elementOperator = elementOperator;
    }

    @Override
    public Elements evaluate(Element element) {
        com.chua.common.support.jsoup.select.Elements elements;
        if (evaluator != null) {
            elements = Collector.collect(evaluator, element);
        } else {
            elements = new com.chua.common.support.jsoup.select.Elements();
            elements.add(element);
        }
        return new DefaultXElements(elements, elementOperator);
    }

    @Override
    public boolean hasAttribute() {
        return elementOperator != null;
    }

    public AbstractEvaluator getEvaluator() {
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
