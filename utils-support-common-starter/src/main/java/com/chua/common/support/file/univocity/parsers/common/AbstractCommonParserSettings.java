package com.chua.common.support.file.univocity.parsers.common;

import com.chua.common.support.file.univocity.parsers.annotations.Headers;
import com.chua.common.support.file.univocity.parsers.annotations.helpers.AnnotationHelper;
import com.chua.common.support.file.univocity.parsers.annotations.helpers.MethodFilter;
import com.chua.common.support.file.univocity.parsers.common.fields.FieldSelector;
import com.chua.common.support.file.univocity.parsers.common.fields.FieldSet;
import com.chua.common.support.file.univocity.parsers.common.input.*;
import com.chua.common.support.file.univocity.parsers.common.input.concurrent.ConcurrentCharInputReader;
import com.chua.common.support.file.univocity.parsers.common.processor.NoopRowProcessor;
import com.chua.common.support.file.univocity.parsers.common.processor.RowProcessor;
import com.chua.common.support.file.univocity.parsers.common.processor.core.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This is the parent class for all configuration classes used by parsers ({@link AbstractParser})
 *
 * <p>By default, all parsers work with, at least, the following configuration options in addition to the ones provided by {@link AbstractCommonSettings}:
 *
 * <ul>
 * <li><b>rowProcessor:</b> a callback implementation of the interface {@link RowProcessor} which handles the life cycle of the parsing process and processes each record extracted from the input</li>
 * <li><b>headerExtractionEnabled <i>(defaults to false)</i>:</b> indicates whether or not the first valid record parsed from the input should be considered as the row containing the names of each column</li>
 * <li><b>columnReorderingEnabled <i>(defaults to true)</i>:</b> indicates whether fields selected using the field selection methods (defined by the parent class {@link AbstractCommonSettings}) should be reordered.
 * <p>When disabled, each parsed record will contain values for all columns, in the order they occur in the input. Fields which were not selected will not be parsed but and the record will contain empty values.
 * <p>When enabled, each parsed record will contain values only for the selected columns. The values will be ordered according to the selection.
 * <li><b>inputBufferSize <i>(defaults to 1024*1024 characters)</i>:</b> The number of characters held by the parser's buffer when processing the input.
 * <li><b>readInputOnSeparateThread <i>(defaults true if the number of available processors at runtime is greater than 1)</i>:</b>
 * <p>When enabled, a reading thread (in {@code input.concurrent.ConcurrentCharInputReader}) will be started and load characters from the input, while the parser is processing its input buffer.
 * This yields better performance, especially when reading from big input (greater than 100 mb)
 * <p>When disabled, the parsing process will briefly pause so the buffer can be replenished every time it is exhausted (in {@link DefaultCharInputReader} it is not as bad or slow as it sounds, and can even be (slightly) more efficient if your input is small)
 * <li><b>numberOfRecordsToRead <i>(defaults to -1)</i>:</b> Defines how many (valid) records are to be parsed before the process is stopped. A negative value indicates there's no limit.</li>
 * <li><b>lineSeparatorDetectionEnabled <i>(defaults to false)</i>:</b> Attempts to identify what is the line separator being used in the input.
 * The first row of the input will be read until a sequence of '\r\n', or characters '\r' or '\n' is found. If a match is found, then it will be used as the line separator to use to parse the input</li>
 * </ul>
 *
 * @param <F> the format supported by this parser.
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see com.chua.common.support.file.univocity.parsers.common.processor.RowProcessor
 * @see com.chua.common.support.file.univocity.parsers.csv.CsvParserSettings
 * @see com.chua.common.support.file.univocity.parsers.fixed.FixedWidthParserSettings
 */
public abstract class AbstractCommonParserSettings<F extends Format> extends AbstractCommonSettings<F> {

    protected Boolean headerExtractionEnabled = null;
    private Processor<? extends AbstractContext> processor;
    private boolean columnReorderingEnabled = true;
    private int inputBufferSize = 1024 * 1024;
    private boolean readInputOnSeparateThread = Runtime.getRuntime().availableProcessors() > 1;
    private long numberOfRecordsToRead = -1L;
    private boolean lineSeparatorDetectionEnabled = false;
    private long numberOfRowsToSkip = 0L;
    private boolean commentCollectionEnabled = false;
    private boolean autoClosingEnabled = true;
    private boolean commentProcessingEnabled = true;
    private List<InputAnalysisProcess> inputAnalysisProcesses = new ArrayList<InputAnalysisProcess>();

    /**
     * Indicates whether or not a separate thread will be used to read characters from the input while parsing (defaults true if the number of available
     * processors at runtime is greater than 1)
     * <p>When enabled, a reading thread (in {@code com.chua.common.support.file.univocity.parsers.common.input.concurrent.ConcurrentCharInputReader})
     * will be started and load characters from the input, while the parser is processing its input buffer.
     * This yields better performance, especially when reading from big input (greater than 100 mb)
     * <p>When disabled, the parsing process will briefly pause so the buffer can be replenished every time it is exhausted
     * (in {@link DefaultCharInputReader} it is not as bad or slow as it sounds, and can even be (slightly) more efficient if your input is small)
     *
     * @return true if the input should be read on a separate thread, false otherwise
     */
    public boolean getReadInputOnSeparateThread() {
        return readInputOnSeparateThread;
    }

    /**
     * Defines whether or not a separate thread will be used to read characters from the input while parsing (defaults true if the number of available
     * processors at runtime is greater than 1)
     * <p>When enabled, a reading thread (in {@code com.chua.common.support.file.univocity.parsers.common.input.concurrent.ConcurrentCharInputReader}) will be
     * started and load characters from the input, while the
     * parser is processing its input buffer. This yields better performance, especially when reading from big input (greater than 100 mb)
     * <p>When disabled, the parsing process will briefly pause so the buffer can be replenished every time it is exhausted (in {@link DefaultCharInputReader}
     * it is not as bad or slow as it sounds, and can even be (slightly) more efficient if your input is small)
     *
     * @param readInputOnSeparateThread the flag indicating whether or not the input should be read on a separate thread
     */
    public void setReadInputOnSeparateThread(boolean readInputOnSeparateThread) {
        this.readInputOnSeparateThread = readInputOnSeparateThread;
    }

    /**
     * Indicates whether or not the first valid record parsed from the input should be considered as the row containing the names of each column
     *
     * @return true if the first valid record parsed from the input should be considered as the row containing the names of each column, false otherwise
     */
    public boolean isHeaderExtractionEnabled() {
        return headerExtractionEnabled == null ? false : headerExtractionEnabled;
    }

    /**
     * Defines whether or not the first valid record parsed from the input should be considered as the row containing the names of each column
     *
     * @param headerExtractionEnabled a flag indicating whether the first valid record parsed from the input should be considered as the row containing the
     *                                names of each column
     */
    public void setHeaderExtractionEnabled(boolean headerExtractionEnabled) {
        this.headerExtractionEnabled = headerExtractionEnabled;
    }

    /**
     * Returns the callback implementation of the interface {@link RowProcessor} which handles the lifecycle of the parsing process and processes each record
     * extracted from the input
     *
     * @return Returns the RowProcessor used by the parser to handle each record
     * @see com.chua.common.support.file.univocity.parsers.common.processor.ObjectRowProcessor
     * @see com.chua.common.support.file.univocity.parsers.common.processor.ObjectRowListProcessor
     * @see com.chua.common.support.file.univocity.parsers.common.processor.MasterDetailProcessor
     * @see com.chua.common.support.file.univocity.parsers.common.processor.MasterDetailListProcessor
     * @see com.chua.common.support.file.univocity.parsers.common.processor.BeanProcessor
     * @see com.chua.common.support.file.univocity.parsers.common.processor.BeanListProcessor
     */
    public RowProcessor getRowProcessor() {
        if (processor == null) {
            return NoopRowProcessor.INSTANCE;
        }
        return (RowProcessor) processor;
    }


    /**
     * Defines the callback implementation of the interface {@link RowProcessor} which handles the lifecycle of the parsing process and processes each record
     * extracted from the input
     *
     * @param processor the RowProcessor instance which should used by the parser to handle each record
     * @see com.chua.common.support.file.univocity.parsers.common.processor.ObjectRowProcessor
     * @see com.chua.common.support.file.univocity.parsers.common.processor.ObjectRowListProcessor
     * @see com.chua.common.support.file.univocity.parsers.common.processor.MasterDetailProcessor
     * @see com.chua.common.support.file.univocity.parsers.common.processor.MasterDetailListProcessor
     * @see com.chua.common.support.file.univocity.parsers.common.processor.BeanProcessor
     * @see com.chua.common.support.file.univocity.parsers.common.processor.BeanListProcessor
     *  Use the {@link #setProcessor(Processor)} method as it allows format-specific processors to be built to work with different implementations of
     * {@link AbstractContext}.
     * Implementations based on {@link RowProcessor} allow only parsers who provide a {@link ParsingContext} to be used.
     */
    public void setRowProcessor(RowProcessor processor) {
        this.processor = processor;
    }


    /**
     * Returns the callback implementation of the interface {@link Processor} which handles the lifecycle of the parsing process and processes each record
     * extracted from the input
     *
     * @param <T> the context type supported by the parser implementation.
     * @return Returns the {@link Processor} used by the parser to handle each record
     * @see AbstractObjectProcessor
     * @see AbstractObjectListProcessor
     * @see AbstractMasterDetailProcessor
     * @see AbstractMasterDetailListProcessor
     * @see AbstractBeanProcessor
     * @see AbstractBeanListProcessor
     */
    public <T extends AbstractContext> Processor<T> getProcessor() {
        if (processor == null) {
            return NoopProcessor.INSTANCE;
        }
        return (Processor<T>) processor;
    }

    /**
     * Defines the callback implementation of the interface {@link Processor} which handles the lifecycle of the parsing process and processes each record
     * extracted from the input
     *
     * @param processor the {@link Processor} instance which should used by the parser to handle each record
     * @see AbstractObjectProcessor
     * @see AbstractObjectListProcessor
     * @see AbstractMasterDetailProcessor
     * @see AbstractMasterDetailListProcessor
     * @see AbstractBeanProcessor
     * @see AbstractBeanListProcessor
     * @see AbstractColumnProcessor
     * @see AbstractColumnProcessor
     */
    public void setProcessor(Processor<? extends AbstractContext> processor) {
        this.processor = processor;
    }

    /**
     * An implementation of {@link CharInputReader} which loads the parser buffer in parallel or sequentially, as defined by the readInputOnSeparateThread
     * property
     *
     * @param whitespaceRangeStart starting range of characters considered to be whitespace.
     * @return The input reader as chosen with the readInputOnSeparateThread property.
     */
    protected CharInputReader newCharInputReader(int whitespaceRangeStart) {
        if (readInputOnSeparateThread) {
            if (lineSeparatorDetectionEnabled) {
                return new ConcurrentCharInputReader(getFormat().getNormalizedNewline(), this.getInputBufferSize(), 10, whitespaceRangeStart, autoClosingEnabled);
            } else {
                return new ConcurrentCharInputReader(getFormat().getLineSeparator(), getFormat().getNormalizedNewline(), this.getInputBufferSize(), 10, whitespaceRangeStart, autoClosingEnabled);
            }
        } else {
            if (lineSeparatorDetectionEnabled) {
                return new DefaultCharInputReader(getFormat().getNormalizedNewline(), this.getInputBufferSize(), whitespaceRangeStart, autoClosingEnabled);
            } else {
                return new DefaultCharInputReader(getFormat().getLineSeparator(), getFormat().getNormalizedNewline(), this.getInputBufferSize(), whitespaceRangeStart, autoClosingEnabled);
            }
        }
    }

    /**
     * The number of valid records to be parsed before the process is stopped. A negative value indicates there's no limit (defaults to -1).
     *
     * @return the number of records to read before stopping the parsing process.
     */
    public long getNumberOfRecordsToRead() {
        return numberOfRecordsToRead;
    }

    /**
     * Defines the number of valid records to be parsed before the process is stopped. A negative value indicates there's no limit (defaults to -1).
     *
     * @param numberOfRecordsToRead the number of records to read before stopping the parsing process.
     */
    public void setNumberOfRecordsToRead(long numberOfRecordsToRead) {
        this.numberOfRecordsToRead = numberOfRecordsToRead;
    }

    /**
     * Indicates whether fields selected using the field selection methods (defined by the parent class {@link AbstractCommonSettings}) should be reordered (defaults to
     * true).
     * <p>When disabled, each parsed record will contain values for all columns, in the order they occur in the input. Fields which were not selected will not
     * be parsed but and the record will contain empty values.
     * <p>When enabled, each parsed record will contain values only for the selected columns. The values will be ordered according to the selection.
     *
     * @return true if the selected fields should be reordered and returned by the parser, false otherwise
     */
    public boolean isColumnReorderingEnabled() {
        return !preventReordering() && columnReorderingEnabled;
    }

    /**
     * Returns the set of selected fields, if any
     *
     * @return the set of selected fields. Null if no field was selected/excluded
     */
    @Override
    FieldSet<?> getFieldSet() {
        return preventReordering() ? null : super.getFieldSet();
    }

    /**
     * Returns the FieldSelector object, which handles selected fields.
     *
     * @return the FieldSelector object, which handles selected fields. Null if no field was selected/excluded
     */
    @Override
    FieldSelector getFieldSelector() {
        return preventReordering() ? null : super.getFieldSelector();
    }

    /**
     * Defines whether fields selected using the field selection methods (defined by the parent class {@link AbstractCommonSettings}) should be reordered (defaults to
     * true).
     * <p>When disabled, each parsed record will contain values for all columns, in the order they occur in the input. Fields which were not selected will not
     * be parsed but the record will contain empty values.
     * <p>When enabled, each parsed record will contain values only for the selected columns. The values will be ordered according to the selection.
     *
     * @param columnReorderingEnabled the flag indicating whether or not selected fields should be reordered and returned by the parser
     */
    public void setColumnReorderingEnabled(boolean columnReorderingEnabled) {
        if (columnReorderingEnabled && preventReordering()) {
            throw new IllegalArgumentException("Cannot reorder columns when using a row processor that manipulates nested rows.");
        }
        this.columnReorderingEnabled = columnReorderingEnabled;
    }

    /**
     * Informs the number of characters held by the parser's buffer when processing the input (defaults to 1024*1024 characters).
     *
     * @return the number of characters held by the parser's buffer when processing the input
     */
    public int getInputBufferSize() {
        return inputBufferSize;
    }

    /**
     * Defines the number of characters held by the parser's buffer when processing the input (defaults to 1024*1024 characters).
     *
     * @param inputBufferSize the new input buffer size (in number of characters)
     */
    public void setInputBufferSize(int inputBufferSize) {
        this.inputBufferSize = inputBufferSize;
    }

    /**
     * Returns an instance of CharAppender with the configured limit of maximum characters per column and the default value used to represent a null value (when
     * the String parsed from the input is empty)
     *
     * @return an instance of CharAppender with the configured limit of maximum characters per column and the default value used to represent a null value (when
     * the String parsed from the input is empty)
     */
    protected CharAppender newCharAppender() {
        int chars = getMaxCharsPerColumn();
        if (chars != -1) {
            return new DefaultCharAppender(chars, getNullValue(), getWhitespaceRangeStart());
        } else {
            return new ExpandingCharAppender(getNullValue(), getWhitespaceRangeStart());
        }
    }

    /**
     * Indicates whether the parser should detect the line separator automatically.
     *
     * @return {@code true} if the first line of the input should be used to search for common line separator sequences (the matching sequence will be used as
     * the line separator for parsing). Otherwise {@code false}.
     */
    public final boolean isLineSeparatorDetectionEnabled() {
        return lineSeparatorDetectionEnabled;
    }

    /**
     * Defines whether the parser should detect the line separator automatically.
     *
     * @param lineSeparatorDetectionEnabled a flag indicating whether the first line of the input should be used to search for common line separator sequences
     *                                      (the matching sequence will be used as the line separator for parsing).
     */
    public final void setLineSeparatorDetectionEnabled(boolean lineSeparatorDetectionEnabled) {
        this.lineSeparatorDetectionEnabled = lineSeparatorDetectionEnabled;
    }

    /**
     * Returns the number of rows to skip from the input before the parser can begin to execute.
     *
     * @return number of rows to skip before parsing
     */
    public final long getNumberOfRowsToSkip() {
        return numberOfRowsToSkip;
    }

    /**
     * Defines a number of rows to skip from the input before the parser can begin to execute.
     *
     * @param numberOfRowsToSkip number of rows to skip before parsing
     */
    public final void setNumberOfRowsToSkip(long numberOfRowsToSkip) {
        if (numberOfRowsToSkip < 0) {
            throw new IllegalArgumentException("Number of rows to skip from the input must be 0 or greater");
        }
        this.numberOfRowsToSkip = numberOfRowsToSkip;
    }

    @Override
    protected void addConfiguration(Map<String, Object> out) {
        super.addConfiguration(out);
        out.put("Header extraction enabled", headerExtractionEnabled);
        out.put("Processor", processor == null ? "none" : processor.getClass().getName());
        out.put("Column reordering enabled", columnReorderingEnabled);
        out.put("Input buffer size", inputBufferSize);
        out.put("Input reading on separate thread", readInputOnSeparateThread);
        out.put("Number of records to read", numberOfRecordsToRead == -1 ? "all" : numberOfRecordsToRead);
        out.put("Line separator detection enabled", lineSeparatorDetectionEnabled);
        out.put("Auto-closing enabled", autoClosingEnabled);
    }

    private boolean preventReordering() {
        if (processor instanceof ColumnOrderDependent) {
            return ((ColumnOrderDependent) processor).preventColumnReordering();
        }

        return false;
    }

    /**
     * Indicates that comments found in the input must be collected (disabled by default). If enabled, comment lines will be
     * stored by the parser and made available via {@code AbstractParser.getContext().comments()} and {@code AbstractParser.getContext().lastComment()}
     *
     * @return a flag indicating whether or not to enable collection of comments.
     */
    public boolean isCommentCollectionEnabled() {
        return commentCollectionEnabled;
    }

    /**
     * Enables collection of comments found in the input (disabled by default). If enabled, comment lines will be
     * stored by the parser and made available via {@code AbstractParser.getContext().comments()} and {@code AbstractParser.getContext().lastComment()}
     *
     * @param commentCollectionEnabled flag indicating whether or not to enable collection of comments.
     */
    public void setCommentCollectionEnabled(boolean commentCollectionEnabled) {
        this.commentCollectionEnabled = commentCollectionEnabled;
    }

    @Override
    final void runAutomaticConfiguration() {
        Class<?> beanClass = null;

        if (processor instanceof AbstractBeanProcessor<?, ?>) {
            beanClass = ((AbstractBeanProcessor<?, ?>) processor).getBeanClass();
        } else if (processor instanceof AbstractMultiBeanProcessor<?>) {
            Class[] classes = ((AbstractMultiBeanProcessor<?>) processor).getBeanClasses();
            if (classes.length > 0) {
                beanClass = classes[0];
            }
        }

        if (beanClass != null) {
            configureFromAnnotations(beanClass);
        }
    }

    /**
     * Configures the parser based on the annotations provided in a given class
     *
     * @param beanClass the classes whose annotations will be processed to derive configurations for parsing
     */
    protected synchronized void configureFromAnnotations(Class<?> beanClass) {
        if (!deriveHeadersFrom(beanClass)) {
            return;
        }
        Headers headerAnnotation = AnnotationHelper.findHeadersAnnotation(beanClass);

        String[] headersFromBean = ArgumentUtils.EMPTY_STRING_ARRAY;
        boolean allFieldsIndexBased = AnnotationHelper.allFieldsIndexBasedForParsing(beanClass);
        boolean extractHeaders = !allFieldsIndexBased;

        if (headerAnnotation != null) {
            if (headerAnnotation.sequence().length > 0) {
                headersFromBean = headerAnnotation.sequence();
            }
            extractHeaders = headerAnnotation.extract();
        }

        if (headerExtractionEnabled == null) {
            setHeaderExtractionEnabled(extractHeaders);
        }

        if (getHeaders() == null && headersFromBean.length > 0 && !headerExtractionEnabled) {
            setHeadersDerivedFromClass(beanClass, headersFromBean);
        }

        if (getFieldSet() == null) {
            if (allFieldsIndexBased) {
                selectIndexes(AnnotationHelper.getSelectedIndexes(beanClass, MethodFilter.ONLY_SETTERS));
            } else if (headersFromBean.length > 0 && AnnotationHelper.allFieldsNameBasedForParsing(beanClass)) {
                selectFields(headersFromBean);
            }
        }
    }

    @Override
    protected AbstractCommonParserSettings clone(boolean clearInputSpecificSettings) {
        return (AbstractCommonParserSettings) super.clone(clearInputSpecificSettings);
    }

    @Override
    protected AbstractCommonParserSettings clone() {
        return (AbstractCommonParserSettings) super.clone();
    }

    @Override
    protected void clearInputSpecificSettings() {
        super.clearInputSpecificSettings();
        processor = null;
        numberOfRecordsToRead = -1L;
        numberOfRowsToSkip = 0L;
    }

    /**
     * Indicates whether automatic closing of the input (reader, stream, etc)
     * is enabled. If {@code true}, the parser will always close the input automatically
     * when all records have been parsed or when an error occurs.
     * <p>
     * Defaults to {@code true}
     *
     * @return flag indicating whether automatic input closing is enabled.
     */
    public boolean isAutoClosingEnabled() {
        return autoClosingEnabled;
    }

    /**
     * Configures whether the parser should always close the input (reader, stream, etc) automatically
     * when all records have been parsed or when an error occurs.
     * <p>
     * Defaults to {@code true}
     *
     * @param autoClosingEnabled flag determining whether automatic input closing should be enabled.
     */
    public void setAutoClosingEnabled(boolean autoClosingEnabled) {
        this.autoClosingEnabled = autoClosingEnabled;
    }

    /**
     * Indicates whether code will check for comment line in the data file
     * is enabled. If {@code true}, the parser will always check for the comment line
     * default value for comment check is #
     * Defaults to {@code true}
     *
     * @return flag indicating whether parser will check for the comment line
     * If disabled/false then parser wont treat any line as comment line including default(#)
     * this condition will supersede the comment character(#)
     */
    public boolean isCommentProcessingEnabled() {
        return commentProcessingEnabled;
    }

    /**
     * Configures whether the parser will check for the comment line in the file
     * Defaults to {@code true}
     *
     * @param commentProcessingEnabled flag determining whether comment line check should be performed
     *                                 If disabled/false then parser wont treat any line as comment line including default(#)
     *                                 this condition will supersede the comment character(#)
     */
    public void setCommentProcessingEnabled(boolean commentProcessingEnabled) {
        this.commentProcessingEnabled = commentProcessingEnabled;
    }

    /**
     * Provides a custom {@link InputAnalysisProcess} to analyze the input buffer and potentially discover configuration options such as
     * column separators is CSV, data formats, etc. The process will be execute only once.
     *
     * @param inputAnalysisProcess a custom process to analyze the contents of the first input buffer loaded when the parsing starts.
     */
    public void addInputAnalysisProcess(InputAnalysisProcess inputAnalysisProcess) {
        if (inputAnalysisProcess == null) {
            return;
        }
        if (this.inputAnalysisProcesses == null) {
            inputAnalysisProcesses = new ArrayList<InputAnalysisProcess>();
        }
        inputAnalysisProcesses.add(inputAnalysisProcess);
    }

    /**
     * Returns the sequence of {@link InputAnalysisProcess} to be used for analyzing the input buffer and potentially discover configuration options such as
     * column separators is CSV, data formats, etc. Each process will be execute only once.
     *
     * @return the list of custom processes to analyze the contents of the first input buffer loaded when the parsing starts.
     */
    public List<InputAnalysisProcess> getInputAnalysisProcesses() {
        return inputAnalysisProcesses;
    }

}