package com.chua.jna.support.log;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinNT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * log
 *
 * @author CH
 */
public class WinLog {
    /**
     * 初始化
     *
     * @return 日志
     */
    public static List<Event> create() {
        List<Event> eventList = new ArrayList<>();
        Advapi32Util.EventLogIterator iter = new Advapi32Util.EventLogIterator("EventLog");
        while (iter.hasNext()) {
            Advapi32Util.EventLogRecord record = iter.next();
            StringBuffer data = new StringBuffer();
            String[] str = record.getStrings();
            if (str != null) {
                for (String s : str) {
                    data.append(s);
                }
            }
            WinNT.EVENTLOGRECORD record1 = record.getRecord();
            eventList.add(new Event((short) record.getInstanceId(), new Date(record1.TimeGenerated.longValue() * 1000), record.getType().toString(), record1.EventCategory.toString(), record.getSource(), data.toString()));
        }
        Collections.sort(eventList);
        return eventList;
    }
}
