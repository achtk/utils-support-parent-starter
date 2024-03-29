/*******************************************************************************
 * Copyright 2015 Univocity Software Pty Ltd
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

/**
 * A singleton instance of a {@link Processor} that does nothing.
 *
 * @author Administrator
 */
public final class NoopProcessor extends AbstractProcessor {

	/**
	 * The singleton instance of the no-op {@link Processor}
	 */
	public static final Processor INSTANCE = new NoopProcessor();

	private NoopProcessor() {
	}

}
