package com.chua.common.support.file.univocity.parsers.conversions;

/**
 * Converts Strings to Longs and vice versa
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class LongConversion extends ObjectConversion<Long> {
	/**
	 * Creates a Conversion from String to Long with default values to return when the input is null.
	 * This default constructor assumes the output of a conversion should be null when input is null
	 */
	public LongConversion() {
		super();
	}

	/**
	 * Creates a Conversion from String to Long with default values to return when the input is null.
	 *
	 * @param valueIfStringIsNull default Long value to be returned when the input String is null. Used when {@link ObjectConversion#execute(String)} is invoked.
	 * @param valueIfObjectIsNull default String value to be returned when a Long input is null. Used when {@code revert(Long)} is invoked.
	 */
	public LongConversion(Long valueIfStringIsNull, String valueIfObjectIsNull) {
		super(valueIfStringIsNull, valueIfObjectIsNull);
	}

	/**
	 * Converts a String to Long.
	 */
	@Override
	protected Long fromString(String input) {
		return Long.valueOf(input);
	}

}
