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

import com.chua.common.support.file.univocity.parsers.common.processor.BeanProcessor;
import com.chua.common.support.file.univocity.parsers.common.processor.BeanWriterProcessor;
import com.chua.common.support.file.univocity.parsers.conversions.Conversion;
import com.chua.common.support.file.univocity.parsers.conversions.Conversions;
import com.chua.common.support.file.univocity.parsers.conversions.LowerCaseConversion;

import java.lang.annotation.*;

/**
 * Indicates the String value of a field must be converted to lower case using {@link LowerCaseConversion}.
 *
 * <p>Commonly used for java beans processed using {@link BeanProcessor} and/or {@link BeanWriterProcessor}
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see Conversion
 * @see Conversions
 * @see BeanProcessor
 * @see BeanWriterProcessor
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface LowerCase {

}
