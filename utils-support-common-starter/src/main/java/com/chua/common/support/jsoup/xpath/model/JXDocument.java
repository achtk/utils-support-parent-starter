package com.chua.common.support.jsoup.xpath.model;

import com.chua.common.support.jsoup.Jsoup;
import com.chua.common.support.jsoup.nodes.Document;
import com.chua.common.support.jsoup.select.Elements;
import com.chua.common.support.jsoup.xpath.core.XpathEvaluator;
import com.chua.common.support.jsoup.xpath.exception.NoSuchAxisException;
import com.chua.common.support.jsoup.xpath.exception.NoSuchFunctionException;
import com.chua.common.support.jsoup.xpath.exception.XpathSyntaxErrorException;

import java.util.Collections;
import java.util.List;

/**
 * @author 汪浩淼 [ et.tw@163.com ]
 */
public class JXDocument {
    private Elements elements;
    private XpathEvaluator xpathEva = new XpathEvaluator();

    public JXDocument(Document doc) {
        elements = doc.children();
    }

    public JXDocument(String html) {
        elements = Jsoup.parse(html).children();
    }

    public JXDocument(Elements els) {
        elements = els;
    }

    public List<Object> sel(String xpath)  {
        try {
            return xpathEva.xpathParser(xpath, elements);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
