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

package com.chua.common.support.file.univocity.parsers.common.processor.core;

import com.chua.common.support.file.univocity.parsers.common.AbstractContext;
import com.chua.common.support.file.univocity.parsers.common.BaseParser;
import com.chua.common.support.file.univocity.parsers.common.AbstractWriter;

import java.util.HashMap;
import java.util.Map;


/**
 * A {@link Processor} implementation for converting rows extracted from any implementation of {@link BaseParser} into java objects.
 *
 * <p>The class types passed to the constructor of this class must contain the annotations provided in {@link com.chua.common.support.file.univocity.parsers.annotations}.
 *
 * <p> For each row processed, one or more java bean instances of any given class will be created with their fields populated.
 * <p> Once all beans are populated from an individual input record, they will be sent to through the {@link AbstractMultiBeanRowProcessor#rowProcessed(Map, AbstractContext)} method,
 * where the user can access all beans parsed for that row.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see BaseParser
 * @see Processor
 * @see AbstractBeanProcessorAbstract
 * @see AbstractMultiBeanProcessor
 */
public abstract class AbstractMultiBeanRowProcessor<C extends AbstractContext> extends AbstractMultiBeanProcessor<C> {

    private final HashMap<Class<?>, Object> row = new HashMap<Class<?>, Object>();
    private long record = -1L;

    /**
     * Creates a processor for java beans of multiple types
     *
     * @param beanTypes the classes with their attributes mapped to fields of records parsed by an {@link BaseParser} or written by an {@link AbstractWriter}.
     */
    public AbstractMultiBeanRowProcessor(Class... beanTypes) {
        super(beanTypes);
    }

    public void processStarted(C context) {
        record = -1L;
        row.clear();
        super.processStarted(context);
    }

    @Override
    public final void beanProcessed(Class<?> beanType, Object beanInstance, C context) {
        if (record != context.currentRecord() && record != -1L) {
            submitRow(context);
        }
        record = context.currentRecord();
        row.put(beanType, beanInstance);
    }

    private void submitRow(C context) {
        if (!row.isEmpty()) {
            rowProcessed(row, context);
            row.clear();
        }
    }

    @Override
    public void processEnded(C context) {
        submitRow(context);
        super.processEnded(context);
    }

    /**
     * Invoked by the processor after all beans of a valid record have been processed.
     *
     * @param row     a map containing all object instances generated from an input row. <b>The map is reused internally. Make a copy if you want to keep the map</b>.
     * @param context A contextual object with information and controls over the current state of the parsing process
     */
    protected abstract void rowProcessed(Map<Class<?>, Object> row, C context);
}