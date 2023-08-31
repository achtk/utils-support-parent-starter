package com.chua.common.support.file.univocity.parsers.common.processor;

import com.chua.common.support.file.univocity.parsers.annotations.helpers.MethodFilter;
import com.chua.common.support.file.univocity.parsers.common.AbstractContext;
import com.chua.common.support.file.univocity.parsers.common.BaseParser;
import com.chua.common.support.file.univocity.parsers.common.AbstractWriter;
import com.chua.common.support.file.univocity.parsers.common.ParsingContext;

/**
 * A {@link RowProcessor} implementation for converting rows extracted from any implementation of {@link BaseParser} into java objects.
 * <p>The class type of the object must contain the annotations provided in {@link com.chua.common.support.file.univocity.parsers.annotations}.
 *
 * <p> For each row processed, a java bean instance of a given class will be created with its fields populated.
 * <p> This instance will then be sent to the {@link com.chua.common.support.file.univocity.parsers.common.processor.core.AbstractBeanProcessorAbstract#beanProcessed(Object, AbstractContext)} method, where the user can access it.
 *
 * @param <T> the annotated class type.
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see BaseParser
 * @see RowProcessor
 * @see com.chua.common.support.file.univocity.parsers.common.processor.core.AbstractBeanProcessorAbstract
 */
public abstract class AbstractBeanProcessor<T> extends com.chua.common.support.file.univocity.parsers.common.processor.core.AbstractBeanProcessorAbstract<T, ParsingContext> implements RowProcessor {

	/**
	 * Creates a processor for java beans of a given type.
	 *
	 * @param beanType the class with its attributes mapped to fields of records parsed by an {@link BaseParser} or written by an {@link AbstractWriter}.
	 */
	public AbstractBeanProcessor(Class<T> beanType) {
		super(beanType, MethodFilter.ONLY_SETTERS);
	}


}
