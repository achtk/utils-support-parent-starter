package com.chua.common.support.file.univocity.parsers.common.processor.core;

import com.chua.common.support.file.univocity.parsers.common.BaseParser;
import com.chua.common.support.file.univocity.parsers.common.AbstractContext;

import java.util.List;
import java.util.Map;

/**
 * A simple {@link Processor} implementation that stores values of columns.
 * Values parsed in each row will be split into columns of Strings. Each column has its own list of values.
 *
 * <p> At the end of the process, the user can access the lists with values parsed for all columns using the methods {@link #getColumnValuesAsList()},
 * {@link #getColumnValuesAsMapOfIndexes()} and {@link #getColumnValuesAsMapOfNames()}. </p>
 *
 *
 * <p><b>Note:</b> Storing the values of all columns may be memory intensive. For large inputs, use a {@link AbstractBatchedColumnProcessor} instead</p>
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see BaseParser
 * @see Processor
 * @see ColumnReader
 */
public abstract class AbstractColumnProcessor<T extends AbstractContext> implements Processor<T>, ColumnReader<String> {

	private final ColumnSplitter<String> splitter;

	/**
	 * Constructs a column processor, pre-allocating room for 1000 rows.
	 */
	public AbstractColumnProcessor() {
		this(1000);
	}

	/**
	 * Constructs a column processor pre-allocating room for the expected number of rows to be processed
	 *
	 * @param expectedRowCount the expected number of rows to be processed
	 */
	public AbstractColumnProcessor(int expectedRowCount) {
		splitter = new ColumnSplitter<String>(expectedRowCount);
	}

	@Override
	public void processStarted(T context) {
		splitter.reset();
	}

	@Override
	public void rowProcessed(String[] row, T context) {
		splitter.addValuesToColumns(row, context);
	}

	@Override
	public void processEnded(T context) {
	}

	@Override
	public final String[] getHeaders() {
		return splitter.getHeaders();
	}

	@Override
	public final List<List<String>> getColumnValuesAsList() {
		return splitter.getColumnValues();
	}

	@Override
	public final void putColumnValuesInMapOfNames(Map<String, List<String>> map) {
		splitter.putColumnValuesInMapOfNames(map);
	}

	@Override
	public final void putColumnValuesInMapOfIndexes(Map<Integer, List<String>> map) {
		splitter.putColumnValuesInMapOfIndexes(map);
	}

	@Override
	public final Map<String, List<String>> getColumnValuesAsMapOfNames() {
		return splitter.getColumnValuesAsMapOfNames();
	}

	@Override
	public final Map<Integer, List<String>> getColumnValuesAsMapOfIndexes() {
		return splitter.getColumnValuesAsMapOfIndexes();
	}

	@Override
	public List<String> getColumn(String columnName) {
		return splitter.getColumnValues(columnName, String.class);
	}

	@Override
	public List<String> getColumn(int columnIndex) {
		return splitter.getColumnValues(columnIndex, String.class);
	}
}
