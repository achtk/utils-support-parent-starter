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

import com.chua.common.support.file.univocity.parsers.common.AbstractContext;

/**
 * A {@link Processor} implementation that just implements all methods defined by the interface.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public abstract class AbstractProcessor<T extends AbstractContext> implements Processor<T> {

	@Override
	public void processStarted(T context) {
	}

	@Override
	public void rowProcessed(String[] row, T context) {
	}

	@Override
	public void processEnded(T context) {
	}
}
