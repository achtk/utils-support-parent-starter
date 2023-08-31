package com.chua.common.support.lang.spider.xsoup.xevaluator;


import com.chua.common.support.jsoup.nodes.Element;
import com.chua.common.support.jsoup.select.AbstractEvaluator;
import com.chua.common.support.jsoup.select.Collector;
import com.chua.common.support.lang.spider.xsoup.Elements;
import com.chua.common.support.lang.spider.xsoup.PathEvaluator;

/**
 * 默认路径评估者
 *
 * @author code4crafter@gmail.com
 * @date 2023/08/31
 */
public class DefaultPathEvaluator implements PathEvaluator {

    private AbstractEvaluator evaluator;

    private ElementOperator elementOperator;

    public DefaultPathEvaluator(AbstractEvaluator evaluator, ElementOperator elementOperator) {
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
        return new DefaultElements(elements, elementOperator);
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
