package com.chua.common.support.json.jsonpath.internal.function.numeric;

import static com.chua.common.support.constant.NumberConstant.ZERO_DOUBLE;

/**
 * Provides the average of a series of numbers in a JSONArray
 * <p>
 *
 * @author mattg
 * @date 6/26/15
 */
public class Average extends AbstractAggregation {

    private Double summation = 0d;
    private Double count = 0d;

    @Override
    protected void next(Number value) {
        count++;
        summation += value.doubleValue();
    }

    @Override
    protected Number getValue() {
        if (count != ZERO_DOUBLE) {
            return summation / count;
        }
        return ZERO_DOUBLE;
    }
}
