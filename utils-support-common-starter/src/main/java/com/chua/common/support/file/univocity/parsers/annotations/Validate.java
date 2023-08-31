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

package com.chua.common.support.file.univocity.parsers.annotations;

import com.chua.common.support.file.univocity.parsers.common.processor.AbstractBeanProcessor;
import com.chua.common.support.file.univocity.parsers.common.processor.BeanWriterProcessor;
import com.chua.common.support.file.univocity.parsers.conversions.Conversion;
import com.chua.common.support.file.univocity.parsers.conversions.Conversions;
import com.chua.common.support.file.univocity.parsers.conversions.Validator;

import java.lang.annotation.*;

/**
 * Performs basic validations against the String representation of the value found in the annotated field.
 * A validation failure will generate a {@link com.chua.common.support.file.univocity.parsers.common.DataValidationException}.
 * <p>
 * By default, nulls and blanks are not allowed.
 *
 * <p>Commonly used for java beans processed using {@link AbstractBeanProcessor} and/or {@link BeanWriterProcessor}
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see Conversion
 * @see Conversions
 * @see AbstractBeanProcessor
 * @see BeanWriterProcessor
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface Validate {

	/**
	 * Indicates whether this field can be {@code null}
	 *
	 * @return {@code true} true if nulls are allowed, {@code false} otherwise
	 */
	boolean nullable() default false;

	/**
	 * Indicates whether this field can be blank (i.e. contain 0 or more whitespaces, where
	 * a whitespace is any character {@code  <= ' '}
	 *
	 * @return {@code true} true if blanks are allowed, {@code false} otherwise
	 */
	boolean allowBlanks() default false;

	/**
	 * Ensures that the value of this field matches a given regular expression.
	 *
	 * @return the regular expression that determines an expected format for the given value
	 */
	String matches() default "";

	/**
	 * Ensures that the value of this field is one of a given set of alternatives
	 *
	 * @return the sequence of allowed values
	 */
	String[] oneOf() default {};

	/**
	 * Ensures that the value of this field does is not an unwanted value.
	 *
	 * @return the sequence of disallowed values
	 */
	String[] noneOf() default {};

	/**
	 * User provided implementations of {@link Validator} which will be executed
	 * in sequence after the validations specified in this annotation execute.
	 *
	 * @return custom classes to be used to validate any value associated with this field.
	 */
	Class<? extends Validator>[] validators() default {};
}
