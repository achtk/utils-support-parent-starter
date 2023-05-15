package com.chua.common.support.file.univocity.parsers.conversions;

/**
 * Converts Strings to Shorts and vice versa
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class ShortConversion extends ObjectConversion<Short> {
	/**
	 * Creates a Conversion from String to Short with default values to return when the input is null.
	 * This default constructor assumes the output of a conversion should be null when input is null
	 */
	public ShortConversion() {
		super();
	}

	/**
	 * Creates a Conversion from String to Short with default values to return when the input is null.
	 *
	 * @param valueIfStringIsNull default Short value to be returned when the input String is null. Used when {@link ShortConversion#execute(String)} is invoked.
	 * @param valueIfObjectIsNull default String value to be returned when a Short input is null. Used when {@code revert(Short)} is invoked.
	 */
	public ShortConversion(Short valueIfStringIsNull, String valueIfObjectIsNull) {
		super(valueIfStringIsNull, valueIfObjectIsNull);
	}

	/**
	 * Converts a String to Short.
	 */
	@Override
	protected Short fromString(String input) {
		return Short.valueOf(input);
	}

}
