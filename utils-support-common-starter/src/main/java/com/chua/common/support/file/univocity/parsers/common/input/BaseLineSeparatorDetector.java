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
package com.chua.common.support.file.univocity.parsers.common.input;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_NULL_CHAR;
import static com.chua.common.support.constant.CommonConstant.SYMBOL_N_CHAR;

/**
 * An {@link InputAnalysisProcess} to detect the line separators used in the input.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public abstract class BaseLineSeparatorDetector implements InputAnalysisProcess {

    @Override
    public void execute(char[] characters, int length) {
        char separator1 = '\0';
        char separator2 = '\0';
        for (int c = 0; c < length; c++) {
            char ch = characters[c];
            if (ch == '\n' || ch == '\r') {
                if (separator1 == '\0') {
                    separator1 = ch;
                } else {
                    separator2 = ch;
                    break;
                }
            } else if (separator1 != '\0') {
                break;
            }
        }

        char lineSeparator1 = separator1;
        char lineSeparator2 = separator2;

        if (separator1 != SYMBOL_NULL_CHAR) {
            if (separator1 == SYMBOL_N_CHAR) {
                lineSeparator1 = SYMBOL_N_CHAR;
                lineSeparator2 = SYMBOL_NULL_CHAR;
            } else {
                lineSeparator1 = SYMBOL_N_CHAR;
                if (separator2 == SYMBOL_N_CHAR) {
                    lineSeparator2 = SYMBOL_N_CHAR;
                } else {
                    lineSeparator2 = SYMBOL_NULL_CHAR;
                }
            }
        }

        apply(lineSeparator1, lineSeparator2);
    }

    /**
     * 執行
     * @param lineSeparator1 Separator
     * @param lineSeparator2 Separator
     */
    protected abstract void apply(char lineSeparator1, char lineSeparator2);
}
