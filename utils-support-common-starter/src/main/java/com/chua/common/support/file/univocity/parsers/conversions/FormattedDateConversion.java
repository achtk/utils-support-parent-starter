/*
 * Copyright (c) 2018. Univocity Software Pty Ltd
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
package com.chua.common.support.file.univocity.parsers.conversions;

import com.chua.common.support.lang.date.DateUtils;
import com.chua.common.support.file.univocity.parsers.common.DataProcessingException;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Converts objects of different date types ({@code java.util.Date} and {@code java.util.Calendar}) to a formatted
 * date {@code String}.
 *
 * <p> The reverse conversion is not supported.
 *
 * <p> The date patterns must follow the pattern rules of {@link SimpleDateFormat}
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see SimpleDateFormat
 */
public class FormattedDateConversion implements Conversion<Object, String> {

	private final DateTimeFormatter dateFormat;
	private final String valueIfObjectIsNull;

	/**
	 * @param format              The pattern to be used to convert an input date into a String in {@link FormattedDateConversion#execute(Object)}.
	 * @param locale              the {@link Locale} that determines how the date mask should be formatted.
	 * @param valueIfObjectIsNull default String value to be returned when an input is {@code null} . Used when {@link FormattedDateConversion#execute(Object)} is invoked with a {@code null} parameter.
	 */
	public FormattedDateConversion(String format, Locale locale, String valueIfObjectIsNull) {
		this.valueIfObjectIsNull = valueIfObjectIsNull;
		locale = locale == null ? Locale.getDefault() : locale;
		this.dateFormat = DateTimeFormatter.ofPattern(format, locale);
	}

	@Override
	public String execute(Object input) {
		if (input == null) {
			return valueIfObjectIsNull;
		}
		Date date = null;
		if (input instanceof Date) {
			date = ((Date) input);
		} else if (input instanceof Calendar) {
			date = ((Calendar) input).getTime();
		}

		if (date != null) {
			return dateFormat.format(DateUtils.toLocalDateTime(date));
		}

		DataProcessingException exception = new DataProcessingException("Cannot format '{value}' to a date. Not an instance of java.util.Date or java.util.Calendar");
		exception.setValue(input);
		throw exception;
	}

	/**
	 * Unsupported operation.
	 *
	 * @param input the input be converted.
	 * @return throws a {@code UnsupportedOperationException}
	 */
	@Override
	public Object revert(String input) {
		throw new UnsupportedOperationException("Can't convert an input string into date type");
	}
}
