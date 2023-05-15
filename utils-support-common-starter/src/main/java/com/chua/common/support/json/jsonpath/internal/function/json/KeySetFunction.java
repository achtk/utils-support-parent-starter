package com.chua.common.support.json.jsonpath.internal.function.json;

import com.chua.common.support.json.jsonpath.internal.EvaluationContext;
import com.chua.common.support.json.jsonpath.internal.PathRef;
import com.chua.common.support.json.jsonpath.internal.function.Parameter;
import com.chua.common.support.json.jsonpath.internal.function.PathFunction;

import java.util.List;

/**
 * Author: Sergey Saiyan sergey.sova42@gmail.com
 * Created at 21/02/2018.
 *
 * @author Administrator
 */
public class KeySetFunction implements PathFunction {

    @Override
    public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx, List<Parameter> parameters) {
        if (ctx.configuration().jsonProvider().isMap(model)) {
            return ctx.configuration().jsonProvider().getPropertyKeys(model);
        }
        return null;
    }
}
