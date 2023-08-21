package com.chua.common.support.lang.spider.xsoup.xevaluator;


import com.chua.common.support.jsoup.nodes.Node;
import com.chua.common.support.jsoup.nodes.TextNode;
import com.chua.common.support.jsoup.select.NodeVisitor;
import com.chua.common.support.utils.StringUtils;

/**
 * @author waincent
 * @since 2018-04-08
 */
public class FormattingVisitor implements NodeVisitor {
    private static final int maxWidth = 80;
    private int width = 0;
    private StringBuilder accum = new StringBuilder(); // holds the accumulated text

    // hit when the node is first seen
    @Override
    public void head(Node node, int depth) {
        String name = node.nodeName();
        if (node instanceof TextNode) {
            append(((TextNode) node).text()); // TextNodes carry all user-readable text in the DOM.
        } else if (name.equals("li")) {
            append("\n * ");
        } else if (name.equals("dt")) {
            append("  ");
        } else if (StringUtils.in(name, "p", "h1", "h2", "h3", "h4", "h5", "tr")) append("\n");
    }

    // hit when all of the node's children (if any) have been visited
    @Override
    public void tail(Node node, int depth) {
        String name = node.nodeName();
        if (StringUtils.in(name, "br", "dd", "dt", "p", "h1", "h2", "h3", "h4", "h5")) {
            append("\n");
        } else if (name.equals("a")) append(String.format(" <%s>", node.absUrl("href")));
    }

    // appends text to the string builder with a simple word wrap method
    private void append(String text) {
        if (text.startsWith("\n")) {
            width = 0; // reset counter if starts with a newline. only from formats above, not in natural text
        }
        if (text.equals(" ") && (accum.length() == 0 || StringUtils.in(accum.substring(accum.length()
                - 1), " ", "\n"))) {
            return; // don't accumulate long runs of empty spaces
        }

        if (text.length() + width > maxWidth) { // won't fit, needs to wrap
            String words[] = text.split("\\s+");
            for (int i = 0; i < words.length; i++) {
                String word = words[i];
                boolean last = i == words.length - 1;
                if (!last) // insert a space if not the last word
                {
                    word = word + " ";
                }
                if (word.length() + width > maxWidth) { // wrap and reset counter
                    accum.append("\n").append(word);
                    width = word.length();
                } else {
                    accum.append(word);
                    width += word.length();
                }
            }
        } else { // fits as is, without need to wrap text
            accum.append(text);
            width += text.length();
        }
    }

    @Override
    public String toString() {
        return accum.toString();
    }
}