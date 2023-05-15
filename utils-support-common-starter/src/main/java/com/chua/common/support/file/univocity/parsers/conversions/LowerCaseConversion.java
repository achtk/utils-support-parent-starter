package com.chua.common.support.file.univocity.parsers.conversions;

/**
 * Converts an input String to its lower case representation
 * <p>
 * The {@link LowerCaseConversion#revert(String)} implements the same behavior of {@link LowerCaseConversion#execute(String)}. Null inputs produce null outputs.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class LowerCaseConversion implements Conversion<String, String> {

    /**
     * Applies the toLowerCase operation in the input and returns the result.
     * Equivalent to {@link LowerCaseConversion#revert(String)}
     *
     * @param input the String to be converted to lower case
     * @return the lower case representation of the given input, or null if the input is null.
     */
    @Override
    public String execute(String input) {
        if (input == null) {
            return null;
        }
        return input.toLowerCase();
    }

    /**
     * Applies the toLowerCase operation in the input and returns the result.
     * Equivalent to {@link LowerCaseConversion#execute(String)}
     *
     * @param input the String to be converted to lower case
     * @return the lower case representation of the given input, or null if the input is null.
     */
    @Override
    public String revert(String input) {
        return execute(input);
    }

}
