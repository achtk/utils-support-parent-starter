package com.chua.common.support.jsoup.xpath.core;


import com.chua.common.support.jsoup.nodes.Element;
import com.chua.common.support.jsoup.select.Elements;
import com.chua.common.support.jsoup.xpath.exception.NoSuchAxisException;
import com.chua.common.support.jsoup.xpath.exception.NoSuchFunctionException;
import com.chua.common.support.jsoup.xpath.model.Node;
import com.chua.common.support.jsoup.xpath.model.Predicate;
import com.chua.common.support.jsoup.xpath.util.CommonUtil;
import com.chua.common.support.jsoup.xpath.util.ScopeEm;
import com.chua.common.support.utils.StringUtils;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import static com.chua.common.support.constant.NumberConstant.NUM_2;

/**
 * @author 汪浩淼 [ et.tw@163.com ]
 * @since 14-3-12
 */
public class XpathEvaluator {

    /**
     * xpath解析器的总入口，同时预处理，如‘|’
     *
     * @param xpath
     * @param root
     * @return
     */
    public List<Object> xpathParser(String xpath, Elements root) throws NoSuchAxisException, NoSuchFunctionException {
        if (xpath.contains("|")) {
            List<Object> rs = new LinkedList<Object>();
            String[] chiXpaths = xpath.split("\\|");
            for (String chiXp : chiXpaths) {
                if (chiXp.length() > 0) {
                    rs.addAll(evaluate(chiXp.trim(), root));
                }
            }
            return rs;
        } else {
            return evaluate(xpath, root);
        }
    }

    /**
     * 获取xpath解析语法树
     *
     * @param xpath
     * @return
     */
    public List<Node> getXpathNodeTree(String xpath) {
        NodeTreeBuilderStateMachine st = new NodeTreeBuilderStateMachine();
        while (st.state != NodeTreeBuilderStateMachine.BuilderState.END) {
            st.state.parser(st, xpath.toCharArray());
        }
        return st.context.xpathTr;
    }

    /**
     * 根据xpath求出结果
     *
     * @param xpath
     * @param root
     * @return
     */
    public List<Object> evaluate(String xpath, Elements root) throws NoSuchAxisException, NoSuchFunctionException {
        List<Object> res = new LinkedList<Object>();
        Elements context = root;
        List<Node> xpathNodes = getXpathNodeTree(xpath);
        for (int i = 0; i < xpathNodes.size(); i++) {
            Node n = xpathNodes.get(i);
            LinkedList<Element> contextTmp = new LinkedList<Element>();
            if (n.getScopeEm() == ScopeEm.RECURSIVE || n.getScopeEm() == ScopeEm.CURREC) {
                if (n.getTagName().startsWith("@")) {
                    for (Element e : context) {
                        //处理上下文自身节点
                        String key = n.getTagName().substring(1);
                        if ("*".equals(key)) {
                            res.add(e.attributes().toString());
                        } else {
                            String value = e.attr(key);
                            if (StringUtils.isNotBlank(value)) {
                                res.add(value);
                            }
                        }
                        //处理上下文子代节点
                        for (Element dep : e.getAllElements()) {
                            if ("*".equals(key)) {
                                res.add(dep.attributes().toString());
                            } else {
                                String value = dep.attr(key);
                                if (StringUtils.isNotBlank(value)) {
                                    res.add(value);
                                }
                            }
                        }
                    }
                } else if (n.getTagName().endsWith("()")) {
                    //递归执行方法默认只支持text()
                    res.add(context.text());
                } else {
                    Elements searchRes = context.select(n.getTagName());
                    for (Element e : searchRes) {
                        Element filterR = filter(e, n);
                        if (filterR != null) {
                            contextTmp.add(filterR);
                        }
                    }
                    context = new Elements(contextTmp);
                }

            } else {
                if (n.getTagName().startsWith("@")) {
                    for (Element e : context) {
                        String key = n.getTagName().substring(1);
                        if ("*".equals(key)) {
                            res.add(e.attributes().toString());
                        } else {
                            String value = e.attr(key);
                            if (StringUtils.isNotBlank(value)) {
                                res.add(value);
                            }
                        }
                    }
                } else if (n.getTagName().endsWith("()")) {
                    res = (List<Object>) callFunc(n.getTagName().substring(0, n.getTagName().length() - 2), context);
                } else {
                    for (Element e : context) {
                        Elements filterScope = e.children();
                        if (StringUtils.isNotBlank(n.getAxis())) {
                            filterScope = getAxisScopeEls(n.getAxis(), e);
                        }
                        for (Element chi : filterScope) {
                            Element fchi = filter(chi, n);
                            if (fchi != null) {
                                contextTmp.add(fchi);
                            }
                        }
                    }
                    context = new Elements(contextTmp);
                    if (i == xpathNodes.size() - 1) {
                        res.addAll(contextTmp);
                    }
                }
            }
        }
        return res;
    }

    /**
     * 元素过滤器
     *
     * @param e
     * @param node
     * @return
     */
    public Element filter(Element e, Node node) throws NoSuchFunctionException, NoSuchAxisException {
        if ("*".equals(node.getTagName()) || node.getTagName().equals(e.nodeName())) {
            if (node.getPredicate() != null) {
                Predicate p = node.getPredicate();
                if (p.getOpEm() == null) {
                    if (p.getValue().matches("\\d+") && getElIndex(e) == Integer.parseInt(p.getValue())) {
                        return e;
                    } else if (p.getValue().endsWith("()") && (Boolean) callFilterFunc(p.getValue().substring(0, p.getValue().length() - NUM_2), e)) {
                        return e;
                    }
                    //todo p.value ~= contains(./@href,'renren.com')
                } else {
                    if (p.getLeft().matches("[^/]+\\(\\)")) {
                        Object filterRes = p.getOpEm().excute(callFilterFunc(p.getLeft().substring(0, p.getLeft().length() - 2), e).toString(), p.getRight());
                        if (filterRes instanceof Boolean && (Boolean) filterRes) {
                            return e;
                        } else if (filterRes instanceof Integer && e.siblingIndex() == Integer.parseInt(filterRes.toString())) {
                            return e;
                        }
                    } else if (p.getLeft().startsWith("@")) {
                        String lValue = e.attr(p.getLeft().substring(1));
                        Object filterRes = p.getOpEm().excute(lValue, p.getRight());
                        if ((Boolean) filterRes) {
                            return e;
                        }
                    } else {
                        // 操作符左边不是函数、属性默认就是xpath表达式了
                        List<Element> eltmp = new LinkedList<Element>();
                        eltmp.add(e);
                        List<Object> rstmp = evaluate(p.getLeft(), new Elements(eltmp));
                        if ((Boolean) p.getOpEm().excute(StringUtils.join(rstmp, ""), p.getRight())) {
                            return e;
                        }
                    }
                }
            } else {
                return e;
            }
        }
        return null;
    }

    /**
     * 调用轴选择器
     *
     * @param axis
     * @param e
     * @return
     * @throws NoSuchAxisException
     */
    public Elements getAxisScopeEls(String axis, Element e) throws NoSuchAxisException {
        try {
            String functionName = CommonUtil.getMethodNameFromStr(axis);
            Method axisSelector = AxisSelector.class.getMethod(functionName, Element.class);
            return (Elements) axisSelector.invoke(SingletonProducer.getInstance().getAxisSelector(), e);
        } catch (NoSuchMethodException e1) {
            throw new NoSuchAxisException("this axis is not supported,plase use other instead of '" + axis + "'");
        } catch (Exception e2) {
            throw new NoSuchAxisException(e2.getMessage());
        }
    }

    /**
     * 调用xpath主干上的函数
     *
     * @param funcname
     * @param context
     * @return
     * @throws NoSuchFunctionException
     */
    public Object callFunc(String funcname, Elements context) throws NoSuchFunctionException {
        try {
            Method function = Functions.class.getMethod(funcname, Elements.class);
            return function.invoke(SingletonProducer.getInstance().getFunctions(), context);
        } catch (NoSuchMethodException e) {
            throw new NoSuchFunctionException("This function is not supported");
        } catch (Exception e1) {
            throw new NoSuchFunctionException(e1.getMessage());
        }
    }

    /**
     * 调用谓语中函数
     *
     * @param funcname fn
     * @param el element
     * @return value
     * @throws NoSuchFunctionException ex
     */
    public Object callFilterFunc(String funcname, Element el) throws NoSuchFunctionException {
        try {
            Method function = Functions.class.getMethod(funcname, Element.class);
            return function.invoke(SingletonProducer.getInstance().getFunctions(), el);
        } catch (NoSuchMethodException e) {
            throw new NoSuchFunctionException("This function is not supported");
        } catch (Exception et) {
            throw new NoSuchFunctionException(et.getMessage());
        }
    }

    public int getElIndex(Element e) {
        if (e != null) {
            return CommonUtil.getElIndexInSameTags(e);
        }
        return 1;
    }

}
