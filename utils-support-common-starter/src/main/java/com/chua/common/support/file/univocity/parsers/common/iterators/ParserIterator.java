/*******************************************************************************
 * Copyright 2017 Univocity Software Pty Ltd
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
package com.chua.common.support.file.univocity.parsers.common.iterators;

import com.chua.common.support.file.univocity.parsers.common.AbstractParser;
import com.chua.common.support.file.univocity.parsers.common.IterableResult;
import com.chua.common.support.file.univocity.parsers.common.ParsingContext;
import com.chua.common.support.file.univocity.parsers.common.ResultIterator;
import com.chua.common.support.file.univocity.parsers.common.record.Record;

import java.util.Iterator;

/**
 * An {@link Iterator} over the parser enabling easy iteration against rows and records
 * Multiple iterations are possible if Files are being fed into the parser,
 * but other forms of input (such as {@code InputStream}s and {@code Reader}s) can not be iterated over more than once.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
abstract class ParserIterator<T> implements IterableResult<T, ParsingContext> {

	protected final AbstractParser parser;

	/**
	 * Creates a {@code ParserIterator} using the provided {@code parser}
	 *
	 * @param parser the {@code parser} to iterate over
	 */
	protected ParserIterator(AbstractParser parser) {
		this.parser = parser;
	}

	@Override
	public final ParsingContext getContext() {
		if (parser.getContext() == null) {
			beginParsing();
		}
		return parser.getContext();
	}


	/**
	 * This method is called whenever the {@code iterator} is starting to iterate over the
	 * results.
	 * an example implementation of this is:
	 * <hr>
	 * <pre>
	 *     {@code
	 *     &#064;Override
	 *     public void beginParsing(){
	 *         parser.beginParsing(input);
	 *     }}
	 * </pre>
	 * <hr>
	 * This is to allow for different input types such as {@code Reader, File, or InputStream} without large code
	 * reuse.
	 */
	protected abstract void beginParsing();

	@Override
	public final ResultIterator<T, ParsingContext> iterator() {
		return new ResultIterator<T, ParsingContext>() {
			T next;
			boolean started;

			@Override
			public ParsingContext getContext() {
				return parser.getContext();
			}

			@Override
			public boolean hasNext() {
				if (started) {
					return next != null;
				} else {
					started = true;
					if (parser.getContext() == null) {
						beginParsing();
					}
					next = nextResult();
					return next != null;
				}
			}

			@Override
			public T next() {
				if (!started) {
					hasNext();
				}
				T out = next;
				next = nextResult();
				return out;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("Can't remove row");
			}
		};
	}

	/**
	 * Returns the next record (either a String[] or a {@link Record})
	 *
	 * @return the next record if available.
	 */
	protected abstract T nextResult();
}
