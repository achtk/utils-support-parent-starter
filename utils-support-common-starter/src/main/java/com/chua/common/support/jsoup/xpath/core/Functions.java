package com.chua.common.support.jsoup.xpath.core;

import com.chua.common.support.constant.RegexConstant;
import com.chua.common.support.jsoup.nodes.Element;
import com.chua.common.support.jsoup.select.Elements;
import com.chua.common.support.jsoup.xpath.util.CommonUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * xpath解析器的支持的全部函数集合，如需扩展按形式添加即可
 *
 * @author 汪浩淼 [ et.tw@163.com ]
 * @author CH
 * Date: 14-3-15
 */
public class Functions {
    /**
     * 只获取节点自身的子文本
     *
     * @param context
     * @return
     */
    public List<Object> text(Elements context) {
        List<Object> res = new LinkedList<Object>();
        if (context != null && context.size() > 0) {
            for (Element e : context) {
                res.add(e.ownText());
            }
        }
        return res;
    }

    /**
     * 递归获取节点内全部的纯文本
     *
     * @param context
     * @return
     */
    public List<Object> allText(Elements context) {
        List<Object> res = new LinkedList<>();
        if (context != null && context.size() > 0) {
            for (Element e : context) {
                res.add(e.text());
            }
        }
        return res;
    }

    /**
     * 获取全部节点的内部的html
     *
     * @param context
     * @return
     */
    public List<Object> html(Elements context) {
        List<Object> res = new LinkedList<Object>();
        if (context != null && context.size() > 0) {
            for (Element e : context) {
                res.add(e.html());
            }
        }
        return res;
    }

    /**
     * 获取全部节点的 包含节点本身在内的全部html
     *
     * @param context
     * @return
     */
    public List<Object> outerHtml(Elements context) {
        List<Object> res = new LinkedList<Object>();
        if (context != null && context.size() > 0) {
            for (Element e : context) {
                res.add(e.outerHtml());
            }
        }
        return res;
    }

    /**
     * 获取全部节点
     *
     * @param context
     * @return
     */
    public List<Object> node(Elements context) {
        List<Object> res = new LinkedList<Object>();
        if (context != null && context.size() > 0) {
            for (Element e : context) {
                res.addAll(e.getAllElements());
            }
        }
        return res;
    }

    /**
     * 抽取节点自有文本中全部数字
     *
     * @param context
     * @return
     */
    public List<Object> num(Elements context) {
        List<Object> res = new LinkedList<Object>();
        if (context != null) {
            Pattern pattern = RegexConstant.NUMBERS;
            for (Element e : context) {
                Matcher matcher = pattern.matcher(e.ownText());
                if (matcher.find()) {
                    res.add(matcher.group());
                }
            }
        }
        return res;
    }

    /**
     * =====================
     * 下面是用于过滤器的函数
     */

    /**
     * 获取元素自己的子文本
     *
     * @param e
     * @return
     */
    public String text(Element e) {
        return e.ownText();
    }

    /**
     * 获取元素下面的全部文本
     *
     * @param e
     * @return
     */
    public String allText(Element e) {
        return e.text();
    }

    /**
     * 判断一个元素是不是最后一个同名同胞中的
     *
     * @param e
     * @return
     */
    public boolean last(Element e) {
        return CommonUtil.getElIndexInSameTags(e) == CommonUtil.sameTagElNums(e);
    }

    /**
     * 判断一个元素是不是同名同胞中的第一个
     *
     * @param e
     * @return
     */
    public boolean first(Element e) {
        return CommonUtil.getElIndexInSameTags(e) == 1;
    }

    /**
     * 返回一个元素在同名兄弟节点中的位置
     *
     * @param e
     * @return
     */
    public int position(Element e) {
        return CommonUtil.getElIndexInSameTags(e);
    }

    /**
     * 判断是否包含
     *
     * @param left
     * @param right
     * @return
     */
    public boolean contains(String left, String right) {
        return left.contains(right);
    }

}
