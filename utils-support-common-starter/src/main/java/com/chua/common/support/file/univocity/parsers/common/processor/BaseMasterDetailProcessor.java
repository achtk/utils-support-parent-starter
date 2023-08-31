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
import com.chua.common.support.file.univocity.parsers.common.processor.core.AbstractMasterDetailProcessorAbstract;
import com.chua.common.support.file.univocity.parsers.conversions.Conversion;

/**
 * A {@link RowProcessor} implementation for associating rows extracted from any implementation of {@link BaseParser} into {@link MasterDetailRecord} instances.
 *
 * <p> For each row processed, a call to  will be made to identify whether or not it is a master row.
 * <p> The detail rows are automatically associated with the master record in an instance of {@link MasterDetailRecord}.
 * <p> When the master record is fully processed (i.e. {@link MasterDetailRecord} contains a master row and  all associated detail rows),
 *
 * <p> <b>Note</b> this class extends {@link BaseObjectRowProcessor} and value conversions provided by {@link Conversion} instances are fully supported.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see MasterDetailRecord
 * @see RowPlacement
 * @see BaseParser
 * @see ObjectRowListProcessor
 * @see RowProcessor
 */
public abstract class BaseMasterDetailProcessor extends AbstractMasterDetailProcessorAbstract<ParsingContext> {

	/**
	 * Creates a MasterDetailProcessor
	 *
	 * @param rowPlacement    indication whether the master records are placed in relation its detail records in the input.
	 *
	 *                        <hr><blockquote><pre>
	 *
	 *                        Master record (Totals)       Master record (Totals)
	 *                         above detail records         under detail records
	 *
	 *                           Totals | 100                 Item   | 60
	 *                           Item   | 60                  Item   | 40
	 *                           Item   | 40                  Totals | 100
	 *                        </pre></blockquote><hr>
	 * @param detailProcessor the {@link ObjectRowListProcessor} that processes detail rows.
	 */
	public BaseMasterDetailProcessor(RowPlacement rowPlacement, ObjectRowListProcessor detailProcessor) {
		super(rowPlacement, detailProcessor);
	}

	public BaseMasterDetailProcessor(ObjectRowListProcessor detailProcessor) {
		super(RowPlacement.TOP, detailProcessor);
	}

}
