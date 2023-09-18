package com.chua.common.support.mysql.binlog.event.deserialization;


import com.chua.common.support.mysql.binlog.MariadbGtidSet;
import com.chua.common.support.mysql.binlog.event.MariadbGtidListEventData;
import com.chua.common.support.mysql.binlog.io.ByteArrayInputStream;

import java.io.IOException;

/**
 * Mariadb GTID_LIST_EVENT Fields
 * <pre>
 *  uint4 Number of GTIDs
 *  GTID[0]
 *      uint4 Replication Domain ID
 *      uint4 Server_ID
 *      uint8 GTID sequence ...
 * GTID[n]
 * </pre>
 *
 * @author <a href="mailto:winger2049@gmail.com">Winger</a>
 * @see <a href="https://mariadb.com/kb/en/gtid_event/">GTID_EVENT</a> for the original doc
 */
public class MariadbGtidListEventDataDeserializer implements EventDataDeserializer<MariadbGtidListEventData> {
    @Override
    public MariadbGtidListEventData deserialize(ByteArrayInputStream inputStream) throws IOException {
        MariadbGtidListEventData eventData = new MariadbGtidListEventData();
        long gtidLength = inputStream.readInteger(4);
        MariadbGtidSet mariaGTIDSet = new MariadbGtidSet();
        for (int i = 0; i < gtidLength; i++) {
            long domainId = inputStream.readInteger(4);
            long serverId = inputStream.readInteger(4);
            long sequence = inputStream.readLong(8);
            mariaGTIDSet.add(new MariadbGtidSet.MariaGtid(domainId, serverId, sequence));
        }
        eventData.setMariaGTIDSet(mariaGTIDSet);
        return eventData;
    }
}
