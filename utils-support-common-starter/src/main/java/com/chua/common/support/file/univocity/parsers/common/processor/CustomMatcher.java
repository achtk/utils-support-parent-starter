/*
 * Copyright (c) 2015. Univocity Software Pty Ltd
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

package com.chua.common.support.file.univocity.parsers.common.processor;

/**
 * Interface used by {@link InputValueSwitch} to allow users to to provide custom matching rules against input values.
 * @author Administrator
 */
public interface CustomMatcher {

	/**
	 * Matches a parsed value against a user provided rule (implementation provided by the user)
	 *
	 * @param value the value to be matched
	 * @return {@code true} if the given value matches the user provided rule, otherwise {@code false}
	 */
	boolean matches(String value);
}
