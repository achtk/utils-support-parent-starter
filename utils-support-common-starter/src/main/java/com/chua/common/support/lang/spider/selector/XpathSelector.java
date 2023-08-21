package com.chua.common.support.lang.spider.selector;


import com.chua.common.support.jsoup.nodes.Element;
import com.chua.common.support.utils.CollectionUtils;
import org.w3c.dom.xpath.XPathEvaluator;

import java.util.List;

/**
 * XPath selector based on Xsoup.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.3.0
 */
public class XpathSelector extends BaseElementSelector {

    private final XPathEvaluator xPathEvaluator;

    public XpathSelector(String xpathStr) {
        this.xPathEvaluator = Xsoup.compile(xpathStr);
    }

    @Override
    public String select(Element element) {
        return xPathEvaluator.evaluate(element).get();
    }

    @Override
    public List<String> selectList(Element element) {
        return xPathEvaluator.evaluate(element).list();
    }

    @Override
    public Element selectElement(Element element) {
        List<Element> elements = selectElements(element);
        if (CollectionUtils.isNotEmpty(elements)) {
            return elements.get(0);
        }
        return null;
    }

    @Override
    public List<Element> selectElements(Element element) {
        return xPathEvaluator.evaluate(element).getElements();
    }

    @Override
    public boolean hasAttribute() {
        return xPathEvaluator.hasAttribute();
    }
}
