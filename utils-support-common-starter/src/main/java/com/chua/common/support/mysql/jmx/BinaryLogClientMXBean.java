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
package com.chua.common.support.mysql.jmx;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public interface BinaryLogClientMXBean {
    /**
     * 名称
     *
     * @return 名称
     */
    String getBinlogFilename();

    /**
     * 名称
     *
     * @param binlogFilename 名称
     */
    void setBinlogFilename(String binlogFilename);

    /**
     * 位置
     *
     * @return 位置
     */
    long getBinlogPosition();

    /**
     * 位置
     *
     * @param binlogPosition 位置
     */
    void setBinlogPosition(long binlogPosition);

    /**
     * 连接
     *
     * @param timeoutInMilliseconds 超时时间
     * @throws IOException      ex
     * @throws TimeoutException ex
     */
    void connect(long timeoutInMilliseconds) throws IOException, TimeoutException;

    /**
     * 是否连接
     * @return 是否连接
     */
    boolean isConnected();

    /**
     * 断开连接
     * @throws IOException ex
     */
    void disconnect() throws IOException;

}
