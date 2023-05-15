package com.chua.common.support.file.univocity.parsers.conversions;

import com.chua.common.support.file.univocity.parsers.common.ArgumentUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Converts Strings to null and vice versa
 *
 * <p> This class supports multiple representations of null values. For example, you can define conversions from  different Strings such as "N/A, ?, -" to null.
 *
 * <p> The reverse conversion from a null to String (in {@link NullStringConversion#revert(Object)} will return the first String provided in this class constructor if the object is null.
 * <p> Using the previous example, a call to {@link NullStringConversion#revert(Object)} will produce "N/A".
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class NullStringConversion implements Conversion<Object, Object> {

	private final Set<String> nullStrings = new HashSet<String>();
	private final String defaultNullString;

	/**
	 * Creates conversions from Strings to null.
	 * <p>The list of Strings that identify nulls are mandatory.
	 *
	 * @param nullRepresentations Strings that identify a <i>true</i> value.  The first element will be returned when executing {@link NullStringConversion#revert(Object)}
	 */
	public NullStringConversion(String... nullRepresentations) {
		ArgumentUtils.noNulls("Null representation strings", nullRepresentations);
		Collections.addAll(nullStrings, nullRepresentations);
		this.defaultNullString = nullRepresentations[0];
	}

	/**
	 * Converts an Object to null. The string representation of the object will be used to match the string elements provided in the constructor.
	 *
	 * @param input an Object to be converted to null.
	 * @return null if the string representation of the object matches any one of the Strings provided in the constructor of this class. Otherwise, the original object will be returned.
	 */
	@Override
	public Object execute(Object input) {
		if (input == null) {
			return null;
		}
		if (nullStrings.contains(String.valueOf(input))) {
			return null;
		}
		return input;
	}

	/**
	 * Converts a null input to a String representation. The String returned will be the first element provided in the constructor of this class.
	 *
	 * @param input an Object that, if null, will be transformed to a String.
	 * @return If the input is null, the string representation for null objects. Otherwise, the original object will be returned.
	 */
	@Override
	public Object revert(Object input) {
		if (input == null) {
			return defaultNullString;
		} else {
			return input;
		}
	}

}
