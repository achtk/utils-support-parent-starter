package com.chua.common.support.lang.spider.xsoup.xevaluator;


import com.chua.common.support.jsoup.nodes.Element;
import com.chua.common.support.jsoup.nodes.Node;
import com.chua.common.support.jsoup.nodes.TextNode;
import com.chua.common.support.jsoup.select.NodeTraversor;
import com.chua.common.support.jsoup.select.NodeVisitor;
import com.chua.common.support.utils.StringUtils;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_N;
import static com.chua.common.support.constant.NameConstant.*;
import static com.chua.common.support.lang.spider.xsoup.xevaluator.FormattingVisitor.BR_TAG;
import static com.chua.common.support.lang.spider.xsoup.xevaluator.FormattingVisitor.TEXT_TAG;

/**
 * HTML to plain-text. This example program demonstrates the use of jsoup to convert HTML input to lightly-formatted
 * plain-text. That is divergent from the general goal of jsoup's .text() methods, which is to get clean data from a
 * scrape.
 * <p>
 * Note that this is a fairly simplistic formatter -- for real world use you'll want to embrace and extend.
 * </p>
 * <p>
 * To invoke from the command line, assuming you've downloaded the jsoup jar to your current directory:</p>
 * <p><code>java -cp jsoup.jar org.jsoup.examples.HtmlToPlainText url [selector]</code></p>
 * where <i>url</i> is the URL to fetch, and <i>selector</i> is an optional CSS selector.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
public class HtmlToPlainText {
    private static final String USER_AGENT = "Mozilla/5.0 (jsoup)";
    private static final int TIMEOUT = 5 * 1000;

    /**
     * Format an Element to plain-text
     *
     * @param element the root element to format
     * @return formatted text
     */
    public String getPlainText(Element element) {
        FormattingVisitor formatter = new FormattingVisitor();
        NodeTraversor.traverse(formatter, element); 

        return formatter.toString();
    }

    
    private static class FormattingVisitor implements NodeVisitor {
        private static final int MAX_WIDTH = 80;
        private int width = 0;
        private final StringBuilder accum = new StringBuilder(); 

        
        @Override
        public void head(Node node, int depth) {
            String name = node.nodeName();
            if (node instanceof TextNode) {
                append(((TextNode) node).text()); 
            } else if (LI_TAG.equals(name)) {
                append("\n * ");
            } else if (DT_TAG.equals(name)) {
                append("  ");
            } else if (StringUtils.in(name, TEXT_TAG)) {
                append("\n");
            }
        }

        
        @Override
        public void tail(Node node, int depth) {
            String name = node.nodeName();
            if (StringUtils.in(name, BR_TAG)) {
                append("\n");
            } else if (A_TAG.equals(name)) {
                append(String.format(" <%s>", node.absUrl("href")));
            }
        }

        
        private void append(String text) {
            if (text.startsWith(SYMBOL_N)) {
                width = 0;
            }
            boolean b = " ".equals(text) &&
                    (accum.length() == 0 || StringUtils.in(accum.substring(accum.length() - 1), " ", "\n"));
            if (b) {
                return; 
            }

            if (text.length() + width > MAX_WIDTH) { 
                String[] words = text.split("\\s+");
                for (int i = 0; i < words.length; i++) {
                    String word = words[i];
                    boolean last = i == words.length - 1;
                    if (!last) 
                    {
                        word = word + " ";
                    }
                    if (word.length() + width > MAX_WIDTH) { 
                        accum.append("\n").append(word);
                        width = word.length();
                    } else {
                        accum.append(word);
                        width += word.length();
                    }
                }
            } else { 
                accum.append(text);
                width += text.length();
            }
        }

        @Override
        public String toString() {
            return accum.toString();
        }
    }
}
