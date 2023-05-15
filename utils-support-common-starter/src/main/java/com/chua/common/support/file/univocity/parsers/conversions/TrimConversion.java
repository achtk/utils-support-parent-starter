package com.chua.common.support.file.univocity.parsers.conversions;

/**
 * Removes leading and trailing white spaces from an input String
 * <p>
 * The {@link TrimConversion#revert(String)} implements the same behavior of {@link TrimConversion#execute(String)}. Null inputs produce null outputs.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class TrimConversion implements Conversion<String, String> {

    private final int length;

    /**
     * Creates a trim conversion that removes leading and trailing whitespaces of any input String.
     */
    public TrimConversion() {
        this.length = -1;
    }

    /**
     * Creates a trim-to-length conversion that limits the length of any resulting String. Input Strings are trimmed, and
     * if the resulting String has more characters than the given limit, any characters over the given limit will be discarded.
     *
     * @param length the maximum number of characters of any String returned by this conversion.
     */
    public TrimConversion(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Maximum trim length must be positive");
        }
        this.length = length;
    }

    /**
     * Removes leading and trailing white spaces from the input and returns the result.
     * Equivalent to {@link TrimConversion#revert(String)}
     *
     * @param input the String to be trimmed
     * @return the input String without leading and trailing white spaces, or null if the input is null.
     */
    @Override
    public String execute(String input) {
        if (input == null) {
            return null;
        }
        if (input.length() == 0) {
            return input;
        }
        if (length != -1) {
            int begin = 0;
            while (begin < input.length() && input.charAt(begin) <= ' ') {
                begin++;
            }
            if (begin == input.length()) {
                return "";
            }

            int end = begin + (length < input.length() ? length : input.length()) - 1;
            if (end >= input.length()) {
                end = input.length() - 1;
            }

            while (input.charAt(end) <= ' ') {
                end--;
            }

            return input.substring(begin, end + 1);
        }
        return input.trim();
    }

    /**
     * Removes leading and trailing white spaces from the input and returns the result.
     * Equivalent to {@link TrimConversion#execute(String)}
     *
     * @param input the String to be trimmed
     * @return the input String without leading and trailing white spaces, or null if the input is null.
     */
    @Override
    public String revert(String input) {
        return execute(input);
    }

}
