package com.chua.common.support.jsoup.select;

import com.chua.common.support.jsoup.helper.Validate;
import com.chua.common.support.jsoup.nodes.Element;
import com.chua.common.support.jsoup.nodes.Node;
import com.chua.common.support.jsoup.select.NodeFilter.FilterResult;

/**
 * Depth-first node traversor. Use to iterate through all nodes under and including the specified root node.
 * <p>
 * This implementation does not use recursion, so a deep DOM does not risk blowing the stack.
 * </p>
 *
 * @author Administrator
 */
public class NodeTraversor {
    /**
     * Start a depth-first traverse of the root and all of its descendants.
     * @param visitor Node visitor.
     * @param root the root node point to traverse.
     */
    public static void traverse(NodeVisitor visitor, Node root) {
        Validate.notNull(visitor);
        Validate.notNull(root);
        Node node = root;
        int depth = 0;
        
        while (node != null) {
            Node parent = node.parentNode();
            int origSize = parent != null ? parent.childNodeSize() : 0;
            Node next = node.nextSibling();

            visitor.head(node, depth);
            if (parent != null && !node.hasParent()) {
                if (origSize == parent.childNodeSize()) {
                    node = parent.childNode(node.siblingIndex());
                } else {
                    node = next;
                    if (node == null) {
                        node = parent;
                        depth--;
                    }
                    continue;
                }
            }

            if (node.childNodeSize() > 0) {
                node = node.childNode(0);
                depth++;
            } else {
                while (true) {
                    assert node != null;
                    if (!(node.nextSibling() == null && depth > 0)) {
                        break;
                    }
                    visitor.tail(node, depth);
                    node = node.parentNode();
                    depth--;
                }
                visitor.tail(node, depth);
                if (node == root) {
                    break;
                }
                node = node.nextSibling();
            }
        }
    }

    /**
     * Start a depth-first traverse of all elements.
     * @param visitor Node visitor.
     * @param elements Elements to filter.
     */
    public static void traverse(NodeVisitor visitor, Elements elements) {
        Validate.notNull(visitor);
        Validate.notNull(elements);
        for (Element el : elements) {
            traverse(visitor, el);
        }
    }

    /**
     * Start a depth-first filtering of the root and all of its descendants.
     * @param filter Node visitor.
     * @param root the root node point to traverse.
     * @return The filter result of the root node, or {@link FilterResult#STOP}.
     */
    public static FilterResult filter(NodeFilter filter, Node root) {
        Node node = root;
        int depth = 0;

        while (node != null) {
            FilterResult result = filter.head(node, depth);
            if (result == FilterResult.STOP) {
                return result;
            }
            if (result == FilterResult.CONTINUE && node.childNodeSize() > 0) {
                node = node.childNode(0);
                ++depth;
                continue;
            }
            while (true) {
                assert node != null;
                if (!(node.nextSibling() == null && depth > 0)) {
                    break;
                }
                if (result == FilterResult.CONTINUE || result == FilterResult.SKIP_CHILDREN) {
                    result = filter.tail(node, depth);
                    if (result == FilterResult.STOP) {
                        return result;
                    }
                }
                Node prev = node;
                node = node.parentNode();
                depth--;
                if (result == FilterResult.REMOVE) {
                    prev.remove();
                }
                result = FilterResult.CONTINUE;
            }
            if (result == FilterResult.CONTINUE || result == FilterResult.SKIP_CHILDREN) {
                result = filter.tail(node, depth);
                if (result == FilterResult.STOP) {
                    return result;
                }
            }
            if (node == root) {
                return result;
            }
            Node prev = node;
            node = node.nextSibling();
            if (result == FilterResult.REMOVE) {
                prev.remove();
            }
        }
        return FilterResult.CONTINUE;
    }

    /**
     * Start a depth-first filtering of all elements.
     * @param filter Node filter.
     * @param elements Elements to filter.
     */
    public static void filter(NodeFilter filter, Elements elements) {
        Validate.notNull(filter);
        Validate.notNull(elements);
        for (Element el : elements) {
            if (filter(filter, el) == FilterResult.STOP) {
                break;
            }
        }
    }
}
