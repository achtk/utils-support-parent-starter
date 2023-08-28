package com.chua.common.support.file.univocity.parsers.common.processor.core;

import com.chua.common.support.file.univocity.parsers.common.AbstractParser;
import com.chua.common.support.file.univocity.parsers.common.AbstractCommonSettings;
import com.chua.common.support.file.univocity.parsers.common.AbstractContext;
import com.chua.common.support.file.univocity.parsers.common.processor.MasterDetailRecord;
import com.chua.common.support.file.univocity.parsers.common.processor.ObjectRowListProcessor;
import com.chua.common.support.file.univocity.parsers.common.processor.RowPlacement;

import java.util.ArrayList;
import java.util.List;

/**
 * A convenience {@link Processor} implementation for storing all {@link MasterDetailRecord} generated form the parsed input into a list.
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
 * @see AbstractMasterDetailProcessor
 * @see Processor
 * @see AbstractParser
 */
public abstract class AbstractMasterDetailListProcessor<T extends AbstractContext> extends AbstractMasterDetailProcessor<T> {

	private final List<MasterDetailRecord> records = new ArrayList<MasterDetailRecord>();
	private String[] headers;

	/**
	 * Creates a MasterDetailListProcessor
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
	public AbstractMasterDetailListProcessor(RowPlacement rowPlacement, AbstractObjectListProcessor detailProcessor) {
		super(rowPlacement, detailProcessor);
	}

	/**
	 * Creates a MasterDetailListProcessor assuming master records are positioned above its detail records in the input.
	 *
	 * @param detailProcessor the {@link ObjectRowListProcessor} that processes detail rows.
	 */
	public AbstractMasterDetailListProcessor(AbstractObjectListProcessor detailProcessor) {
		super(detailProcessor);
	}

	/**
	 * Stores the generated {@link MasterDetailRecord} with the set of associated parsed records into a list.
	 *
	 * @param record  {@link MasterDetailRecord} generated with a set of associated records extracted by the parser
	 * @param context A contextual object with information and controls over the current state of the parsing process
	 * @see MasterDetailRecord
	 */
	@Override
	protected void masterDetailRecordProcessed(MasterDetailRecord record, T context) {
		records.add(record);
	}

	@Override
	public void processEnded(T context) {
		headers = context.headers();
		super.processEnded(context);
	}

	/**
	 * Returns the list of generated MasterDetailRecords at the end of the parsing process.
	 *
	 * @return the list of generated MasterDetailRecords at the end of the parsing process.
	 */
	public List<MasterDetailRecord> getRecords() {
		return this.records;
	}

	/**
	 * Returns the record headers. This can be either the headers defined in {@link AbstractCommonSettings#getHeaders()} or the headers parsed in the file when {@link CommonSettings#getHeaders()}  equals true
	 *
	 * @return the headers of all records parsed.
	 */
	public String[] getHeaders() {
		return headers;
	}
}
