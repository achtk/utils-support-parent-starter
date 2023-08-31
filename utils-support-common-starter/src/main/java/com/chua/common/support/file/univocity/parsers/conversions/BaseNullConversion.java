package com.chua.common.support.file.univocity.parsers.conversions;

/**
 * Default implementation for conversions from input Objects of type <b>I</b> to output Objects of type <b>O</b>
 *
 * <p>Extending classes must implement a proper String to <b>T</b> conversion in {@link ObjectConversion#fromString(String)}
 * <p>This abstract class provides default results for conversions when the input is null.
 *
 * @param <I> The object type resulting from conversions of values of type <b>O</b>.
 * @param <O> The object type resulting from conversions of values of type <b>I</b>.
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */

public abstract class BaseNullConversion<I, O> implements Conversion<I, O> {

	private O valueOnNullInput;
	private I valueOnNullOutput;

	/**
	 * Creates a Conversion from an object to another object of a different type, with default values to return when the input is null.
	 * The default constructor assumes the output of a conversion should be null when input is null
	 */
	public BaseNullConversion() {
		this(null, null);
	}

	/**
	 * Creates a Conversion from an object to another object of a different type, with default values to return when the input is null.
	 *
	 * @param valueOnNullInput  default value of type <b>O</b> to be returned when the input object <b>I</b> is null. Used when {@link BaseNullConversion#execute(Object)} is invoked.
	 * @param valueOnNullOutput default value of type <b>I</b> to be returned when an input of type <b>I</b> is null. Used when {@link BaseNullConversion#revert(Object)} is invoked.
	 */
	public BaseNullConversion(O valueOnNullInput, I valueOnNullOutput) {
		this.valueOnNullInput = valueOnNullInput;
		this.valueOnNullOutput = valueOnNullOutput;
	}

	/**
	 * Converts the given instance of type <b>I</b> to an instance of <b>O</b>
	 *
	 * @param input the input value of type <b>I</b> to be converted to an object of type <b>O</b>
	 * @return the conversion result, or the value of {@link BaseNullConversion#valueOnNullInput} if the input object is null.
	 */
	@Override
	public O execute(I input) {
		if (input == null) {
			return valueOnNullInput;
		}
		return fromInput(input);
	}

	/**
	 * Creates an instance of <b>O</b> from a <b>I</b> object
	 *
	 * @param input The object of type <b>I</b> to be converted to <b>O</b>
	 * @return an instance of <b>O</b>, converted from the <b>I</b> input.
	 */
	protected abstract O fromInput(I input);

	/**
	 * Converts a value of type <b>O</b> back to a value of type <b>I</b>
	 *
	 * @param input the input of type <b>O</b> to be converted to an output <b>I</b>
	 * @return the conversion result, or the value of {@link BaseNullConversion#valueOnNullOutput} if the input object is null.
	 */
	@Override
	public I revert(O input) {
		if (input == null) {
			return valueOnNullOutput;
		}
		return undo(input);
	}

	/**
	 * Converts a value of type <b>O</b> back to <b>I</b>.
	 *
	 * @param input the input object to be converted to <b>I</b>
	 * @return the conversion result
	 */
	protected abstract I undo(O input);

	/**
	 * returns a default value of type <b>O</b> to be returned when the input of type <b>I</b> is null. Used when {@link BaseNullConversion#execute(Object)} is invoked.
	 *
	 * @return the default value of type <b>O</b> used when converting from a null <b>I</b>
	 */
	public O getValueOnNullInput() {
		return valueOnNullInput;
	}

	/**
	 * returns default instance of <b>I</b> to be returned when an input of type <b>O</b> is null. Used when {@link BaseNullConversion#revert(Object)} is invoked.
	 *
	 * @return the default <b>I</b> instance used when converting from a null <b>O</b>
	 */
	public I getValueOnNullOutput() {
		return valueOnNullOutput;
	}

	/**
	 * defines the default value of type <b>O</b> which should be returned when {@link BaseNullConversion#execute(Object)} is invoked with a null <b>I</b>..
	 *
	 * @param valueOnNullInput the default value of type <b>T</b> when converting from a null input
	 */
	public void setValueOnNullInput(O valueOnNullInput) {
		this.valueOnNullInput = valueOnNullInput;
	}

	/**
	 * defines the default value of type <b>I</b> which should be returned when {@link BaseNullConversion#revert(Object)} is invoked with a null <b>O</b>.
	 *
	 * @param valueOnNullOutput a default value of type <b>I</b> when converting from a null input
	 */
	public void setValueOnNullOutput(I valueOnNullOutput) {
		this.valueOnNullOutput = valueOnNullOutput;
	}
}
