package com.chua.common.support.file.univocity.parsers.common.processor;

import com.chua.common.support.file.univocity.parsers.common.AbstractContext;
import com.chua.common.support.file.univocity.parsers.common.BaseParser;
import com.chua.common.support.file.univocity.parsers.common.AbstractWriter;
import com.chua.common.support.file.univocity.parsers.common.ParsingContext;
import com.chua.common.support.file.univocity.parsers.common.processor.core.AbstractBeanListProcessorAbstract;

import java.util.List;

/**
 * A convenience {@link AbstractBeanProcessor} implementation for storing all java objects generated form the parsed input into a list.
 * A typical use case of this class will be:
 *
 * <hr><blockquote><pre>{@code
 *
 * parserSettings.setRowProcessor(new BeanListProcessor(MyObject.class));
 * parser.parse(reader); // will invoke the {@link BeanListProcessor#beanProcessed(Object, AbstractContext)} method for each generated object.
 *
 * List&lt;T&gt; beans = rowProcessor.getBeans();
 * }</pre></blockquote><hr>
 *
 * @param <T> the annotated class type.
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see AbstractBeanProcessor
 * @see RowProcessor
 * @see BaseParser
 * @see AbstractBeanListProcessorAbstract
 */
public class BeanListProcessor<T> extends AbstractBeanListProcessorAbstract<T, ParsingContext> implements RowProcessor {

	/**
	 * Creates a processor that stores java beans of a given type into a list
	 *
	 * @param beanType the class with its attributes mapped to fields of records parsed by an {@link BaseParser} or written by an {@link AbstractWriter}.
	 */
	public BeanListProcessor(Class<T> beanType) {
		super(beanType);
	}

	/**
	 * Creates a processor that stores java beans of a given type into a list
	 *
	 * @param beanType          the class with its attributes mapped to fields of records parsed by an {@link BaseParser} or written by an {@link AbstractWriter}.
	 * @param expectedBeanCount expected number of rows to be parsed from the input which will be converted into java beans.
	 *                          Used to pre-allocate the size of the output {@link List}
	 *                          returned by {@link #getBeans()}
	 */
	public BeanListProcessor(Class<T> beanType, int expectedBeanCount) {
		super(beanType, expectedBeanCount);
	}

}
