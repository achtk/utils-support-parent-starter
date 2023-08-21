package com.chua.common.support.lang.spider.utils;

import com.chua.common.support.lang.spider.model.annotation.ExtractBy;
import com.chua.common.support.lang.spider.selector.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Tools for annotation converting. <br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.1
 */
public class ExtractorUtils {

    public static Selector getSelector(ExtractBy extractBy) {
        String value = extractBy.value();
        Selector selector;
        switch (extractBy.type()) {
            case Css:
                selector = new CssSelector(value);
                break;
            case Regex:
                selector = new RegexSelector(value);
                break;
            break;
            case JsonPath:
                selector = new JsonPathSelector(value);
                break;
            default:
                selector = new XpathSelector(value);
        }
        return selector;
    }

    public static List<Selector> getSelectors(ExtractBy[] extractBies) {
        List<Selector> selectors = new ArrayList<Selector>();
        if (extractBies == null) {
            return selectors;
        }
        for (ExtractBy extractBy : extractBies) {
            selectors.add(getSelector(extractBy));
        }
        return selectors;
    }
}
