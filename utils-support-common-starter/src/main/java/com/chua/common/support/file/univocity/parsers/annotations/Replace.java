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
package com.chua.common.support.file.univocity.parsers.annotations;

import com.chua.common.support.file.univocity.parsers.common.processor.AbstractBeanProcessor;
import com.chua.common.support.file.univocity.parsers.common.processor.BeanWriterProcessor;
import com.chua.common.support.file.univocity.parsers.conversions.Conversion;
import com.chua.common.support.file.univocity.parsers.conversions.Conversions;
import com.chua.common.support.file.univocity.parsers.conversions.RegexConversion;

import java.lang.annotation.*;

/**
 * Indicates the String value of a field must have some of its contents replaced using {@link RegexConversion}.
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
public @interface Replace {
	/**
	 * The regular expression to be executed against the String value of the annotated field
	 *
	 * @return the regular expression
	 */
	String expression();

	/**
	 * The replacement string to substitute any contents matched by the given {@link Replace#expression()}
	 *
	 * @return the replacement string
	 */
	String replacement();
}
