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
package com.chua.common.support.file.univocity.parsers.common.processor.core;

import com.chua.common.support.file.univocity.parsers.common.BaseParser;
import com.chua.common.support.file.univocity.parsers.common.ArgumentUtils;
import com.chua.common.support.file.univocity.parsers.common.AbstractContext;
import com.chua.common.support.file.univocity.parsers.common.processor.MasterDetailRecord;
import com.chua.common.support.file.univocity.parsers.common.processor.ObjectRowListProcessor;
import com.chua.common.support.file.univocity.parsers.common.processor.RowPlacement;
import com.chua.common.support.file.univocity.parsers.conversions.Conversion;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Processor} implementation for associating rows extracted from any implementation of {@link BaseParser} into {@link MasterDetailRecord} instances.
 *
 * <p> For each row processed, a call to {@link AbstractMasterDetailProcessorAbstract#isMasterRecord(String[], AbstractContext)} will be made to identify whether or not it is a master row.
 * <p> The detail rows are automatically associated with the master record in an instance of {@link MasterDetailRecord}.
 * <p> When the master record is fully processed (i.e. {@link MasterDetailRecord} contains a master row and  all associated detail rows),
 * it is sent to the user for processing in {@link AbstractMasterDetailProcessorAbstract#masterDetailRecordProcessed(MasterDetailRecord, AbstractContext)}.
 *
 * <p> <b>Note</b> this class extends {@link AbstractObjectProcessorAbstract} and value conversions provided by {@link Conversion} instances are fully supported.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see MasterDetailRecord
 * @see RowPlacement
 * @see BaseParser
 * @see ObjectRowListProcessor
 * @see Processor
 */
public abstract class AbstractMasterDetailProcessorAbstract<T extends AbstractContext> extends AbstractObjectProcessorAbstract<T> {

	private final AbstractObjectListProcessorAbstract detailProcessor;
	private MasterDetailRecord record;
	private final boolean isMasterRowAboveDetail;

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
	public AbstractMasterDetailProcessorAbstract(RowPlacement rowPlacement, AbstractObjectListProcessorAbstract detailProcessor) {
		ArgumentUtils.noNulls("Row processor for reading detail rows", detailProcessor);
		this.detailProcessor = detailProcessor;
		this.isMasterRowAboveDetail = rowPlacement == RowPlacement.TOP;
	}

	/**
	 * Creates a MasterDetailProcessor assuming master records are positioned above its detail records in the input.
	 *
	 * @param detailProcessor the {@link AbstractObjectListProcessorAbstract} that processes detail rows.
	 */
	public AbstractMasterDetailProcessorAbstract(AbstractObjectListProcessorAbstract detailProcessor) {
		this(RowPlacement.TOP, detailProcessor);
	}

	@Override
	public void processStarted(T context) {
		detailProcessor.processStarted(context);
	}

	/**
	 * Invoked by the parser after all values of a valid record have been processed.
	 *
	 * <p>This method will then try to identify whether the given record is a master record.
	 * <p>If it is, any conversions applied to the fields of the master record will be executed;
	 * <p>Otherwise, the parsed row will be delegated to the {@link AbstractMasterDetailProcessorAbstract#detailProcessor} given in the constructor, and a detail record will be associated with the current {@link MasterDetailRecord}
	 *
	 * @param row     the data extracted by the parser for an individual record.
	 * @param context A contextual object with information and controls over the current state of the parsing process
	 */
	@Override
	public final void rowProcessed(String[] row, T context) {
		if (isMasterRecord(row, context)) {
			super.rowProcessed(row, context);
		} else {
			if (isMasterRowAboveDetail && record == null) {
				return;
			}
			detailProcessor.rowProcessed(row, context);
		}
	}

	/**
	 * Invoked by the parser after all values of a valid record have been processed and any conversions have been executed.
	 *
	 * @param row     the data extracted by the parser for an individual record.
	 * @param context A contextual object with information and controls over the current state of the parsing process
	 */
	@Override
	public final void rowProcessed(Object[] row, T context) {
		if (record == null) {
			record = new MasterDetailRecord();
			record.setMasterRow(row);

			if (isMasterRowAboveDetail) {
				return;
			}
		}

		processRecord(row, context);
	}

	/**
	 * Associates individual rows to a {@link MasterDetailRecord} and invokes {@link AbstractMasterDetailProcessorAbstract#masterDetailRecordProcessed(MasterDetailRecord, T)} when it is fully populated.
	 *
	 * @param row     a record extracted from the parser that had all (if any) conversions executed and is ready to be sent to the user.
	 * @param context A contextual object with information and controls over the current state of the parsing process
	 */
	private void processRecord(Object[] row, T context) {
		List<Object[]> detailRows = detailProcessor.getRows();
		record.setDetailRows(new ArrayList<Object[]>(detailRows));

		if (!isMasterRowAboveDetail) {
			record.setMasterRow(row);
		}

		if (record.getMasterRow() != null) {
			masterDetailRecordProcessed(record.clone(), context);
			record.clear();
		}

		detailRows.clear();

		if (isMasterRowAboveDetail) {
			record.setMasterRow(row);
		}
	}

	@Override
	public void processEnded(T context) {
		super.processEnded(context);
		detailProcessor.processEnded(context);

		if (isMasterRowAboveDetail) {
			processRecord(null, context);
		}
	}

	/**
	 * Queries whether or not the given row is a master record.
	 *
	 * @param row     the data extracted by the parser for an individual record.
	 * @param context A contextual object with information and controls over the current state of the parsing process
	 * @return true if the row is a master record, false if it is a detail record.
	 */
	protected abstract boolean isMasterRecord(String[] row, T context);

	/**
	 * Invoked by the processor after a master row and all associated detail rows have been processed.
	 *
	 * @param record  The master detail records
	 * @param context A contextual object with information and controls over the current state of the parsing process
	 */
	protected abstract void masterDetailRecordProcessed(MasterDetailRecord record, T context);
}
