package com.chua.common.support.jsoup.xpath.core;


import com.chua.common.support.jsoup.xpath.model.Node;

import java.util.LinkedList;

/**
 * @author 汪浩淼 [ et.tw@163.com ]
 * @author CH
 * @since 14-3-10
 */
public class XContext {
    public LinkedList<Node> xpathTr;
    public XContext(){
        xpathTr = new LinkedList<Node>();
    }
}
