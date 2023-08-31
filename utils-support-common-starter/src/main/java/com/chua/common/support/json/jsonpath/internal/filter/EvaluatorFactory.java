package com.chua.common.support.json.jsonpath.internal.filter;

import com.chua.common.support.json.jsonpath.JsonPathException;
import com.chua.common.support.json.jsonpath.Predicate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import static com.chua.common.support.json.jsonpath.internal.filter.ValueNodes.PatternNode;
import static com.chua.common.support.json.jsonpath.internal.filter.ValueNodes.ValueListNode;

/**
 * @author Administrator
 */
public class EvaluatorFactory {

    private static final Map<RelationalOperator, Evaluator> EVALUATORS = new HashMap<RelationalOperator, Evaluator>();

    static {
        EVALUATORS.put(RelationalOperator.EXISTS, new ExistsEvaluator());
        EVALUATORS.put(RelationalOperator.NE, new NotEqualsEvaluator());
        EVALUATORS.put(RelationalOperator.TSNE, new TypeSafeNotEqualsEvaluator());
        EVALUATORS.put(RelationalOperator.EQ, new EqualsEvaluator());
        EVALUATORS.put(RelationalOperator.TSEQ, new TypeSafeEqualsEvaluator());
        EVALUATORS.put(RelationalOperator.LT, new LessThanEvaluator());
        EVALUATORS.put(RelationalOperator.LTE, new LessThanEqualsEvaluator());
        EVALUATORS.put(RelationalOperator.GT, new GreaterThanEvaluator());
        EVALUATORS.put(RelationalOperator.GTE, new GreaterThanEqualsEvaluator());
        EVALUATORS.put(RelationalOperator.REGEX, new RegexpEvaluator());
        EVALUATORS.put(RelationalOperator.SIZE, new SizeEvaluator());
        EVALUATORS.put(RelationalOperator.EMPTY, new EmptyEvaluator());
        EVALUATORS.put(RelationalOperator.IN, new InEvaluator());
        EVALUATORS.put(RelationalOperator.NIN, new NotInEvaluator());
        EVALUATORS.put(RelationalOperator.ALL, new AllEvaluator());
        EVALUATORS.put(RelationalOperator.CONTAINS, new ContainsEvaluator());
        EVALUATORS.put(RelationalOperator.MATCHES, new PredicateMatchEvaluator());
        EVALUATORS.put(RelationalOperator.TYPE, new TypeEvaluator());
        EVALUATORS.put(RelationalOperator.SUBSETOF, new SubsetOfEvaluator());
        EVALUATORS.put(RelationalOperator.ANYOF, new AnyOfEvaluator());
        EVALUATORS.put(RelationalOperator.NONEOF, new NoneOfEvaluator());
    }

    public static Evaluator createEvaluator(RelationalOperator operator) {
        return EVALUATORS.get(operator);
    }

    private static class ExistsEvaluator implements Evaluator {
        @Override
        public boolean evaluate(BaseValueNode left, BaseValueNode right, Predicate.PredicateContext ctx) {
            if (!left.isBooleanNode() && !right.isBooleanNode()) {
                throw new JsonPathException("Failed to evaluate exists expression");
            }
            return left.asBooleanNode().getBoolean() == right.asBooleanNode().getBoolean();
        }
    }

    private static class NotEqualsEvaluator implements Evaluator {
        @Override
        public boolean evaluate(BaseValueNode left, BaseValueNode right, Predicate.PredicateContext ctx) {
            return !EVALUATORS.get(RelationalOperator.EQ).evaluate(left, right, ctx);
        }
    }

    private static class TypeSafeNotEqualsEvaluator implements Evaluator {
        @Override
        public boolean evaluate(BaseValueNode left, BaseValueNode right, Predicate.PredicateContext ctx) {
            return !EVALUATORS.get(RelationalOperator.TSEQ).evaluate(left, right, ctx);
        }
    }

    private static class EqualsEvaluator implements Evaluator {
        @Override
        public boolean evaluate(BaseValueNode left, BaseValueNode right, Predicate.PredicateContext ctx) {
            if (left.isJsonNode() && right.isJsonNode()) {
                return left.asJsonNode().equals(right.asJsonNode(), ctx);
            } else {
                return left.equals(right);
            }
        }
    }

    private static class TypeSafeEqualsEvaluator implements Evaluator {
        @Override
        public boolean evaluate(BaseValueNode left, BaseValueNode right, Predicate.PredicateContext ctx) {
            if (!left.getClass().equals(right.getClass())) {
                return false;
            }
            return EVALUATORS.get(RelationalOperator.EQ).evaluate(left, right, ctx);
        }
    }

    private static class TypeEvaluator implements Evaluator {
        @Override
        public boolean evaluate(BaseValueNode left, BaseValueNode right, Predicate.PredicateContext ctx) {
            return right.asClassNode().getClazz() == left.type(ctx);
        }
    }

    private static class LessThanEvaluator implements Evaluator {
        @Override
        public boolean evaluate(BaseValueNode left, BaseValueNode right, Predicate.PredicateContext ctx) {
            if (left.isNumberNode() && right.isNumberNode()) {
                return left.asNumberNode().getNumber().compareTo(right.asNumberNode().getNumber()) < 0;
            }
            if (left.isStringNode() && right.isStringNode()) {
                return left.asStringNode().getString().compareTo(right.asStringNode().getString()) < 0;
            }
            if (left.isOffsetDateTimeNode() && right.isOffsetDateTimeNode()) { //workaround for issue: https://github.com/json-path/JsonPath/issues/613
                return left.asOffsetDateTimeNode().getDate().compareTo(right.asOffsetDateTimeNode().getDate()) < 0;
            }
            return false;
        }
    }

    private static class LessThanEqualsEvaluator implements Evaluator {
        @Override
        public boolean evaluate(BaseValueNode left, BaseValueNode right, Predicate.PredicateContext ctx) {
            if (left.isNumberNode() && right.isNumberNode()) {
                return left.asNumberNode().getNumber().compareTo(right.asNumberNode().getNumber()) <= 0;
            }
            if (left.isStringNode() && right.isStringNode()) {
                return left.asStringNode().getString().compareTo(right.asStringNode().getString()) <= 0;
            }
            if (left.isOffsetDateTimeNode() && right.isOffsetDateTimeNode()) { //workaround for issue: https://github.com/json-path/JsonPath/issues/613
                return left.asOffsetDateTimeNode().getDate().compareTo(right.asOffsetDateTimeNode().getDate()) <= 0;
            }
            return false;
        }
    }

    private static class GreaterThanEvaluator implements Evaluator {
        @Override
        public boolean evaluate(BaseValueNode left, BaseValueNode right, Predicate.PredicateContext ctx) {
            if (left.isNumberNode() && right.isNumberNode()) {
                return left.asNumberNode().getNumber().compareTo(right.asNumberNode().getNumber()) > 0;
            } else if (left.isStringNode() && right.isStringNode()) {
                return left.asStringNode().getString().compareTo(right.asStringNode().getString()) > 0;
            } else if (left.isOffsetDateTimeNode() && right.isOffsetDateTimeNode()) { //workaround for issue: https://github.com/json-path/JsonPath/issues/613
                return left.asOffsetDateTimeNode().getDate().compareTo(right.asOffsetDateTimeNode().getDate()) > 0;
            }
            return false;
        }
    }

    private static class GreaterThanEqualsEvaluator implements Evaluator {
        @Override
        public boolean evaluate(BaseValueNode left, BaseValueNode right, Predicate.PredicateContext ctx) {
            if (left.isNumberNode() && right.isNumberNode()) {
                return left.asNumberNode().getNumber().compareTo(right.asNumberNode().getNumber()) >= 0;
            } else if (left.isStringNode() && right.isStringNode()) {
                return left.asStringNode().getString().compareTo(right.asStringNode().getString()) >= 0;
            } else if (left.isOffsetDateTimeNode() && right.isOffsetDateTimeNode()) { //workaround for issue: https://github.com/json-path/JsonPath/issues/613
                return left.asOffsetDateTimeNode().getDate().compareTo(right.asOffsetDateTimeNode().getDate()) >= 0;
            }
            return false;
        }
    }

    private static class SizeEvaluator implements Evaluator {
        @Override
        public boolean evaluate(BaseValueNode left, BaseValueNode right, Predicate.PredicateContext ctx) {
            if (!right.isNumberNode()) {
                return false;
            }
            int expectedSize = right.asNumberNode().getNumber().intValue();

            if (left.isStringNode()) {
                return left.asStringNode().length() == expectedSize;
            } else if (left.isJsonNode()) {
                return left.asJsonNode().length(ctx) == expectedSize;
            }
            return false;
        }
    }

    private static class EmptyEvaluator implements Evaluator {
        @Override
        public boolean evaluate(BaseValueNode left, BaseValueNode right, Predicate.PredicateContext ctx) {
            if (left.isStringNode()) {
                return left.asStringNode().isEmpty() == right.asBooleanNode().getBoolean();
            } else if (left.isJsonNode()) {
                return left.asJsonNode().isEmpty(ctx) == right.asBooleanNode().getBoolean();
            }
            return false;
        }
    }

    private static class InEvaluator implements Evaluator {
        @Override
        public boolean evaluate(BaseValueNode left, BaseValueNode right, Predicate.PredicateContext ctx) {
            ValueListNode valueListNode;
            if (right.isJsonNode()) {
                BaseValueNode vn = right.asJsonNode().asValueListNode(ctx);
                if (vn.isUndefinedNode()) {
                    return false;
                } else {
                    valueListNode = vn.asValueListNode();
                }
            } else {
                valueListNode = right.asValueListNode();
            }
            return valueListNode.contains(left);
        }
    }

    private static class NotInEvaluator implements Evaluator {
        @Override
        public boolean evaluate(BaseValueNode left, BaseValueNode right, Predicate.PredicateContext ctx) {
            return !EVALUATORS.get(RelationalOperator.IN).evaluate(left, right, ctx);
        }
    }

    private static class AllEvaluator implements Evaluator {
        @Override
        public boolean evaluate(BaseValueNode left, BaseValueNode right, Predicate.PredicateContext ctx) {
            ValueListNode requiredValues = right.asValueListNode();

            if (left.isJsonNode()) {
                BaseValueNode valueNode = left.asJsonNode().asValueListNode(ctx); //returns UndefinedNode if conversion is not possible
                if (valueNode.isValueListNode()) {
                    ValueListNode shouldContainAll = valueNode.asValueListNode();
                    for (BaseValueNode required : requiredValues) {
                        if (!shouldContainAll.contains(required)) {
                            return false;
                        }
                    }
                }
                return true;
            }
            return false;
        }
    }

    private static class ContainsEvaluator implements Evaluator {
        @Override
        public boolean evaluate(BaseValueNode left, BaseValueNode right, Predicate.PredicateContext ctx) {
            if (left.isStringNode() && right.isStringNode()) {
                return left.asStringNode().contains(right.asStringNode().getString());
            } else if (left.isJsonNode()) {
                BaseValueNode valueNode = left.asJsonNode().asValueListNode(ctx);
                if (valueNode.isUndefinedNode()) {
                    return false;
                } else {
                    boolean res = valueNode.asValueListNode().contains(right);
                    return res;
                }
            }
            return false;
        }
    }

    private static class PredicateMatchEvaluator implements Evaluator {
        @Override
        public boolean evaluate(BaseValueNode left, BaseValueNode right, Predicate.PredicateContext ctx) {
            return right.asPredicateNode().getPredicate().apply(ctx);
        }
    }

    private static class RegexpEvaluator implements Evaluator {
        @Override
        public boolean evaluate(BaseValueNode left, BaseValueNode right, Predicate.PredicateContext ctx) {
            if (!(left.isPatternNode() ^ right.isPatternNode())) {
                return false;
            }

            if (left.isPatternNode()) {
                boolean b = right.isValueListNode() || (right.isJsonNode() && right.asJsonNode().isArray(ctx));
                if (b) {
                    return matchesAny(left.asPatternNode(), right.asJsonNode().asValueListNode(ctx));
                }
                return matches(left.asPatternNode(), getInput(right));
            } else {
                boolean b = left.isValueListNode() || (left.isJsonNode() && left.asJsonNode().isArray(ctx));
                if (b) {
                    return matchesAny(right.asPatternNode(), left.asJsonNode().asValueListNode(ctx));
                }
                return matches(right.asPatternNode(), getInput(left));
            }
        }

        private boolean matches(PatternNode patternNode, String inputToMatch) {
            return patternNode.getCompiledPattern().matcher(inputToMatch).matches();
        }

        private boolean matchesAny(PatternNode patternNode, BaseValueNode valueNode) {
            if (!valueNode.isValueListNode()) {
                return false;
            }

            ValueListNode listNode = valueNode.asValueListNode();
            Pattern pattern = patternNode.getCompiledPattern();

            for (Iterator<BaseValueNode> it = listNode.iterator(); it.hasNext(); ) {
                String input = getInput(it.next());
                if (pattern.matcher(input).matches()) {
                    return true;
                }
            }
            return false;
        }

        private String getInput(BaseValueNode valueNode) {
            String input = "";

            if (valueNode.isStringNode() || valueNode.isNumberNode()) {
                input = valueNode.asStringNode().getString();
            } else if (valueNode.isBooleanNode()) {
                input = valueNode.asBooleanNode().toString();
            }

            return input;
        }
    }

    private static class SubsetOfEvaluator implements Evaluator {
        @Override
        public boolean evaluate(BaseValueNode left, BaseValueNode right, Predicate.PredicateContext ctx) {
            ValueListNode rightValueListNode;
            if (right.isJsonNode()) {
                BaseValueNode vn = right.asJsonNode().asValueListNode(ctx);
                if (vn.isUndefinedNode()) {
                    return false;
                } else {
                    rightValueListNode = vn.asValueListNode();
                }
            } else {
                rightValueListNode = right.asValueListNode();
            }
            ValueListNode leftValueListNode;
            if (left.isJsonNode()) {
                BaseValueNode vn = left.asJsonNode().asValueListNode(ctx);
                if (vn.isUndefinedNode()) {
                    return false;
                } else {
                    leftValueListNode = vn.asValueListNode();
                }
            } else {
                leftValueListNode = left.asValueListNode();
            }
            return leftValueListNode.subsetof(rightValueListNode);
        }
    }

    private static class AnyOfEvaluator implements Evaluator {
        @Override
        public boolean evaluate(BaseValueNode left, BaseValueNode right, Predicate.PredicateContext ctx) {
            ValueListNode rightValueListNode;
            if (right.isJsonNode()) {
                BaseValueNode vn = right.asJsonNode().asValueListNode(ctx);
                if (vn.isUndefinedNode()) {
                    return false;
                } else {
                    rightValueListNode = vn.asValueListNode();
                }
            } else {
                rightValueListNode = right.asValueListNode();
            }
            ValueListNode leftValueListNode;
            if (left.isJsonNode()) {
                BaseValueNode vn = left.asJsonNode().asValueListNode(ctx);
                if (vn.isUndefinedNode()) {
                    return false;
                } else {
                    leftValueListNode = vn.asValueListNode();
                }
            } else {
                leftValueListNode = left.asValueListNode();
            }

            for (BaseValueNode leftValueNode : leftValueListNode) {
                for (BaseValueNode rightValueNode : rightValueListNode) {
                    if (leftValueNode.equals(rightValueNode)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private static class NoneOfEvaluator implements Evaluator {
        @Override
        public boolean evaluate(BaseValueNode left, BaseValueNode right, Predicate.PredicateContext ctx) {
            ValueListNode rightValueListNode;
            if (right.isJsonNode()) {
                BaseValueNode vn = right.asJsonNode().asValueListNode(ctx);
                if (vn.isUndefinedNode()) {
                    return false;
                } else {
                    rightValueListNode = vn.asValueListNode();
                }
            } else {
                rightValueListNode = right.asValueListNode();
            }
            ValueListNode leftValueListNode;
            if (left.isJsonNode()) {
                BaseValueNode vn = left.asJsonNode().asValueListNode(ctx);
                if (vn.isUndefinedNode()) {
                    return false;
                } else {
                    leftValueListNode = vn.asValueListNode();
                }
            } else {
                leftValueListNode = left.asValueListNode();
            }

            for (BaseValueNode leftValueNode : leftValueListNode) {
                for (BaseValueNode rightValueNode : rightValueListNode) {
                    if (leftValueNode.equals(rightValueNode)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }
}
