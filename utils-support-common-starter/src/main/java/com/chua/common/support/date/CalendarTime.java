package com.chua.common.support.date;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

/**
 * 日历
 *
 * @author CH
 */
@Data
@AllArgsConstructor
public class CalendarTime {

    private LocalDate startDate;
    private LocalDate endDate;
}
