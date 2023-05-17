package com.chua.common.support.jsoup.select;

import com.chua.common.support.jsoup.helper.Validate;
import com.chua.common.support.jsoup.parser.TokenQueue;
import com.chua.common.support.utils.NumberUtils;
import com.chua.common.support.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_COMMA_CHAR;
import static com.chua.common.support.jsoup.internal.Normalizer.normalize;

/**
 * Parses a CSS selector into an Evaluator tree.
 *
 * @author Administrator
 */
public class QueryParser {
    private final static String[] COMBINATORS = {",", ">", "+", "~", " "};
    private static final String[] ATTRIBUTE_EVAL = new String[]{"=", "!=", "^=", "$=", "*=", "~="};

    private final TokenQueue tq;
    private final String query;
    private final List<Evaluator> evaluators = new ArrayList<>();

    /**
     * Create a new QueryParser.
     * @param query CSS query
     */
    private QueryParser(String query) {
        Validate.notEmpty(query);
        query = query.trim();
        this.query = query;
        this.tq = new TokenQueue(query);
    }

    /**
     * Parse a CSS query into an Evaluator.
     * @param query CSS query
     * @return Evaluator
     * @see Selector selector query syntax
     */
    public static Evaluator parse(String query) {
        try {
            QueryParser p = new QueryParser(query);
            return p.parse();
        } catch (IllegalArgumentException e) {
            throw new Selector.SelectorParseException(e.getMessage());
        }
    }

    /**
     * Parse the query
     * @return Evaluator
     */
    Evaluator parse() {
        tq.consumeWhitespace();

        if (tq.matchesAny(COMBINATORS)) {
            evaluators.add(new StructuralEvaluator.Root());
            combinator(tq.consume());
        } else {
            findElements();
        }

        while (!tq.isEmpty()) {
            boolean seenWhite = tq.consumeWhitespace();

            if (tq.matchesAny(COMBINATORS)) {
                combinator(tq.consume());
            } else if (seenWhite) {
                combinator(' ');
            } else { // E.class, E#id, E[attr] etc. AND
                findElements();
            }
        }

        if (evaluators.size() == 1) {
            return evaluators.get(0);
        }

        return new CombiningEvaluator.And(evaluators);
    }

    private void combinator(char combinator) {
        tq.consumeWhitespace();
        String subQuery = consumeSubQuery();
        Evaluator rootEval;
        Evaluator currentEval; // the evaluator the new eval will be combined to. could be root, or rightmost or.
        Evaluator newEval = parse(subQuery);
        boolean replaceRightMost = false;

        if (evaluators.size() == 1) {
            rootEval = currentEval = evaluators.get(0);
            if (rootEval instanceof CombiningEvaluator.Or && combinator != SYMBOL_COMMA_CHAR) {
                currentEval = ((CombiningEvaluator.Or) currentEval).rightMostEvaluator();
                assert currentEval != null;
                replaceRightMost = true;
            }
        } else {
            rootEval = currentEval = new CombiningEvaluator.And(evaluators);
        }
        evaluators.clear();

        switch (combinator) {
            case '>':
                currentEval = new CombiningEvaluator.And(new StructuralEvaluator.ImmediateParent(currentEval), newEval);
                break;
            case ' ':
                currentEval = new CombiningEvaluator.And(new StructuralEvaluator.Parent(currentEval), newEval);
                break;
            case '+':
                currentEval = new CombiningEvaluator.And(new StructuralEvaluator.ImmediatePreviousSibling(currentEval), newEval);
                break;
            case '~':
                currentEval = new CombiningEvaluator.And(new StructuralEvaluator.PreviousSibling(currentEval), newEval);
                break;
            case ',':
                CombiningEvaluator.Or or;
                if (currentEval instanceof CombiningEvaluator.Or) {
                    or = (CombiningEvaluator.Or) currentEval;
                } else {
                    or = new CombiningEvaluator.Or();
                    or.add(currentEval);
                }
                or.add(newEval);
                currentEval = or;
                break;
            default:
                throw new Selector.SelectorParseException("Unknown combinator '%s'", combinator);
        }

        if (replaceRightMost) {
            ((CombiningEvaluator.Or) rootEval).replaceRightMostEvaluator(currentEval);
        } else {
            rootEval = currentEval;
        }
        evaluators.add(rootEval);
    }

    private String consumeSubQuery() {
        StringBuilder sq = StringUtils.borrowBuilder();
        while (!tq.isEmpty()) {
            if (tq.matches("(")) {
                sq.append("(").append(tq.chompBalanced('(', ')')).append(")");
            } else if (tq.matches("[")) {
                sq.append("[").append(tq.chompBalanced('[', ']')).append("]");
            } else if (tq.matchesAny(COMBINATORS)) {
                if (sq.length() > 0) {
                    break;
                } else {
                    tq.consume();
                }
            } else {
                sq.append(tq.consume());
            }
        }
        return sq.toString();
    }

    private void findElements() {
        if (tq.matchChomp("#")) {
            byId();
        } else if (tq.matchChomp(".")) {
            byClass();
        } else if (tq.matchesWord() || tq.matches("*|")) {
            byTag();
        } else if (tq.matches("[")) {
            byAttribute();
        } else if (tq.matchChomp("*")) {
            allElements();
        } else if (tq.matchChomp(":lt(")) {
            indexLessThan();
        } else if (tq.matchChomp(":gt(")) {
            indexGreaterThan();
        } else if (tq.matchChomp(":eq(")) {
            indexEquals();
        } else if (tq.matches(":has(")) {
            has();
        } else if (tq.matches(":contains(")) {
            contains(false);
        } else if (tq.matches(":containsOwn(")) {
            contains(true);
        } else if (tq.matches(":containsWholeText(")) {
            containsWholeText(false);
        } else if (tq.matches(":containsWholeOwnText(")) {
            containsWholeText(true);
        } else if (tq.matches(":containsData(")) {
            containsData();
        } else if (tq.matches(":matches(")) {
            matches(false);
        } else if (tq.matches(":matchesOwn(")) {
            matches(true);
        } else if (tq.matches(":matchesWholeText(")) {
            matchesWholeText(false);
        } else if (tq.matches(":matchesWholeOwnText(")) {
            matchesWholeText(true);
        } else if (tq.matches(":not(")) {
            not();
        } else if (tq.matchChomp(":nth-child(")) {
            cssNthChild(false, false);
        } else if (tq.matchChomp(":nth-last-child(")) {
            cssNthChild(true, false);
        } else if (tq.matchChomp(":nth-of-type(")) {
            cssNthChild(false, true);
        } else if (tq.matchChomp(":nth-last-of-type(")) {
            cssNthChild(true, true);
        } else if (tq.matchChomp(":first-child")) {
            evaluators.add(new Evaluator.IsFirstChild());
        } else if (tq.matchChomp(":last-child")) {
            evaluators.add(new Evaluator.IsLastChild());
        } else if (tq.matchChomp(":first-of-type")) {
            evaluators.add(new Evaluator.IsFirstOfType());
        } else if (tq.matchChomp(":last-of-type")) {
            evaluators.add(new Evaluator.IsLastOfType());
        } else if (tq.matchChomp(":only-child")) {
            evaluators.add(new Evaluator.IsOnlyChild());
        } else if (tq.matchChomp(":only-of-type")) {
            evaluators.add(new Evaluator.IsOnlyOfType());
        } else if (tq.matchChomp(":empty")) {
            evaluators.add(new Evaluator.IsEmpty());
        } else if (tq.matchChomp(":root")) {
            evaluators.add(new Evaluator.IsRoot());
        } else if (tq.matchChomp(":matchText")) {
            evaluators.add(new Evaluator.MatchText());
        } else {
            throw new Selector.SelectorParseException("Could not parse query '%s': unexpected token at '%s'", query, tq.remainder());
        }

    }

    private void byId() {
        String id = tq.consumeCssIdentifier();
        Validate.notEmpty(id);
        evaluators.add(new Evaluator.Id(id));
    }

    private void byClass() {
        String className = tq.consumeCssIdentifier();
        Validate.notEmpty(className);
        evaluators.add(new Evaluator.Class(className.trim()));
    }

    private void byTag() {
        String tagName = normalize(tq.consumeElementSelector());
        Validate.notEmpty(tagName);

        if (tagName.startsWith("*|")) {
            String plainTag = tagName.substring(2);
            evaluators.add(new CombiningEvaluator.Or(
                    new Evaluator.Tag(plainTag),
                    new Evaluator.TagEndsWith(tagName.replace("*|", ":")))
            );
        } else {
            if (tagName.contains("|")) {
                tagName = tagName.replace("|", ":");
            }

            evaluators.add(new Evaluator.Tag(tagName));
        }
    }

    private void byAttribute() {
        TokenQueue cq = new TokenQueue(tq.chompBalanced('[', ']'));
        String key = cq.consumeToAny(ATTRIBUTE_EVAL);
        Validate.notEmpty(key);
        cq.consumeWhitespace();

        if (cq.isEmpty()) {
            if (key.startsWith("^")) {
                evaluators.add(new Evaluator.AttributeStarting(key.substring(1)));
            } else {
                evaluators.add(new Evaluator.Attribute(key));
            }
        } else {
            if (cq.matchChomp("=")) {
                evaluators.add(new Evaluator.AttributeWithValue(key, cq.remainder()));
            } else if (cq.matchChomp("!=")) {
                evaluators.add(new Evaluator.AttributeWithValueNot(key, cq.remainder()));
            } else if (cq.matchChomp("^=")) {
                evaluators.add(new Evaluator.AttributeWithValueStarting(key, cq.remainder()));
            } else if (cq.matchChomp("$=")) {
                evaluators.add(new Evaluator.AttributeWithValueEnding(key, cq.remainder()));
            } else if (cq.matchChomp("*=")) {
                evaluators.add(new Evaluator.AttributeWithValueContaining(key, cq.remainder()));
            } else if (cq.matchChomp("~=")) {
                evaluators.add(new Evaluator.AttributeWithValueMatching(key, Pattern.compile(cq.remainder())));
            } else {
                throw new Selector.SelectorParseException("Could not parse attribute query '%s': unexpected token at '%s'", query, cq.remainder());
            }
        }
    }

    private void allElements() {
        evaluators.add(new Evaluator.AllElements());
    }

    private void indexLessThan() {
        evaluators.add(new Evaluator.IndexLessThan(consumeIndex()));
    }

    private void indexGreaterThan() {
        evaluators.add(new Evaluator.IndexGreaterThan(consumeIndex()));
    }

    private void indexEquals() {
        evaluators.add(new Evaluator.IndexEquals(consumeIndex()));
    }
    
    private static final Pattern NTH_AB = Pattern.compile("(([+-])?(\\d+)?)n(\\s*([+-])?\\s*\\d+)?", Pattern.CASE_INSENSITIVE);
    private static final Pattern NTH_B  = Pattern.compile("([+-])?(\\d+)");

	private void cssNthChild(boolean backwards, boolean ofType) {
        String argS = normalize(tq.chompTo(")"));
        Matcher matcher = NTH_AB.matcher(argS);
        Matcher mB = NTH_B.matcher(argS);
		final int a, b;
		if ("odd".equals(argS)) {
			a = 2;
			b = 1;
		} else if ("even".equals(argS)) {
            a = 2;
            b = 0;
        } else if (matcher.matches()) {
            a = matcher.group(3) != null ? Integer.parseInt(matcher.group(1).replaceFirst("^\\+", "")) : 1;
            b = matcher.group(4) != null ? Integer.parseInt(matcher.group(4).replaceFirst("^\\+", "")) : 0;
        } else if (mB.matches()) {
            a = 0;
            b = Integer.parseInt(mB.group().replaceFirst("^\\+", ""));
        } else {
            throw new Selector.SelectorParseException("Could not parse nth-index '%s': unexpected format", argS);
        }
		if (ofType) {
            if (backwards) {
                evaluators.add(new Evaluator.IsNthLastOfType(a, b));
            } else {
                evaluators.add(new Evaluator.IsNthOfType(a, b));
            }
        } else {
			if (backwards) {
                evaluators.add(new Evaluator.IsNthLastChild(a, b));
            } else {
                evaluators.add(new Evaluator.IsNthChild(a, b));
            }
		}
	}

    private int consumeIndex() {
        String indexS = tq.chompTo(")").trim();
        Validate.isTrue(NumberUtils.isNumber(indexS), "Index must be numeric");
        return Integer.parseInt(indexS);
    }

    private void has() {
        tq.consume(":has");
        String subQuery = tq.chompBalanced('(', ')');
        Validate.notEmpty(subQuery, ":has(selector) sub-select must not be empty");
        evaluators.add(new StructuralEvaluator.Has(parse(subQuery)));
    }

    private void contains(boolean own) {
        String query = own ? ":containsOwn" : ":contains";
        tq.consume(query);
        String searchText = TokenQueue.unescape(tq.chompBalanced('(', ')'));
        Validate.notEmpty(searchText, query + "(text) query must not be empty");
        evaluators.add(own
                ? new Evaluator.ContainsOwnText(searchText)
                : new Evaluator.ContainsText(searchText));
    }

    private void containsWholeText(boolean own) {
        String query = own ? ":containsWholeOwnText" : ":containsWholeText";
        tq.consume(query);
        String searchText = TokenQueue.unescape(tq.chompBalanced('(', ')'));
        Validate.notEmpty(searchText, query + "(text) query must not be empty");
        evaluators.add(own
            ? new Evaluator.ContainsWholeOwnText(searchText)
            : new Evaluator.ContainsWholeText(searchText));
    }

    private void containsData() {
        tq.consume(":containsData");
        String searchText = TokenQueue.unescape(tq.chompBalanced('(', ')'));
        Validate.notEmpty(searchText, ":containsData(text) query must not be empty");
        evaluators.add(new Evaluator.ContainsData(searchText));
    }

    private void matches(boolean own) {
        String query = own ? ":matchesOwn" : ":matches";
        tq.consume(query);
        String regex = tq.chompBalanced('(', ')');
        Validate.notEmpty(regex, query + "(regex) query must not be empty");

        evaluators.add(own
                ? new Evaluator.MatchesOwn(Pattern.compile(regex))
                : new Evaluator.Matches(Pattern.compile(regex)));
    }

    private void matchesWholeText(boolean own) {
        String query = own ? ":matchesWholeOwnText" : ":matchesWholeText";
        tq.consume(query);
        String regex = tq.chompBalanced('(', ')');
        Validate.notEmpty(regex, query + "(regex) query must not be empty");

        evaluators.add(own
                ? new Evaluator.MatchesWholeOwnText(Pattern.compile(regex))
                : new Evaluator.MatchesWholeText(Pattern.compile(regex)));
    }

    private void not() {
        tq.consume(":not");
        String subQuery = tq.chompBalanced('(', ')');
        Validate.notEmpty(subQuery, ":not(selector) subselect must not be empty");

        evaluators.add(new StructuralEvaluator.Not(parse(subQuery)));
    }

    @Override
    public String toString() {
        return query;
    }


}