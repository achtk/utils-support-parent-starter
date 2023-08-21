package com.chua.common.support.lang.spider.xsoup;


import com.chua.common.support.jsoup.Jsoup;
import com.chua.common.support.jsoup.nodes.Document;
import com.chua.common.support.jsoup.nodes.Element;
import com.chua.common.support.jsoup.select.NodeTraversor;
import com.chua.common.support.lang.spider.xsoup.w3c.NodeAdaptors;
import com.chua.common.support.lang.spider.xsoup.xevaluator.FormattingVisitor;
import com.chua.common.support.lang.spider.xsoup.xevaluator.XPathParser;

/**
 * @author code4crafter@gmail.com
 */
public class Xsoup {

    /*-------------     XEvaluator         --------------- */

    public static XElements select(Element element, String xpathStr) {
        return XPathParser.parse(xpathStr).evaluate(element);
    }

    public static XElements select(String html, String xpathStr) {
        return XPathParser.parse(xpathStr).evaluate(Jsoup.parse(html));
    }

    public static XPathEvaluator compile(String xpathStr) {
        return XPathParser.parse(xpathStr);
    }

    /*-------------     W3cAdaptor         --------------- */

    public static org.w3c.dom.Element convertElement(Element element) {
        return NodeAdaptors.getElement(element);
    }

    public static org.w3c.dom.Document convertDocument(Document document) {
        return NodeAdaptors.getDocument(document);
    }

    public static String HtmlToPlainText(Element element) {
        FormattingVisitor formatter = new FormattingVisitor();
        NodeTraversor.traverse(formatter, element);
        return formatter.toString();
    }
}
