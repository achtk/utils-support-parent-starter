package com.chua.common.support.jsoup.xpath.util;

/**
 * 筛选作用域
 * @author 汪浩淼 [ et.tw@163.com ]
 * @since 14-3-7
 */
public enum ScopeEm {
    /**
     * 默认只在子代中筛选,有轴时由轴定义筛选域
     */
    INCHILREN("/"),
    /**
     * 向下递归查找
     */
    RECURSIVE("//"),
    /**
     * 当前节点下
     */
    CUR("./"),
    /**
     * 当前节点向下递归
     */
    CURREC(".//");
    private String val;
    private ScopeEm(String type){
        this.val = type;
    }
    public String val() {
        return this.val;
    }
}
