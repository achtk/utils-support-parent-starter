package com.chua.common.support.file.univocity.parsers.common.processor.core;

import com.chua.common.support.file.univocity.parsers.annotations.Parsed;
import com.chua.common.support.file.univocity.parsers.annotations.helpers.MethodFilter;
import com.chua.common.support.file.univocity.parsers.common.AbstractContext;
import com.chua.common.support.file.univocity.parsers.common.BaseParser;
import com.chua.common.support.file.univocity.parsers.common.AbstractWriter;
import com.chua.common.support.file.univocity.parsers.common.NormalizedString;

/**
 * A {@link Processor} implementation for converting rows extracted from any implementation of {@link BaseParser} into java objects.
 * <p>The class type of the object must contain the annotations provided in {@link com.chua.common.support.file.univocity.parsers.annotations}.
 *
 * <p> For each row processed, a java bean instance of a given class will be created with its fields populated.
 * <p> This instance will then be sent to the {@link AbstractBeanProcessorAbstract#beanProcessed(Object, AbstractContext)} method, where the user can access it.
 *
 * @param <T> the annotated class type.
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see BaseParser
 * @see Processor
 */
public abstract class AbstractBeanProcessorAbstract<T, C extends AbstractContext> extends BeanConversionProcessorAbstract<T> implements Processor<C> {

	/**
	 * Creates a processor for java beans of a given type.
	 *
	 * @param beanType     the class with its attributes mapped to fields of records parsed by an {@link BaseParser} or written by an {@link AbstractWriter}.
	 * @param methodFilter filter to apply over annotated methods when the processor is reading data from beans (to write values to an output)
	 *                     or writing values into beans (when parsing). It is used to choose either a "get" or a "set"
	 *                     method annotated with {@link Parsed}, when both methods target the same field.
	 */
	public AbstractBeanProcessorAbstract(Class<T> beanType, MethodFilter methodFilter) {
		super(beanType, methodFilter);
	}

	/**
	 * Converts a parsed row to a java object
	 */
	@Override
	public final void rowProcessed(String[] row, C context) {
		T instance = createBean(row, context);
		if (instance != null) {
			beanProcessed(instance, context);
		}
	}

	/**
	 * Invoked by the processor after all values of a valid record have been processed and converted into a java object.
	 *
	 * @param bean    java object created with the information extracted by the parser for an individual record.
	 * @param context A contextual object with information and controls over the current state of the parsing process
	 */
	public abstract void beanProcessed(T bean, C context);

	@Override
	public void processStarted(C context) {
		super.initialize(NormalizedString.toArray(context.headers()));
	}

	@Override
	public void processEnded(C context) {
	}
}
