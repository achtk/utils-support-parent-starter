package com.chua.common.support.lang.spider.xsoup.xevaluator;


import com.chua.common.support.jsoup.nodes.Node;
import com.chua.common.support.jsoup.nodes.TextNode;
import com.chua.common.support.jsoup.select.NodeVisitor;
import com.chua.common.support.utils.StringUtils;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_BLANK;
import static com.chua.common.support.constant.NameConstant.*;

/**
 * @author waincent
 * @since 2018-04-08
 */
public class FormattingVisitor implements NodeVisitor {
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
        } else if (StringUtils.in(name, "p", "h1", "h2", "h3", "h4", "h5", "tr")) {
            append("\n");
        }
    }

    
    @Override
    public void tail(Node node, int depth) {
        String name = node.nodeName();
        if (StringUtils.in(name, "br", "dd", "dt", "p", "h1", "h2", "h3", "h4", "h5")) {
            append("\n");
        } else if (A_TAG.equals(name)) {
            append(String.format(" <%s>", node.absUrl("href")));
        }
    }

    
    private void append(String text) {
        if (text.startsWith("\n")) {
            width = 0; 
        }
        if (SYMBOL_BLANK.equals(text) && (accum.length() == 0 || StringUtils.in(accum.substring(accum.length()
                - 1), " ", "\n"))) {
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