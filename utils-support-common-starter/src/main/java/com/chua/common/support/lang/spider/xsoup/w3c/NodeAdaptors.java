package com.chua.common.support.lang.spider.xsoup.w3c;

import com.chua.common.support.jsoup.nodes.Attribute;
import com.chua.common.support.jsoup.nodes.Attributes;
import com.chua.common.support.jsoup.nodes.Element;
import com.chua.common.support.jsoup.select.Elements;
import org.w3c.dom.*;

import java.util.List;

/**
 * @author code4crafer@gmail.com
 */
public class NodeAdaptors {

    public static Node getNode(com.chua.common.support.jsoup.nodes.Node node) {
        if (node == null) {
            return null;
        }
        if (node instanceof Element) {
            return new ElementAdaptor((Element) node);
        }
        return null;
    }

    public static org.w3c.dom.Element getElement(Element element) {
        if (element == null) {
            return null;
        }
        return new ElementAdaptor(element);
    }

    public static Document getDocument(com.chua.common.support.jsoup.nodes.Document document) {
        if (document == null) {
            return null;
        }
        return new DocumentAdaptor(document);
    }

    public static NodeList getNodeList(Elements elements) {
        if (elements == null || elements.size() == 0) {
            return null;
        }
        return new NodeListAdaptor(elements);
    }

    public static NodeList getNodeList(List<com.chua.common.support.jsoup.nodes.Node> elements) {
        if (elements == null || elements.size() == 0) {
            return null;
        }
        return new NodeListAdaptor(elements);
    }

    public static Attr getAttr(Attribute attr, com.chua.common.support.jsoup.nodes.Element element) {
        if (attr == null || element == null) {
            return null;
        }
        return new AttributeAdaptor(attr, element);
    }

    public static NamedNodeMap getNamedNodeMap(List<? extends Node> nodeList) {
        if (nodeList == null || nodeList == null) {
            return null;
        }
        return new NamedNodeMapAdaptor(nodeList);
    }

    public static List<Attr> getAttributes(Attributes attrs, com.chua.common.support.jsoup.nodes.Element element) {
        if (attrs == null || element == null) {
            return null;
        }
        return new AttributesAdaptor(attrs, element).get();
    }
}
