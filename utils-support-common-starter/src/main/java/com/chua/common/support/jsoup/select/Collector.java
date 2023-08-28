package com.chua.common.support.jsoup.select;

import com.chua.common.support.json.jsonpath.internal.filter.Evaluator;
import com.chua.common.support.jsoup.nodes.Element;
import com.chua.common.support.jsoup.nodes.Node;

import static com.chua.common.support.jsoup.select.NodeFilter.FilterResult.CONTINUE;
import static com.chua.common.support.jsoup.select.NodeFilter.FilterResult.STOP;

/**
 * Collects a list of elements that match the supplied criteria.
 *
 * @author Jonathan Hedley
 */
public class Collector {

    private Collector() {}

    /**
     Build a list of elements, by visiting root and every descendant of root, and testing it against the evaluator.
     @param eval Evaluator to test elements against
     @param root root of tree to descend
     @return list of matches; empty if none
     */
    public static Elements collect (AbstractEvaluator eval, Element root) {
        Elements elements = new Elements();
        NodeTraversor.traverse((node, depth) -> {
            if (node instanceof Element) {
                Element el = (Element) node;
                if (eval.matches(root, el)) {
                    elements.add(el);
                }
            }
        }, root);
        return elements;
    }

    /**
     Finds the first Element that matches the Evaluator that descends from the root, and stops the query once that first
     match is found.
     @param eval Evaluator to test elements against
     @param root root of tree to descend
     @return the first match; {@code null} if none
     */
    public static  Element findFirst(AbstractEvaluator eval, Element root) {
        FirstFinder finder = new FirstFinder(eval);
        return finder.find(root, root);
    }

    static class FirstFinder implements NodeFilter {
        private  Element evalRoot = null;
        private  Element match = null;
        private final AbstractEvaluator eval;

        FirstFinder(AbstractEvaluator eval) {
            this.eval = eval;
        }

         Element find(Element root, Element start) {
            evalRoot = root;
            match = null;
            NodeTraversor.filter(this, start);
            return match;
        }

        @Override
        public FilterResult head(Node node, int depth) {
            if (node instanceof Element) {
                Element el = (Element) node;
                if (eval.matches(evalRoot, el)) {
                    match = el;
                    return STOP;
                }
            }
            return CONTINUE;
        }

        @Override
        public FilterResult tail(Node node, int depth) {
            return CONTINUE;
        }
    }

}
