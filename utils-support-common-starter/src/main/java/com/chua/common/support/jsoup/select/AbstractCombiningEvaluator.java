package com.chua.common.support.jsoup.select;

import com.chua.common.support.json.jsonpath.internal.filter.Evaluator;
import com.chua.common.support.jsoup.nodes.Element;
import com.chua.common.support.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Base combining (and, or) evaluator.
 * @author Administrator
 */
public abstract class AbstractCombiningEvaluator extends AbstractEvaluator {
    final ArrayList<AbstractEvaluator> evaluators;
    int num = 0;

    AbstractCombiningEvaluator() {
        super();
        evaluators = new ArrayList<>();
    }

    AbstractCombiningEvaluator(Collection<AbstractEvaluator> evaluators) {
        this();
        this.evaluators.addAll(evaluators);
        updateNumEvaluators();
    }

    AbstractEvaluator rightMostEvaluator() {
        return num > 0 ? evaluators.get(num - 1) : null;
    }
    
    void replaceRightMostEvaluator(AbstractEvaluator replacement) {
        evaluators.set(num - 1, replacement);
    }

    void updateNumEvaluators() {
        // used so we don't need to bash on size() for every match test
        num = evaluators.size();
    }

    public static final class And extends AbstractCombiningEvaluator {
        And(Collection<AbstractEvaluator> evaluators) {
            super(evaluators);
        }

        And(AbstractEvaluator... evaluators) {
            this(Arrays.asList(evaluators));
        }

        @Override
        public boolean matches(Element root, Element node) {
            for (int i = num - 1; i >= 0; i--) { // process backwards so that :matchText is evaled earlier, to catch parent query. todo - should redo matchText to virtually expand during match, not pre-match (see SelectorTest#findBetweenSpan)
                AbstractEvaluator s = evaluators.get(i);
                if (!s.matches(root, node)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String toString() {
            return StringUtils.join(evaluators, "");
        }
    }

    public static final class Or extends AbstractCombiningEvaluator {
        /**
         * Create a new Or evaluator. The initial evaluators are ANDed together and used as the first clause of the OR.
         * @param evaluators initial OR clause (these are wrapped into an AND evaluator).
         */
        Or(Collection<AbstractEvaluator> evaluators) {
            super();
            if (num > 1) {
                this.evaluators.add(new And(evaluators));
            } else // 0 or 1
            {
                this.evaluators.addAll(evaluators);
            }
            updateNumEvaluators();
        }

        Or(AbstractEvaluator... evaluators) { this(Arrays.asList(evaluators)); }

        Or() {
            super();
        }

        public void add(AbstractEvaluator e) {
            evaluators.add(e);
            updateNumEvaluators();
        }

        @Override
        public boolean matches(Element root, Element node) {
            for (int i = 0; i < num; i++) {
                AbstractEvaluator s = evaluators.get(i);
                if (s.matches(root, node)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String toString() {
            return StringUtils.join(evaluators, ", ");
        }
    }
}
