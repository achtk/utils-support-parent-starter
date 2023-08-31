package com.chua.common.support.lang.spider.xsoup.xevaluator;

import com.chua.common.support.jsoup.nodes.Element;
import com.chua.common.support.jsoup.nodes.Node;
import com.chua.common.support.jsoup.nodes.TextNode;
import com.chua.common.support.lang.spider.xsoup.Xsoup;
import com.chua.common.support.utils.Preconditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Operate on element to get XPath result.
 *
 * @author code4crafter@gmail.com
 */
public abstract class AbstractElementOperator {

    /**
     * 操作
     * @param element 元素
     * @return 结果
     */
    public abstract String operate(Element element);

    public static class AttributeGetter extends AbstractElementOperator {

        private final String attribute;

        public AttributeGetter(String attribute) {
            this.attribute = attribute;
        }

        @Override
        public String operate(Element element) {
            return element.attr(attribute);
        }

        @Override
        public String toString() {
            return "@" + attribute;
        }
    }

    public static class AllText extends AbstractElementOperator {

        @Override
        public String operate(Element element) {
            return element.text();
        }

        @Override
        public String toString() {
            return "allText()";
        }
    }

    public static class Html extends AbstractElementOperator {

        @Override
        public String operate(Element element) {
            return element.html();
        }

        @Override
        public String toString() {
            return "html()";
        }
    }

    public static class OuterHtml extends AbstractElementOperator {

        @Override
        public String operate(Element element) {
            return element.outerHtml();
        }

        @Override
        public String toString() {
            return "outerHtml()";
        }
    }

    public static class TidyText extends AbstractElementOperator {

        @Override
        public String operate(Element element) {
            //FormattingVisitor formatter = new FormattingVisitor();
            //NodeTraversor.traverse(formatter, element);
            //return formatter.toString();
            //return new HtmlToPlainText().getPlainText(element);
            return Xsoup.htmlToPlainText(element);
        }

        @Override
        public String toString() {
            return "tidyText()";
        }
    }

    public static class GroupedText extends AbstractElementOperator {

        private int group;

        public GroupedText(int group) {
            this.group = group;
        }

        @Override
        public String operate(Element element) {
            int index = 0;
            StringBuilder accum = new StringBuilder();
            for (Node node : element.childNodes()) {
                if (node instanceof TextNode) {
                    TextNode textNode = (TextNode) node;
                    if (group == 0) {
                        accum.append(textNode.text());
                    } else if (++index == group) {
                        return textNode.text();
                    }
                }
            }
            return accum.toString();
        }

        @Override
        public String toString() {
            return String.format("text(%d)", group);
        }
    }

    /**
     * usage:
     * <br>
     * regex('.*')
     * regex(@attr,'.*')
     * regex(@attr,'.*',group)
     */
    public static class Regex extends AbstractElementOperator {

        private Pattern pattern;

        private String attribute;

        private int group;

        public Regex(String expr) {
            this.pattern = Pattern.compile(expr);
        }

        public Regex(String expr, String attribute) {
            this.attribute = attribute;
            this.pattern = Pattern.compile(expr);
        }

        public Regex(String expr, String attribute, int group) {
            this.attribute = attribute;
            this.pattern = Pattern.compile(expr);
            this.group = group;
        }

        @Override
        public String operate(Element element) {
            Matcher matcher = pattern.matcher(getSource(element));
            if (matcher.find()) {
                return matcher.group(group);
            }
            return null;
        }

        protected String getSource(Element element) {
            if (attribute == null) {
                return element.outerHtml();
            } else {
                String attr = element.attr(attribute);
                Preconditions.notNull(attr, "Attribute " + attribute + " of " + element + " is not exist!");
                return attr;
            }
        }

        @Override
        public String toString() {
            return String.format("regex(%s%s%s)",
                    attribute != null ? "@" + attribute + "," : "", pattern.toString(),
                    group != 0 ? "," + group : "");
        }
    }
}
