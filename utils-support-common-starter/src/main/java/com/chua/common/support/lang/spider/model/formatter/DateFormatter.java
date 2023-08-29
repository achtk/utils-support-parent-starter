package com.chua.common.support.lang.spider.model.formatter;

import com.chua.common.support.lang.date.DateUtils;

import java.util.Date;

/**
 * @author code4crafter@gmail.com
 * @since 0.3.2
 */
public class DateFormatter implements ObjectFormatter<Date> {

    public static final String[] DEFAULT_PATTERN = new String[]{"yyyy-MM-dd HH:mm"};
    private String[] datePatterns = DEFAULT_PATTERN;

    @Override
    public Date format(String raw) throws Exception {
        return DateUtils.parseDate(raw, datePatterns);
    }

    @Override
    public Class<Date> clazz() {
        return Date.class;
    }

    @Override
    public void initParam(String[] extra) {
        boolean b = extra != null && !(extra.length == 1 && extra[0].length() == 0);
        if (b) {
            datePatterns = extra;
        }
    }
}
