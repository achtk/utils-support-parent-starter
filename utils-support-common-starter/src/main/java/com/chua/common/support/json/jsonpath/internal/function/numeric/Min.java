package com.chua.common.support.json.jsonpath.internal.function.numeric;

/**
 * Defines the summation of a series of JSONArray numerical values
 * <p>
 *
 * @author mattg
 * @date 6/26/15
 */
public class Min extends AbstractAggregation {
    private Double min = Double.MAX_VALUE;

    @Override
    protected void next(Number value) {
        if (min > value.doubleValue()) {
            min = value.doubleValue();
        }
    }

    @Override
    protected Number getValue() {
        return min;
    }
}
