package com.chua.common.support.jsoup.xpath.model;

import com.chua.common.support.jsoup.xpath.util.ScopeEm;
import lombok.Data;

/**
 * xpath语法链的一个基本节点
 * @author 汪浩淼 [ et.tw@163.com]
 * @author CH
 */
@Data
public class Node {
    private ScopeEm scopeEm;
    private String axis;
    private String tagName;
    private Predicate predicate;

}
