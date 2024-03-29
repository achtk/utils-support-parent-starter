/*
 * Copyright 2014 Patrick Prasse
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
package com.chua.common.support.mysql.binlog.event;

/**
 * @author <a href="mailto:pprasse@actindo.de">Patrick Prasse</a>
 */
public class GtidEventData implements EventData {

    public static final byte COMMIT_FLAG = 1;

    private MySqlGtid gtid;
    private byte flags;

    @Deprecated
    public GtidEventData() {
    }

    public GtidEventData(MySqlGtid gtid, byte flags) {
        this.gtid = gtid;
        this.flags = flags;
    }

    @Deprecated
    public String getGtid() {
        return gtid.toString();
    }

    @Deprecated
    public void setGtid(String gtid) {
        this.gtid = MySqlGtid.fromString(gtid);
    }

    public MySqlGtid getMySqlGtid() {
        return gtid;
    }

    public byte getFlags() {
        return flags;
    }

    @Deprecated
    public void setFlags(byte flags) {
        this.flags = flags;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("GtidEventData");
        sb.append("{flags=").append(flags).append(", gtid='").append(gtid).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
