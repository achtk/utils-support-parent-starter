package com.chua.common.support.extra.el.baseutil;

import java.util.Comparator;

/**
 * 基础类
 *
 * @author CH
 */
public interface Order {

    Comparator<Order> AES = new Comparator<Order>() {

        @Override
        public int compare(Order o1, Order o2) {
            if (o1.getOrder() > o2.getOrder()) {
                return 1;
            } else if (o1.getOrder() == o2.getOrder()) {
                return 0;
            } else {
                return -1;
            }
        }
    };
    Comparator<Order> DESC = new Comparator<Order>() {

        @Override
        public int compare(Order o1, Order o2) {
            if (o1.getOrder() > o2.getOrder()) {
                return -1;
            } else if (o1.getOrder() == o2.getOrder()) {
                return 0;
            } else {
                return 1;
            }
        }
    };

    /**
     * 返回顺序
     *
     * @return
     */
    int getOrder();
}
