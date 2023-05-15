package com.chua.common.support.json.jsonpath.internal.filter;

import com.chua.common.support.json.Json;
import com.chua.common.support.json.jsonpath.InvalidPathException;
import com.chua.common.support.json.jsonpath.JsonPathException;
import com.chua.common.support.json.jsonpath.Predicate;
import com.chua.common.support.json.jsonpath.internal.Path;
import com.chua.common.support.json.jsonpath.internal.path.PathCompiler;

import java.time.OffsetDateTime;
import java.util.regex.Pattern;

import static com.chua.common.support.json.jsonpath.internal.filter.ValueNodes.*;

/**
 * @author Administrator
 */
public abstract class ValueNode {

    public abstract Class<?> type(Predicate.PredicateContext ctx);

    public boolean isPatternNode() {
        return false;
    }

    public PatternNode asPatternNode() {
        throw new InvalidPathException("Expected regexp node");
    }

    public boolean isPathNode() {
        return false;
    }

    public PathNode asPathNode() {
        throw new InvalidPathException("Expected path node");
    }

    public boolean isNumberNode() {
        return false;
    }

    public NumberNode asNumberNode() {
        throw new InvalidPathException("Expected number node");
    }

    public boolean isStringNode() {
        return false;
    }

    public StringNode asStringNode() {
        throw new InvalidPathException("Expected string node");
    }

    public boolean isBooleanNode() {
        return false;
    }

    public BooleanNode asBooleanNode() {
        throw new InvalidPathException("Expected boolean node");
    }

    public boolean isJsonNode() {
        return false;
    }

    public JsonNode asJsonNode() {
        throw new InvalidPathException("Expected json node");
    }

    public boolean isPredicateNode() {
        return false;
    }

    public PredicateNode asPredicateNode() {
        throw new InvalidPathException("Expected predicate node");
    }

    public boolean isValueListNode() {
        return false;
    }

    public ValueListNode asValueListNode() {
        throw new InvalidPathException("Expected value list node");
    }

    public boolean isNullNode() {
        return false;
    }

    public NullNode asNullNode() {
        throw new InvalidPathException("Expected null node");
    }

    public UndefinedNode asUndefinedNode() {
        throw new InvalidPathException("Expected undefined node");
    }

    public boolean isUndefinedNode() {
        return false;
    }

    public boolean isClassNode() {
        return false;
    }

    public ClassNode asClassNode() {
        throw new InvalidPathException("Expected class node");
    }

    public static ValueNode toValueNode(Object o) {

        if (o == null) {
            return NULL_NODE;
        }
        if (o instanceof ValueNode) {
            return (ValueNode) o;
        }
        if (o instanceof Class) {
            return createClassNode((Class) o);
        } else if (isPath(o)) {
            return new PathNode(o.toString(), false, false);
        } else if (isJson(o)) {
            return createJsonNode(o.toString());
        } else if (o instanceof String) {
            return createStringNode(o.toString(), true);
        } else if (o instanceof Character) {
            return createStringNode(o.toString(), false);
        } else if (o instanceof Number) {
            return createNumberNode(o.toString());
        } else if (o instanceof Boolean) {
            return createBooleanNode(o.toString());
        } else if (o instanceof Pattern) {
            return createPatternNode((Pattern) o);
        } else if (o instanceof OffsetDateTime) {
            return createOffsetDateTimeNode(o.toString());
        } else {
            throw new JsonPathException("Could not determine value type");
        }

    }

    public OffsetDateTimeNode asOffsetDateTimeNode() {
        throw new InvalidPathException("Expected offsetDateTime node");
    }


    private static boolean isPath(Object o) {
        if (o == null || !(o instanceof String)) {
            return false;
        }
        String str = o.toString().trim();
        if (str.length() <= 0) {
            return false;
        }
        char c0 = str.charAt(0);
        if (c0 == '@' || c0 == '$') {
            try {
                PathCompiler.compile(str);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    private static boolean isJson(Object o) {
        if (o == null || !(o instanceof String)) {
            return false;
        }
        String str = o.toString().trim();
        if (str.length() <= 1) {
            return false;
        }
        char c0 = str.charAt(0);
        char c1 = str.charAt(str.length() - 1);
        boolean b = (c0 == '[' && c1 == ']') || (c0 == '{' && c1 == '}');
        if (b) {
            try {
                Json.fromJson(str, Object.class);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public boolean isOffsetDateTimeNode() {
        return false;
    }

    public static StringNode createStringNode(CharSequence charSequence, boolean escape) {
        return new StringNode(charSequence, escape);
    }

    public static ClassNode createClassNode(Class<?> clazz) {
        return new ClassNode(clazz);
    }

    public static NumberNode createNumberNode(CharSequence charSequence) {
        return new NumberNode(charSequence);
    }

    public static BooleanNode createBooleanNode(CharSequence charSequence) {
        return Boolean.parseBoolean(charSequence.toString()) ? TRUE : FALSE;
    }

    public static NullNode createNullNode() {
        return NULL_NODE;
    }

    public static JsonNode createJsonNode(CharSequence json) {
        return new JsonNode(json);
    }

    public static JsonNode createJsonNode(Object parsedJson) {
        return new JsonNode(parsedJson);
    }

    public static PatternNode createPatternNode(CharSequence pattern) {
        return new PatternNode(pattern);
    }

    public static PatternNode createPatternNode(Pattern pattern) {
        return new PatternNode(pattern);
    }

    public static OffsetDateTimeNode createOffsetDateTimeNode(CharSequence charSequence) {
        return new OffsetDateTimeNode(charSequence);
    }


    public static UndefinedNode createUndefinedNode() {
        return UNDEFINED;
    }

    public static PathNode createPathNode(CharSequence path, boolean existsCheck, boolean shouldExists) {
        return new PathNode(path, existsCheck, shouldExists);
    }

    public static ValueNode createPathNode(Path path) {
        return new PathNode(path);
    }


}

