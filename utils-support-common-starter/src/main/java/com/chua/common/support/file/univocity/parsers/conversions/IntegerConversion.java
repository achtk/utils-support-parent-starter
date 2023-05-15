package com.chua.common.support.file.univocity.parsers.conversions;

/**
 * Converts Strings to Integers and vice versa
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class IntegerConversion extends ObjectConversion<Integer> {
    /**
     * Creates a Conversion from String to Integer with default values to return when the input is null.
     * This default constructor assumes the output of a conversion should be null when input is null
     */
    public IntegerConversion() {
        super();
    }

    /**
     * Creates a Conversion from String to Integer with default values to return when the input is null.
     *
     * @param valueIfStringIsNull default Integer value to be returned when the input String is null. Used when {@link ObjectConversion#execute(String)} is invoked.
     * @param valueIfObjectIsNull default String value to be returned when a Integer input is null. Used when {@code revert(Integer)} is invoked.
     */
    public IntegerConversion(Integer valueIfStringIsNull, String valueIfObjectIsNull) {
        super(valueIfStringIsNull, valueIfObjectIsNull);
    }

    /**
     * Converts a String to Integer.
     */
    @Override
    protected Integer fromString(String input) {
        return Integer.valueOf(input);
    }

}
