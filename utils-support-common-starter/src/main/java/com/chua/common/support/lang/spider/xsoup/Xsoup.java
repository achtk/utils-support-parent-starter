package com.chua.common.support.lang.spider.xsoup;


import com.chua.common.support.jsoup.Jsoup;
import com.chua.common.support.jsoup.nodes.Document;
import com.chua.common.support.jsoup.nodes.Element;
import com.chua.common.support.jsoup.select.NodeTraversor;
import com.chua.common.support.lang.spider.xsoup.w3c.NodeAdaptors;
import com.chua.common.support.lang.spider.xsoup.xevaluator.FormattingVisitor;
import com.chua.common.support.lang.spider.xsoup.xevaluator.PathParser;

/**
 * @author code4crafter@gmail.com
 */
public class Xsoup {

    /*-------------     XEvaluator         --------------- */

    public static Elements select(Element element, String xpathStr) {
        return PathParser.parse(xpathStr).evaluate(element);
    }

    public static Elements select(String html, String xpathStr) {
        return PathParser.parse(xpathStr).evaluate(Jsoup.parse(html));
    }

    public static PathEvaluator compile(String xpathStr) {
        return PathParser.parse(xpathStr);
    }

    /*-------------     W3cAdaptor         --------------- */

    public static org.w3c.dom.Element convertElement(Element element) {
        return NodeAdaptors.getElement(element);
    }

    public static org.w3c.dom.Document convertDocument(Document document) {
        return NodeAdaptors.getDocument(document);
    }

    public static String htmlToPlainText(Element element) {
        FormattingVisitor formatter = new FormattingVisitor();
        NodeTraversor.traverse(formatter, element);
        return formatter.toString();
    }
}
