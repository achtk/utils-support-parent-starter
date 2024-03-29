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

import com.chua.common.support.file.univocity.parsers.common.*;
import com.chua.common.support.file.univocity.parsers.common.processor.MasterDetailRecord;
import com.chua.common.support.file.univocity.parsers.common.processor.ObjectRowListProcessor;
import com.chua.common.support.file.univocity.parsers.common.processor.BaseObjectRowProcessor;
import com.chua.common.support.file.univocity.parsers.common.processor.RowListProcessor;
import com.chua.common.support.file.univocity.parsers.conversions.Conversion;

/**
 * The essential callback interface to handle records parsed by any parser that extends {@link BaseParser}.
 *
 * <p>When parsing an input, univocity-parsers will obtain the RowProcessor from {@link BaseCommonParserSettings#getRowProcessor()}, and
 * delegate each parsed row to {@link Processor#rowProcessed(String[], AbstractContext)}.
 *
 * <p>Before parsing the first row, the parser will invoke the {@link Processor#processStarted(AbstractContext)} method.
 * By this time the input buffer will be already loaded and ready to be consumed.
 *
 * <p>After parsing the last row, all resources are closed and the processing stops. Only after the {@link Processor#processEnded(AbstractContext)} is called so you
 * can perform any additional housekeeping you might need.
 *
 * <p>More control and information over the parsing process are provided by the {@link AbstractContext} object.
 *
 * <p>univocity-parsers provides many useful default implementations of this interface in the package {@link com.chua.common.support.file.univocity.parsers.common.processor}, namely:
 *
 * <ul>
 * <li>{@link RowListProcessor}: convenience class for storing the processed rows into a list.</li>
 * <li>{@link BaseObjectRowProcessor}: used for processing rows and executing conversions of parsed values to objects using instances of {@link Conversion}</li>
 * <li>{@link ObjectRowListProcessor}: convenience class for rows of converted objects using {@link BaseObjectRowProcessor} into a list.</li>
 * <li>{@link AbstractMasterDetailProcessorAbstract}: used for reading inputs where records are organized in a master-detail fashion (with a master element that contains a list of associated elements) </li>
 * <li>{@link AbstractMasterDetailListProcessorAbstract}: convenience class for storing {@link MasterDetailRecord} created by instances created by {@link AbstractMasterDetailProcessorAbstract} into a list </li>
 * <li>{@link AbstractBeanProcessorAbstract}: used for automatically create and populate javabeans annotated with the annotations provided in package {@link com.chua.common.support.file.univocity.parsers.annotations}</li>
 * <li>{@link AbstractBeanListProcessorAbstract}: convenience class for storing all javabeans created by {@link AbstractBeanProcessorAbstract} into a list</li>
 * </ul>
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see BaseParser
 * @see BaseCommonParserSettings
 * @see ParsingContext
 * @see AbstractContext
 */
public interface Processor<T extends AbstractContext> {

	/**
	 * This method will by invoked by the parser once, when it is ready to start processing the input.
	 *
	 * @param context A contextual object with information and controls over the current state of the parsing process
	 */
	void processStarted(T context);

	/**
	 * Invoked by the parser after all values of a valid record have been processed.
	 *
	 * @param row     the data extracted by the parser for an individual record. Note that:
	 *                <ul>
	 *                <li>it will never by null. </li>
	 *                <li>it will never be empty unless explicitly configured using {@link BaseCommonSettings#setSkipEmptyLines(boolean)}</li>
	 *                <li>it won't contain lines identified by the parser as comments. To disable comment processing set {@link BaseFormat#setComment(char)} to '\0'</li>
	 *                </ul>
	 * @param context A contextual object with information and controls over the current state of the parsing process
	 */
	void rowProcessed(String[] row, T context);

	/**
	 * This method will by invoked by the parser once, after the parsing process stopped and all resources were closed.
	 * <p> It will always be called by the parser: in case of errors, if the end of the input us reached, or if the user stopped the process manually using {@link ParsingContext#stop()}.
	 *
	 * @param context A contextual object with information and controls over the state of the parsing process
	 */
	void processEnded(T context);
}
