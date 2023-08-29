/*
 * Copyright 2013 Stanley Shyiko
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
 */
package com.chua.common.support.mysql.network.protocol;

import com.chua.common.support.mysql.io.ByteArrayInputStream;

import java.io.IOException;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_HASH_CHAR;
import static com.chua.common.support.constant.CommonConstant.SYMBOL_RIGHT_BIG_PARANTHESES_CHAR;

/**
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public class ErrorPacket implements Packet {

    private int errorCode;
    private String sqlState;
    private String errorMessage;

    public ErrorPacket(byte[] bytes) throws IOException {
        ByteArrayInputStream buffer = new ByteArrayInputStream(bytes);
        this.errorCode = buffer.readInteger(2);
        if (buffer.peek() == SYMBOL_HASH_CHAR) {
            buffer.skip(1);
            this.sqlState = buffer.readString(5);
        }
        this.errorMessage = buffer.readString(buffer.available());
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getSqlState() {
        return sqlState;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
