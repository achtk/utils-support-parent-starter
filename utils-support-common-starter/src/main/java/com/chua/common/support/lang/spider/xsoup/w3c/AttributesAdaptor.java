package com.chua.common.support.lang.spider.xsoup.w3c;

import com.chua.common.support.jsoup.nodes.Attribute;
import com.chua.common.support.jsoup.nodes.Attributes;
import org.w3c.dom.Attr;

import java.util.ArrayList;
import java.util.List;

/**
 * @author code4crafer@gmail.com
 */
public class AttributesAdaptor {

    private Attributes attributes;

    private com.chua.common.support.jsoup.nodes.Element element;

    private List<Attr> attrList;

    public AttributesAdaptor(Attributes attributes, com.chua.common.support.jsoup.nodes.Element element) {
        this.attributes = attributes;
        this.element = element;
        attrList = new ArrayList<Attr>();
        for (Attribute attribute : attributes) {
            attrList.add(new AttributeAdaptor(attribute, element));
        }
    }

    public List<Attr> get() {
        return attrList;
    }
}
