
package com.chua.common.support.file.univocity.parsers.conversions;

/**
 * Converts any non-null object to its String representation.
 *
 * @author Administrator
 */
public class ToStringConversion extends NullConversion<Object, Object> {

    public ToStringConversion() {
    }

    public ToStringConversion(Object valueOnNullInput, Object valueOnNullOutput) {
        super(valueOnNullInput, valueOnNullOutput);
    }

    @Override
    protected Object fromInput(Object input) {
        if (input != null) {
            return input.toString();
        }
        return null;
    }

    @Override
    protected Object undo(Object input) {
        return execute(input);
    }
}
