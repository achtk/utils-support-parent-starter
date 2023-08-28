package com.chua.common.support.lang.spider.xsoup.xevaluator;

import com.chua.common.support.jsoup.nodes.Element;
import com.chua.common.support.jsoup.select.AbstractEvaluator;
import com.chua.common.support.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * Base combining (and, or) evaluator.
 * <p>
 * Copy from {@link AbstractCombiningEvaluator} because it is package visible
 *
 * @see AbstractCombiningEvaluator
 */
abstract class AbstractCombiningEvaluator extends AbstractEvaluator {
    final List<AbstractEvaluator> evaluators;

    AbstractCombiningEvaluator() {
        super();
        evaluators = new ArrayList<AbstractEvaluator>();
    }

    AbstractCombiningEvaluator(Collection<AbstractEvaluator> evaluators) {
        this();
        this.evaluators.addAll(evaluators);
    }

    AbstractEvaluator rightMostEvaluator() {
        return evaluators.size() > 0 ? evaluators.get(evaluators.size() - 1) : null;
    }

    void replaceRightMostEvaluator(AbstractEvaluator replacement) {
        evaluators.set(evaluators.size() - 1, replacement);
    }

    static final class And extends AbstractCombiningEvaluator {
        And(Collection<AbstractEvaluator> evaluators) {
            super(evaluators);
        }

        And(AbstractEvaluator... evaluators) {
            this(Arrays.asList(evaluators));
        }

        @Override
        public boolean matches(Element root, Element node) {
            for (int i = 0; i < evaluators.size(); i++) {
                AbstractEvaluator s = evaluators.get(i);
                if (!s.matches(root, node)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String toString() {
            return StringUtils.join(evaluators, " ");
        }
    }

    static final class Or extends AbstractCombiningEvaluator {

        Or(Collection<AbstractEvaluator> evaluators) {
            super();
            this.evaluators.addAll(evaluators);
        }

        Or(AbstractEvaluator... evaluators) {
            this(Arrays.asList(evaluators));
        }

        Or() {
            super();
        }

        public void add(AbstractEvaluator e) {
            evaluators.add(e);
        }

        @Override
        public boolean matches(Element root, Element node) {
            for (int i = 0; i < evaluators.size(); i++) {
                AbstractEvaluator s = evaluators.get(i);
                if (s.matches(root, node)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String toString() {
            return String.format(":or%s", evaluators);
        }
    }
}
