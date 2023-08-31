package com.chua.common.support.json.jsonpath.internal.function.text;

import com.chua.common.support.json.jsonpath.internal.EvaluationContext;
import com.chua.common.support.json.jsonpath.internal.Path;
import com.chua.common.support.json.jsonpath.internal.PathRef;
import com.chua.common.support.json.jsonpath.internal.function.Parameter;
import com.chua.common.support.json.jsonpath.internal.function.PathFunction;
import com.chua.common.support.json.jsonpath.internal.path.CompiledPath;
import com.chua.common.support.json.jsonpath.internal.path.BasePathToken;
import com.chua.common.support.json.jsonpath.internal.path.RootPathToken;
import com.chua.common.support.json.jsonpath.internal.path.WildcardPathToken;

import java.util.List;

/**
 * Provides the length of a JSONArray Object
 * <p>
 *
 * @author mattg
 * @date 6/26/15
 */
public class Length implements PathFunction {

    public static final String TOKEN_NAME = "length";

    /**
     * When we calculate the length of a path, what we're asking is given the node we land on how many children does it
     * have.  Thus when we wrote the original query what we really wanted was $..book.length() or $.length($..book.*)
     *
     * @param currentPath The current path location inclusive of the function name
     * @param parent      The path location above the current function
     * @param model       The JSON model as input to this particular function
     * @param ctx         Eval context, state bag used as the path is traversed, maintains the result of executing
     * @param parameters
     * @return
     */
    @Override
    public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx, List<Parameter> parameters) {
        if (null != parameters && parameters.size() > 0) {

            // Set the tail of the first parameter, when its not a function path parameter (which wouldn't make sense
            // for length - to the wildcard such that we request all of its children so we can get back an array and
            // take its length.
            Parameter lengthOfParameter = parameters.get(0);
            if (!lengthOfParameter.getPath().isFunctionPath()) {
                Path path = lengthOfParameter.getPath();
                if (path instanceof CompiledPath) {
                    RootPathToken root = ((CompiledPath) path).getRoot();
                    BasePathToken tail = root.getNext();
                    while (null != tail && null != tail.getNext()) {
                        tail = tail.getNext();
                    }
                    if (null != tail) {
                        tail.setNext(new WildcardPathToken());
                    }
                }
            }
            Object innerModel = parameters.get(0).getPath().evaluate(model, model, ctx.configuration()).getValue();
            if (ctx.configuration().jsonProvider().isArray(innerModel)) {
                return ctx.configuration().jsonProvider().length(innerModel);
            }
        }
        if (ctx.configuration().jsonProvider().isArray(model)) {
            return ctx.configuration().jsonProvider().length(model);
        } else if (ctx.configuration().jsonProvider().isMap(model)) {
            return ctx.configuration().jsonProvider().length(model);
        }
        return null;
    }
}