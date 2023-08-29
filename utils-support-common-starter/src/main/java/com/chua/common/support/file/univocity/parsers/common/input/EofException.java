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
package com.chua.common.support.file.univocity.parsers.common.input;

/**
 * Internal exception marker to signalize the end of the input.
 *
 * @author Administrator
 */
public final class EofException extends RuntimeException {

    private static final long serialVersionUID = -4064380464076294133L;

    /**
     * Creates a new exception
     */
    public EofException() {
        super();
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
