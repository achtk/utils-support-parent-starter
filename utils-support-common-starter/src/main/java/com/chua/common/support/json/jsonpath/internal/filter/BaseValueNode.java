package com.chua.common.support.json.jsonpath.internal.filter;

import com.chua.common.support.json.Json;
import com.chua.common.support.json.jsonpath.InvalidPathException;
import com.chua.common.support.json.jsonpath.JsonPathException;
import com.chua.common.support.json.jsonpath.Predicate;
import com.chua.common.support.json.jsonpath.internal.Path;
import com.chua.common.support.json.jsonpath.internal.path.PathCompiler;

import java.time.OffsetDateTime;
import java.util.regex.Pattern;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_AT_CHAR;
import static com.chua.common.support.constant.CommonConstant.SYMBOL_DOLLAR_CHAR;
import static com.chua.common.support.json.jsonpath.internal.filter.ValueNodes.*;

/**
 * 值节点
 *
 * @author Administrator
 * @date 2023/08/31
 */
public abstract class BaseValueNode {

    /**
     * 类型
     *
     * @param ctx ctx
     * @return {@link Class}<{@link ?}>
     */
    public abstract Class<?> type(Predicate.PredicateContext ctx);

    /**
     * 模式节点
     *
     * @return boolean
     */
    public boolean isPatternNode() {
        return false;
    }

    /**
     * 作为模式节点
     *
     * @return {@link PatternNode}
     */
    public PatternNode asPatternNode() {
        throw new InvalidPathException("Expected regexp node");
    }

    /**
     * 路径节点
     *
     * @return boolean
     */
    public boolean isPathNode() {
        return false;
    }

    /**
     * 作为路径节点
     *
     * @return {@link PathNode}
     */
    public PathNode asPathNode() {
        throw new InvalidPathException("Expected path node");
    }

    /**
     * 是节点号
     *
     * @return boolean
     */
    public boolean isNumberNode() {
        return false;
    }

    /**
     * 随着节点数量
     *
     * @return {@link NumberNode}
     */
    public NumberNode asNumberNode() {
        throw new InvalidPathException("Expected number node");
    }

    /**
     * 是字符串节点
     *
     * @return boolean
     */
    public boolean isStringNode() {
        return false;
    }

    /**
     * 作为字符串节点
     *
     * @return {@link StringNode}
     */
    public StringNode asStringNode() {
        throw new InvalidPathException("Expected string node");
    }

    /**
     * 布尔节点
     *
     * @return boolean
     */
    public boolean isBooleanNode() {
        return false;
    }

    /**
     * 作为布尔节点
     *
     * @return {@link BooleanNode}
     */
    public BooleanNode asBooleanNode() {
        throw new InvalidPathException("Expected boolean node");
    }

    /**
     * json节点
     *
     * @return boolean
     */
    public boolean isJsonNode() {
        return false;
    }

    /**
     * 为json节点
     *
     * @return {@link JsonNode}
     */
    public JsonNode asJsonNode() {
        throw new InvalidPathException("Expected json node");
    }

    /**
     * 谓词节点
     *
     * @return boolean
     */
    public boolean isPredicateNode() {
        return false;
    }

    /**
     * 作为谓词节点
     *
     * @return {@link PredicateNode}
     */
    public PredicateNode asPredicateNode() {
        throw new InvalidPathException("Expected predicate node");
    }

    /**
     * 是值列表节点
     *
     * @return boolean
     */
    public boolean isValueListNode() {
        return false;
    }

    /**
     * 作为值列表节点
     *
     * @return {@link ValueListNode}
     */
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

    public static BaseValueNode toValueNode(Object o) {

        if (o == null) {
            return NULL_NODE;
        }
        if (o instanceof BaseValueNode) {
            return (BaseValueNode) o;
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
        if (c0 == SYMBOL_AT_CHAR || c0 == SYMBOL_DOLLAR_CHAR) {
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

    public static BaseValueNode createPathNode(Path path) {
        return new PathNode(path);
    }


}

