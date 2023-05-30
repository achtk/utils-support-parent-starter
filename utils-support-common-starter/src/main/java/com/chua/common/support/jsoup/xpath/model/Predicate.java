package com.chua.common.support.jsoup.xpath.model;

import com.chua.common.support.jsoup.xpath.util.OpEm;
import lombok.Data;

/**
 * xpath语法节点的谓语部分，即要满足的限定条件
 * @author 汪浩淼 [ et.tw@163.com]
 * @author CH
 */
@Data
public class Predicate {

    private OpEm opEm;
    private String left;
    private String right;
    private String value;
}
