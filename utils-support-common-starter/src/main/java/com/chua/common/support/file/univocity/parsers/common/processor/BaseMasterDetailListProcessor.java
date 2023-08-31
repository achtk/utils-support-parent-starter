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
import com.chua.common.support.file.univocity.parsers.common.processor.core.AbstractMasterDetailListProcessorAbstract;
import com.chua.common.support.file.univocity.parsers.common.processor.core.AbstractObjectListProcessorAbstract;

/**
 * A convenience {@link BaseMasterDetailProcessor} implementation for storing all {@link MasterDetailRecord} generated form the parsed input into a list.
 * A typical use case of this class will be:
 *
 * <hr><blockquote><pre>{@code
 *
 * ObjectRowListProcessor detailProcessor = new ObjectRowListProcessor();
 * MasterDetailListProcessor masterRowProcessor = new MasterDetailListProcessor(detailProcessor) {
 *      protected boolean isMasterRecord(String[] row, ParsingContext context) {
 *          return "Total".equals(row[0]);
 *      }
 * };
 *
 * parserSettings.setRowProcessor(masterRowProcessor);
 *
 * List&lt;MasterDetailRecord&gt; rows = masterRowProcessor.getRecords();
 * }</pre></blockquote><hr>
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see BaseMasterDetailProcessor
 * @see RowProcessor
 * @see BaseParser
 */
public abstract class BaseMasterDetailListProcessor extends AbstractMasterDetailListProcessorAbstract<ParsingContext> implements RowProcessor {


	public BaseMasterDetailListProcessor(RowPlacement rowPlacement, AbstractObjectListProcessorAbstract detailProcessor) {
		super(rowPlacement, detailProcessor);
	}

	public BaseMasterDetailListProcessor(AbstractObjectListProcessorAbstract detailProcessor) {
		super(detailProcessor);
	}
}
