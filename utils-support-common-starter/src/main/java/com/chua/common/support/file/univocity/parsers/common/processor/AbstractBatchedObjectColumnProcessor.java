/*******************************************************************************
 * Copyright 2014 Univocity Software Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.chua.common.support.file.univocity.parsers.common.processor;

import com.chua.common.support.file.univocity.parsers.common.BaseParser;
import com.chua.common.support.file.univocity.parsers.common.ParsingContext;
import com.chua.common.support.file.univocity.parsers.common.processor.core.AbstractBatchedColumnProcessor;
import com.chua.common.support.file.univocity.parsers.conversions.Conversion;

/**
 * A {@link RowProcessor} implementation for converting batches of rows extracted from any implementation of {@link BaseParser} into columns of objects.
 * <p>This uses the value conversions provided by {@link Conversion} instances.</p>
 *
 * <p> For each row processed, a sequence of conversions will be executed to generate the appropriate object. Each resulting object will then be stored in
 * a list that contains the values of the corresponding column. </p>
 *
 * <p> During the execution of the process, the {@link #batchProcessed(int)} method will be invoked after a given number of rows has been processed.</p>
 * <p> The user can access the lists with values parsed for all columns using the methods {@link #getColumnValuesAsList()},
 * {@link #getColumnValuesAsMapOfIndexes()} and {@link #getColumnValuesAsMapOfNames()}. </p>
 * <p> After {@link #batchProcessed(int)} is invoked, all values will be discarded and the next batch of column values will be accumulated.
 * This process will repeat until there's no more rows in the input.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see BaseParser
 * @see RowProcessor
 * @see AbstractBatchedColumnProcessor
 * @see Conversion
 */
public abstract class AbstractBatchedObjectColumnProcessor extends com.chua.common.support.file.univocity.parsers.common.processor.core.AbstractBatchedObjectColumnProcessorAbstract<ParsingContext> implements RowProcessor {


	/**
	 * Constructs a batched column processor configured to invoke the {@link #AbstractBatchedObjectColumnProcessor} method after a given number of rows has been processed.
	 *
	 * @param rowsPerBatch the number of rows to process in each batch.
	 */
	public AbstractBatchedObjectColumnProcessor(int rowsPerBatch) {
		super(rowsPerBatch);
	}

}
