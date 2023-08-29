package com.chua.common.support.lang.spider.xsoup.xevaluator;

import com.chua.common.support.jsoup.nodes.Element;
import com.chua.common.support.lang.spider.xsoup.Elements;
import com.chua.common.support.lang.spider.xsoup.PathEvaluator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author code4crafter@gmail.com
 */
public class CombingXPathEvaluator implements PathEvaluator {

    private final List<PathEvaluator> xPathEvaluators;

    public CombingXPathEvaluator(List<PathEvaluator> xPathEvaluators) {
        this.xPathEvaluators = xPathEvaluators;
    }

    public CombingXPathEvaluator(PathEvaluator... xPathEvaluators) {
        this.xPathEvaluators = Arrays.asList(xPathEvaluators);
    }

    @Override
    public Elements evaluate(Element element) {
        List<Elements> xElementses = new ArrayList<Elements>();
        for (PathEvaluator xPathEvaluator : xPathEvaluators) {
            xElementses.add(xPathEvaluator.evaluate(element));
        }
        return new CombiningDefaultXElements(xElementses);
    }

    @Override
    public boolean hasAttribute() {
        for (PathEvaluator xPathEvaluator : xPathEvaluators) {
            if (xPathEvaluator.hasAttribute()) {
                return true;
            }
        }
        return false;
    }
}
