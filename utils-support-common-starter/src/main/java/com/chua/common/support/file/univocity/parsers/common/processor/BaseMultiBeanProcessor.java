/*
 * Copyright (c) 2015. Univocity Software Pty Ltd
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chua.common.support.file.univocity.parsers.common.processor;

import com.chua.common.support.file.univocity.parsers.common.AbstractContext;
import com.chua.common.support.file.univocity.parsers.common.BaseParser;
import com.chua.common.support.file.univocity.parsers.common.AbstractWriter;
import com.chua.common.support.file.univocity.parsers.common.ParsingContext;
import com.chua.common.support.file.univocity.parsers.common.processor.core.AbstractMultiBeanProcessor;

/**
 * A {@link RowProcessor} implementation for converting rows extracted from any implementation of {@link BaseParser} into java objects.
 *
 * <p>The class types passed to the constructor of this class must contain the annotations provided in {@link com.chua.common.support.file.univocity.parsers.annotations}.
 *
 * <p> For each row processed, one or more java bean instances of any given class will be created with their fields populated.
 * <p> Each individual instance will then be sent to the {@link BaseMultiBeanProcessor#beanProcessed(Class, Object, AbstractContext)} method, where the user can access the
 * beans parsed for each row.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see BaseParser
 * @see RowProcessor
 * @see AbstractBeanProcessor
 */
public abstract class BaseMultiBeanProcessor extends AbstractMultiBeanProcessor<ParsingContext> implements RowProcessor {

	/**
	 * Creates a processor for java beans of multiple types
	 *
	 * @param beanTypes the classes with their attributes mapped to fields of records parsed by an {@link BaseParser} or written by an {@link AbstractWriter}.
	 */
	public BaseMultiBeanProcessor(Class... beanTypes) {
		super(beanTypes);
	}
}
