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
package com.chua.common.support.mysql.binlog.event.deserialization;

import com.chua.common.support.mysql.binlog.event.XidEventData;
import com.chua.common.support.mysql.binlog.io.ByteArrayInputStream;

import java.io.IOException;

/**
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public class XidEventDataDeserializer implements EventDataDeserializer<XidEventData> {

    @Override
    public XidEventData deserialize(ByteArrayInputStream inputStream) throws IOException {
        XidEventData eventData = new XidEventData();
        eventData.setXid(inputStream.readLong(8));
        return eventData;
    }
}
