package com.chua.common.support.file.univocity.parsers.common.processor;

import com.chua.common.support.file.univocity.parsers.annotations.helpers.MethodFilter;
import com.chua.common.support.file.univocity.parsers.common.AbstractWriter;
import com.chua.common.support.file.univocity.parsers.common.AbstractCommonSettings;
import com.chua.common.support.file.univocity.parsers.common.NormalizedString;
import com.chua.common.support.file.univocity.parsers.common.fields.FieldConversionMapping;
import com.chua.common.support.file.univocity.parsers.common.processor.core.BeanConversionProcessor;

/**
 * A {@link RowWriterProcessor} implementation for converting annotated java objects into object arrays suitable for writing in any implementation of {@link AbstractWriter}.
 * <p>The class type of the object must contain the annotations provided in {@link com.chua.common.support.file.univocity.parsers.annotations}.
 *
 * <p> For any given java bean instance, this processor will read and convert annotated fields into an object array.
 *
 * @param <T> the annotated class type.
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see AbstractWriter
 * @see RowWriterProcessor
 * @see BeanConversionProcessor
 */
public class BeanWriterProcessor<T> extends BeanConversionProcessor<T> implements RowWriterProcessor<T> {

	private NormalizedString[] normalizedHeaders;
	private String[] previousHeaders;


	/**
	 * Initializes the BeanWriterProcessor with the annotated bean class
	 *
	 * @param beanType the class annotated with one or more of the annotations provided in {@link com.chua.common.support.file.univocity.parsers.annotations}.
	 */
	public BeanWriterProcessor(Class<T> beanType) {
		super(beanType, MethodFilter.ONLY_GETTERS);
	}


	/**
	 * Converts the java bean instance into a sequence of values for writing.
	 *
	 * @param input          an instance of the type defined in this class constructor.
	 * @param headers        All field names used to produce records in a given destination. May be null if no headers have been defined in {@link AbstractCommonSettings#getHeaders()}
	 * @param indexesToWrite The indexes of the headers that are actually being written. May be null if no fields have been selected using {@link AbstractCommonSettings#selectFields(String...)} or {@link CommonSettings#selectIndexes(Integer...)}
	 * @return a row of objects containing the values extracted from the java bean
	 */
	public Object[] write(T input, String[] headers, int[] indexesToWrite) {
		if (previousHeaders != headers) {
			previousHeaders = headers;
			normalizedHeaders = NormalizedString.toArray(headers);
		}
		return write(input, normalizedHeaders, indexesToWrite);
	}

	/**
	 * Converts the java bean instance into a sequence of values for writing.
	 *
	 * @param input          an instance of the type defined in this class constructor.
	 * @param headers        All field names used to produce records in a given destination. May be null if no headers have been defined in {@link AbstractCommonSettings#getHeaders()}
	 * @param indexesToWrite The indexes of the headers that are actually being written. May be null if no fields have been selected using {@link AbstractCommonSettings#selectFields(String...)} or {@link CommonSettings#selectIndexes(Integer...)}
	 * @return a row of objects containing the values extracted from the java bean
	 */
	@Override
	public Object[] write(T input, NormalizedString[] headers, int[] indexesToWrite) {
		if (!initialized) {
			super.initialize(headers);
		}
		return reverseConversions(input, headers, indexesToWrite);
	}

	@Override
	protected FieldConversionMapping cloneConversions() {
		return null;
	}
}
