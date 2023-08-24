package com.chua.hikvision.support.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *  海康车辆/车库信息
 * </p>
 *
 * @author ch
 * @since 2022-06-28
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class HikCarCarportBill {

    static final HikCarCarportBill INSTANCE = new HikCarCarportBill();

    public static HikCarCarportBill empty() {
        return INSTANCE;
    }
}
