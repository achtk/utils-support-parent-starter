package com.chua.common.support.file.univocity.parsers.conversions;

/**
 * Converts an input String to its upper case representation
 * <p>
 * The {@link UpperCaseConversion#revert(String)} implements the same behavior of {@link UpperCaseConversion#execute(String)}. Null inputs produce null outputs.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class UpperCaseConversion implements Conversion<String, String> {

    /**
     * Applies the toUpperCase operation in the input and returns the result.
     * Equivalent to {@link UpperCaseConversion#revert(String)}
     *
     * @param input the String to be converted to upper case
     * @return the upper case representation of the given input, or null if the input is null.
     */
    @Override
    public String execute(String input) {
        if (input == null) {
            return null;
        }
        return input.toUpperCase();
    }

    /**
     * Applies the toUpperCase operation in the input and returns the result.
     * Equivalent to {@link UpperCaseConversion#execute(String)}
     *
     * @param input the String to be converted to upper case
     * @return the upper case representation of the given input, or null if the input is null.
     */
    @Override
    public String revert(String input) {
        return execute(input);
    }

}
