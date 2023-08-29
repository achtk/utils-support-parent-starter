package com.chua.common.support.lang.spider.xsoup.xevaluator;

import com.chua.common.support.jsoup.select.AbstractEvaluator;
import com.chua.common.support.jsoup.select.Selector;
import com.chua.common.support.lang.spider.xsoup.PathEvaluator;
import com.chua.common.support.lang.spider.xsoup.SoupTokenQueue;
import com.chua.common.support.utils.Preconditions;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.chua.common.support.constant.NumberConstant.NUM_2;
import static com.chua.common.support.constant.NumberConstant.NUM_3;

/**
 * Parser of XPath.
 *
 * @author code4crafter@gmail.com
 */
public class PathParser {

    private static final String[] COMBINATORS = new String[]{"//", "/", "|"};

    private static final String[] ESCAPED_QUOTES = new String[]{"\\\"", "\\'"};

    private static final String[] QUOTES = new String[]{"\"", "'"};

    private static final String[] HIERARCHY_COMBINATORS = new String[]{"//", "/", "|"};

    private static final Map<String, FunctionEvaluator> FUNCTION_MAPPING = new HashMap<String, FunctionEvaluator>();
    private static final String OR_COMBINATOR = "|";

    static {
        FUNCTION_MAPPING.put("contains", new FunctionEvaluator() {
            @Override
            public AbstractEvaluator call(String... param) {
                Preconditions.isTrue(param.length == 2, String.format("Error argument of %s", "contains"));
                return new AbstractEvaluator.AttributeWithValueContaining(param[0], param[1]);
            }
        });
        FUNCTION_MAPPING.put("starts-with", new FunctionEvaluator() {
            @Override
            public AbstractEvaluator call(String... param) {
                Preconditions.isTrue(param.length == 2, String.format("Error argument of %s", "starts-with"));
                return new AbstractEvaluator.AttributeWithValueStarting(param[0], param[1]);
            }
        });
        FUNCTION_MAPPING.put("ends-with", new FunctionEvaluator() {
            @Override
            public AbstractEvaluator call(String... param) {
                Preconditions.isTrue(param.length == 2, String.format("Error argument of %s", "ends-with"));
                return new AbstractEvaluator.AttributeWithValueEnding(param[0], param[1]);
            }
        });
    }

    private SoupTokenQueue tq;
    private String query;
    private List<AbstractEvaluator> evals = new ArrayList<>();
    private ElementOperator elementOperator;
    private boolean noEvalAllow = false;
    private final Pattern patternForText = Pattern.compile("text\\((\\d*)\\)");

    public PathParser(String xpathStr) {
        this.query = xpathStr;
        this.tq = new SoupTokenQueue(xpathStr);
    }

    public static PathEvaluator parse(String xpathStr) {
        PathParser xPathParser = new PathParser(xpathStr);
        return xPathParser.parse();
    }

    public PathEvaluator parse() {

        while (!tq.isEmpty()) {
            Preconditions.isFalse(noEvalAllow, "XPath error! No operator allowed after attribute or function!" + tq);
            if (tq.matchChomp(OR_COMBINATOR)) {
                tq.consumeWhitespace();
                return combineXPathEvaluator(tq.remainder());
            } else if (tq.matchesAny(HIERARCHY_COMBINATORS)) {
                combinator(tq.consumeAny(HIERARCHY_COMBINATORS));
            } else {
                findElements();
            }
            tq.consumeWhitespace();
        }
        return collectXPathEvaluator();
    }

    private PathEvaluator combineXPathEvaluator(String subQuery) {
        PathEvaluator xPathEvaluator = collectXPathEvaluator();
        return new CombingXPathEvaluator(xPathEvaluator, parse(subQuery));
    }

    private PathEvaluator collectXPathEvaluator() {
        if (noEvalAllow) {
            return new DefaultXPathEvaluator(null, elementOperator);
        }

        if (evals.size() == 1) {
            return new DefaultXPathEvaluator(evals.get(0), elementOperator);
        }

        return new DefaultXPathEvaluator(new AbstractCombiningEvaluator.And(evals), elementOperator);
    }

    private void combinator(String combinator) {
        AbstractEvaluator currentEval;
        if (evals.size() == 0) {
            currentEval = new StructuralEvaluator.Root();
        } else if (evals.size() == 1) {
            currentEval = evals.get(0);
        } else {
            currentEval = new AbstractCombiningEvaluator.And(evals);
        }
        evals.clear();
        String subQuery = consumeSubQuery();
        PathEvaluator tmpEval = parse(subQuery);
        if (!(tmpEval instanceof DefaultXPathEvaluator)) {
            throw new IllegalArgumentException(String.format("Error XPath in %s", subQuery));
        }
        DefaultXPathEvaluator newEval = (DefaultXPathEvaluator) tmpEval;
        if (newEval.getElementOperator() != null) {
            elementOperator = newEval.getElementOperator();
        }
        // attribute expr does not return Evaluator
        if (newEval.getEvaluator() != null) {
            if ("//".equals(combinator)) {
                currentEval =
                        new AbstractCombiningEvaluator.And(newEval.getEvaluator(), new StructuralEvaluator.Parent(currentEval));
            } else if ("/".equals(combinator)) {
                currentEval =
                        new AbstractCombiningEvaluator.And(newEval.getEvaluator(), new StructuralEvaluator.ImmediateParent(currentEval));
            }
        }
        evals.add(currentEval);
    }

    private String consumeSubQuery() {
        StringBuilder sq = new StringBuilder();
        while (!tq.isEmpty()) {
            tq.consumeWhitespace();
            if (tq.matches("(")) {
                sq.append("(").append(tq.chompBalanced('(', ')')).append(")");
            } else if (tq.matches("[")) {
                sq.append("[").append(tq.chompBalanced('[', ']')).append("]");
            } else if (tq.matchesAny(ESCAPED_QUOTES)) {
                sq.append(tq.consumeAny(ESCAPED_QUOTES));
            } else if (tq.matchesAny(QUOTES)) {
                sq.append(tq.chompBalancedQuotes());
            } else if (tq.matchesAny(COMBINATORS)) {
                break;
            } else if (!tq.isEmpty()) {
                sq.append(tq.consume());
            }
        }
        return sq.toString();
    }

    private void findElements() {
        if (tq.matches("@")) {
            consumeAttribute();
        } else if (tq.matches("*")) {
            allElements();
        } else if (tq.matchesRegex("\\w+\\(.*\\).*")) {
            consumeOperatorFunction();
        } else if (tq.matchesWord()) {
            byTag();
        } else if (tq.matchesRegex("\\[\\d+\\]")) {
            byNth();
        } else if (tq.matches("[")) {
            evals.add(consumePredicates(tq.chompBalanced('[', ']')));
        } else {
            // unhandled
            throw new Selector.SelectorParseException("Could not parse query '%s': unexpected token at '%s'", query, tq.remainder());
        }
    }

    private AbstractEvaluator consumePredicates(String queue) {
        SoupTokenQueue predicatesQueue = new SoupTokenQueue(queue);
        EvaluatorStack evaluatorStack = new EvaluatorStack();
        Operation currentOperation = null;
        predicatesQueue.consumeWhitespace();
        while (!predicatesQueue.isEmpty()) {
            if (predicatesQueue.matchChomp("and")) {
                currentOperation = Operation.AND;
            } else if (predicatesQueue.matchChomp("or")) {
                currentOperation = Operation.OR;
            } else {
                if (currentOperation == null && evaluatorStack.size() > 0) {
                    throw new IllegalArgumentException(String.format("Need AND/OR between two predicate! %s", predicatesQueue.remainder()));
                }
                AbstractEvaluator evaluator;
                if (predicatesQueue.matches("(")) {
                    evaluator = consumePredicates(predicatesQueue.chompBalanced('(', ')'));
                } else if (predicatesQueue.matches("@")) {
                    evaluator = byAttribute(predicatesQueue);
                } else if (predicatesQueue.matchesRegex("\\w+.*")) {
                    evaluator = byFunction(predicatesQueue);
                } else {
                    throw new Selector.SelectorParseException("Could not parse query '%s': unexpected token at '%s'", query, predicatesQueue.remainder());
                }
                evaluatorStack.calc(evaluator, currentOperation);
                //consume operator
                currentOperation = null;
            }
            predicatesQueue.consumeWhitespace();
        }
        evaluatorStack.mergeOr();
        return evaluatorStack.peek();
    }

    private AbstractEvaluator byFunction(SoupTokenQueue predicatesQueue) {
        for (Map.Entry<String, FunctionEvaluator> entry : FUNCTION_MAPPING.entrySet()) {
            if (predicatesQueue.matchChomp(entry.getKey())) {
                String paramString = predicatesQueue.chompBalanced('(', ')');
                List<String> params = SoupTokenQueue.trimQuotes(SoupTokenQueue.parseFuncionParams(paramString));

                if (params.get(0).startsWith("@")) {
                    params.set(0, params.get(0).substring(1));
                    return entry.getValue().call(params.toArray(new String[0]));
                } else {
                    return null;
                }
            }
        }

        throw new Selector.SelectorParseException("Could not parse query '%s': unexpected token at '%s'", query, predicatesQueue.remainder());
    }

    private void allElements() {
        tq.consume();
        evals.add(new AbstractEvaluator.AllElements());
    }

    private void byNth() {
        String nth = tq.chompBalanced('[', ']');
        evals.add(new XEvaluators.IsNthOfType(0, Integer.parseInt(nth)));
    }

    private void consumeAttribute() {
        tq.consume("@");
        elementOperator = new ElementOperator.AttributeGetter(tq.remainder());
        noEvalAllow = true;
    }

    private void consumeOperatorFunction() {
        String remainder = consumeSubQuery();
        if (remainder.startsWith("text(")) {
            functionText(remainder);
        } else if (remainder.startsWith("regex(")) {
            functionRegex(remainder);
        } else if ("allText()".equals(remainder)) {
            elementOperator = new ElementOperator.AllText();
        } else if ("tidyText()".equals(remainder)) {
            elementOperator = new ElementOperator.TidyText();
        } else if ("html()".equals(remainder)) {
            elementOperator = new ElementOperator.Html();
        } else if ("outerHtml()".equals(remainder)) {
            elementOperator = new ElementOperator.OuterHtml();
        } else {
            throw new IllegalArgumentException("Unsupported function " + remainder);
        }
        if (elementOperator != null) {
            noEvalAllow = true;
        }
    }

    private void functionRegex(String remainder) {
        Preconditions.isTrue(remainder.endsWith(")"), "Unclosed bracket for function! " + remainder);
        List<String> params =
                SoupTokenQueue.trimQuotes(SoupTokenQueue.parseFuncionParams(remainder.substring("regex(".length(), remainder.length()
                        - 1)));
        if (params.size() == 1) {
            elementOperator = new ElementOperator.Regex(params.get(0));
        } else if (params.size() == NUM_2) {
            if (params.get(0).startsWith("@")) {
                elementOperator = new ElementOperator.Regex(params.get(1), params.get(0).substring(1));
            } else {
                elementOperator = new ElementOperator.Regex(params.get(0), null, Integer.parseInt(params.get(1)));
            }
        } else if (params.size() == NUM_3) {
            elementOperator =
                    new ElementOperator.Regex(params.get(1), params.get(0).substring(1), Integer.parseInt(params.get(2)));
        } else {
            throw new Selector.SelectorParseException("Unknown usage for regex()" + remainder);
        }
    }

    private void functionText(String remainder) {
        Matcher matcher = patternForText.matcher(remainder);
        if (matcher.matches()) {
            int attributeGroup;
            String group = matcher.group(1);
            if ("".equals(group)) {
                attributeGroup = 0;
            } else {
                attributeGroup = Integer.parseInt(group);
            }
            elementOperator = new ElementOperator.GroupedText(attributeGroup);
        }
    }

    private void byTag() {
        String tagName = tq.consumeElementSelector();
        Preconditions.notEmpty(tagName);

        // namespaces: if element name is "abc:def", selector must be "abc|def", so flip:
        if (tagName.contains("|")) {
            tagName = tagName.replace("|", ":");
        }

        evals.add(new AbstractEvaluator.Tag(tagName.trim().toLowerCase()));
    }

    private AbstractEvaluator byAttribute(SoupTokenQueue cq) {
        cq.matchChomp("@");
        String key =
                cq.consumeToAny("=", "!=", "^=", "$=", "*=", "~=");
        Preconditions.notEmpty(key);
        cq.consumeWhitespace();
        AbstractEvaluator evaluator;
        if (cq.isEmpty()) {
            if ("*".equals(key)) {
                evaluator = new XEvaluators.HasAnyAttribute();
            } else {
                evaluator = new AbstractEvaluator.Attribute(key);
            }
        } else {
            if (cq.matchChomp("=")) {
                String value = chompEqualValue(cq);
                //to support select one class out of all
                if ("class".equals(key)) {
                    String className = SoupTokenQueue.trimQuotes(value);
                    if (!className.contains(" ")) {
                        evaluator = new AbstractEvaluator.Class(className);
                    } else {
                        evaluator = new AbstractEvaluator.AttributeWithValue(key, className);
                    }
                } else {
                    evaluator = new AbstractEvaluator.AttributeWithValue(key, SoupTokenQueue.trimQuotes(value));
                }
            } else if (cq.matchChomp("!=")) {
                evaluator = new AbstractEvaluator.AttributeWithValueNot(key, SoupTokenQueue.trimQuotes(chompEqualValue(cq)));
            } else if (cq.matchChomp("^=")) {
                evaluator = new AbstractEvaluator.AttributeWithValueStarting(key, SoupTokenQueue.trimQuotes(chompEqualValue(cq)));
            } else if (cq.matchChomp("$=")) {
                evaluator = new AbstractEvaluator.AttributeWithValueEnding(key, SoupTokenQueue.trimQuotes(chompEqualValue(cq)));
            } else if (cq.matchChomp("*=")) {
                evaluator =
                        new AbstractEvaluator.AttributeWithValueContaining(key, SoupTokenQueue.trimQuotes(chompEqualValue(cq)));
            } else if (cq.matchChomp("~=")) {
                evaluator =
                        new AbstractEvaluator.AttributeWithValueMatching(key, Pattern.compile(SoupTokenQueue.trimQuotes(chompEqualValue(cq))));
            } else {
                throw new Selector.SelectorParseException("Could not parse attribute query '%s': unexpected token at '%s'", query, chompEqualValue(cq));
            }
        }
        return evaluator;
    }

    private String chompEqualValue(SoupTokenQueue cq) {
        String value;
        if (cq.matchChomp("'")) {
            value = cq.chompTo("'");
        } else if (cq.matchChomp("\"")) {
            value = cq.chompTo("\"");
        } else if (cq.containsAny(" ")) {
            value = cq.chompTo(" ");
        } else {
            value = cq.remainder();
        }
        return value;
    }

    enum Operation {
        /**
         * and
         */
        AND,
        /**
         * or
         */
        OR
    }

    interface FunctionEvaluator {
        /**
         * 执行
         *
         * @param param 参数
         * @return 结果
         */
        AbstractEvaluator call(String... param);
    }

    /**
     * EvaluatorStack for logic calculate.
     * Priority: AND &gt; OR, Regardless of bracket.
     * <br>
     * Calculate AND immediately.
     * Store evaluator with OR, until there are two evaluator in stack, then calculate it.
     */
    static class EvaluatorStack extends Stack<AbstractEvaluator> {

        public void calc(AbstractEvaluator evaluator, Operation operation) {
            if (size() == 0) {
                push(evaluator);
            } else {
                if (operation == Operation.AND) {
                    evaluator = new AbstractCombiningEvaluator.And(pop(), evaluator);
                } else {
                    mergeOr();
                }
                push(evaluator);
            }
        }

        public void mergeOr() {
            if (size() >= NUM_2) {
                AbstractEvaluator pop1 = pop();
                AbstractEvaluator pop2 = pop();
                AbstractEvaluator tempEvaluator = new AbstractCombiningEvaluator.Or(pop2, pop1);
                push(tempEvaluator);
            }
        }
    }
}
