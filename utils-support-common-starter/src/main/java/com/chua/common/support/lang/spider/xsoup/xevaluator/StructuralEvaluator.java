package com.chua.common.support.lang.spider.xsoup.xevaluator;


import com.chua.common.support.jsoup.nodes.Element;
import com.chua.common.support.jsoup.select.AbstractEvaluator;

/**
 * Base structural evaluator.
 * Copy from {@link StructuralEvaluator} because it is package visible
 *
 * @see StructuralEvaluator
 */
abstract class StructuralEvaluator extends AbstractEvaluator {
    AbstractEvaluator evaluator;

    static class Root extends AbstractEvaluator {
        public boolean matches(Element root, Element element) {
            return root == element;
        }

        public String toString() {
            return ":root";
        }
    }

    static class Has extends StructuralEvaluator {
        public Has(AbstractEvaluator evaluator) {
            this.evaluator = evaluator;
        }

        public boolean matches(Element root, Element element) {
            for (Element e : element.getAllElements()) {
                if (e != element && evaluator.matches(root, e)) return true;
            }
            return false;
        }

        public String toString() {
            return String.format(":has(%s)", evaluator);
        }
    }

    static class Not extends StructuralEvaluator {
        public Not(AbstractEvaluator evaluator) {
            this.evaluator = evaluator;
        }

        public boolean matches(Element root, Element node) {
            return !evaluator.matches(root, node);
        }

        public String toString() {
            return String.format(":not%s", evaluator);
        }
    }

    static class Parent extends StructuralEvaluator {
        public Parent(AbstractEvaluator evaluator) {
            this.evaluator = evaluator;
        }

        public boolean matches(Element root, Element element) {
            Element parent = element.parent();
            while (parent != null) {
                if (evaluator.matches(root, parent)) return true;
                parent = parent.parent();
            }
            return false;
        }

        public String toString() {
            return String.format(":parent%s", evaluator);
        }
    }

    static class ImmediateParent extends StructuralEvaluator {
        public ImmediateParent(AbstractEvaluator evaluator) {
            this.evaluator = evaluator;
        }

        public boolean matches(Element root, Element element) {
            Element parent = element.parent();
            return parent != null && evaluator.matches(root, parent);
        }

        public String toString() {
            return String.format(":ImmediateParent%s", evaluator);
        }
    }

    static class PreviousSibling extends StructuralEvaluator {
        public PreviousSibling(AbstractEvaluator evaluator) {
            this.evaluator = evaluator;
        }

        public boolean matches(Element root, Element element) {
            if (root == element) return false;

            Element prev = element.previousElementSibling();

            while (prev != null) {
                if (evaluator.matches(root, prev)) return true;

                prev = prev.previousElementSibling();
            }
            return false;
        }

        public String toString() {
            return String.format(":prev*%s", evaluator);
        }
    }

    static class ImmediatePreviousSibling extends StructuralEvaluator {
        public ImmediatePreviousSibling(AbstractEvaluator evaluator) {
            this.evaluator = evaluator;
        }

        public boolean matches(Element root, Element element) {
            if (root == element) return false;

            Element prev = element.previousElementSibling();
            return prev != null && evaluator.matches(root, prev);
        }

        public String toString() {
            return String.format(":prev%s", evaluator);
        }
    }
}
