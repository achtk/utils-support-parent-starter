package com.chua.common.support.file.univocity.parsers.conversions;

/**
 * Replaces contents of a given input String, identified by a regular expression, with a replacement String.
 * <p>
 * The {@link RegexConversion#revert(String)} implements the same behavior of {@link RegexConversion#execute(String)}. Null inputs produce null outputs.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class RegexConversion implements Conversion<String, String> {

    private final String replaceRegex;
    private final String replacement;

    /**
     * Creates a conversion that matches contents identified by the given regular expression and replaces them by the given replacement String.
     *
     * @param replaceRegex the regular expression used to match contents of a given input String
     * @param replacement  the replacement content to replace any contents matched by the given regular expression
     */
    public RegexConversion(String replaceRegex, String replacement) {
        this.replaceRegex = replaceRegex;
        this.replacement = replacement;
    }

    /**
     * Executes the regular expression provided in the constructor of this class against the input and replaces any matched content with the replacement String.
     * Equivalent to {@link RegexConversion#revert(String)}
     *
     * @param input The input to have contents matched by the regular expression and replaced
     * @return The String resulting from the content replacement
     */
    @Override
    public String execute(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll(replaceRegex, replacement);

    }

    /**
     * Executes the regular expression provided in the constructor of this class against the input and replaces any matched content with the replacement String.
     * Equivalent to {@link RegexConversion#execute(String)}
     *
     * @param input The input to have contents matched by the regular expression and replaced
     * @return The String resulting from the content replacement
     */
    @Override
    public String revert(String input) {
        return execute(input);
    }

}
