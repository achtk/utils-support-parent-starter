package com.chua.common.support.json.jsonpath.internal.function;

import com.chua.common.support.json.jsonpath.internal.EvaluationContext;
import com.chua.common.support.json.jsonpath.internal.PathRef;

import java.util.List;

/**
 * Defines the default behavior which is to return the model that is provided as input as output
 * <p>
 *
 * @author mattg
 * @date 6/26/15
 */
public class PassthruPathFunction implements PathFunction {

    @Override
    public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx, List<Parameter> parameters) {
        return model;
    }
}
