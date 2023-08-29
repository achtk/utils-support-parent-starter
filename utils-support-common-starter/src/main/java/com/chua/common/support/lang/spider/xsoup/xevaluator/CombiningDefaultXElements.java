package com.chua.common.support.lang.spider.xsoup.xevaluator;

import com.chua.common.support.lang.spider.xsoup.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author code4crafter@gmail.com
 */
public class CombiningDefaultXElements implements Elements {

    private List<Elements> elementsList;

    public CombiningDefaultXElements(List<Elements> elementsList) {
        this.elementsList = elementsList;
    }

    public CombiningDefaultXElements(Elements... elementsList) {
        this.elementsList = Arrays.asList(elementsList);
    }

    @Override
    public String get() {
        for (Elements xElements : elementsList) {
            String result = xElements.get();
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public List<String> list() {
        List<String> results = new ArrayList<String>();
        for (Elements xElements : elementsList) {
            results.addAll(xElements.list());
        }
        return results;
    }

    public com.chua.common.support.jsoup.select.Elements getElements() {
        com.chua.common.support.jsoup.select.Elements elements = new com.chua.common.support.jsoup.select.Elements();
        for (Elements xElements : elementsList) {
            elements.addAll(xElements.getElements());
        }
        return elements;
    }
}
