package com.chua.common.support.json.jsonpath.internal.path;

import com.chua.common.support.json.jsonpath.Predicate;
import com.chua.common.support.json.jsonpath.internal.function.Parameter;

import java.util.Collection;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * @author Administrator
 */
public class PathTokenFactory {

    public static RootPathToken createRootPathToken(char token) {
        return new RootPathToken(token);
    }

    public static BasePathToken createSinglePropertyPathToken(String property, char stringDelimiter) {
        return new PropertyPathToken(singletonList(property), stringDelimiter);
    }

    public static BasePathToken createPropertyPathToken(List<String> properties, char stringDelimiter) {
        return new PropertyPathToken(properties, stringDelimiter);
    }

    public static BasePathToken createSliceArrayPathToken(final ArraySliceOperation arraySliceOperation) {
        return new ArraySliceToken(arraySliceOperation);
    }

    public static BasePathToken createIndexArrayPathToken(final ArrayIndexOperation arrayIndexOperation) {
        return new ArrayIndexToken(arrayIndexOperation);
    }

    public static BasePathToken createWildCardPathToken() {
        return new WildcardPathToken();
    }

    public static BasePathToken crateScanToken() {
        return new ScanPathToken();
    }

    public static BasePathToken createPredicatePathToken(Collection<Predicate> predicates) {
        return new PredicatePathToken(predicates);
    }

    public static BasePathToken createPredicatePathToken(Predicate predicate) {
        return new PredicatePathToken(predicate);
    }

    public static BasePathToken createFunctionPathToken(String function, List<Parameter> parameters) {
        return new FunctionPathToken(function, parameters);
    }
}
