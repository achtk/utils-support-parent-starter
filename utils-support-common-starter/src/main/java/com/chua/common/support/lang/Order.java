package com.chua.common.support.lang;


import com.chua.common.support.constant.Direction;

import java.io.Serializable;

import static com.chua.common.support.constant.CommonConstant.EMPTY;
import static com.chua.common.support.constant.CommonConstant.SYMBOL_BLANK;

/**
 * SQL排序对象
 * @author Looly
 *
 */
public class Order implements Serializable{
    private static final long serialVersionUID = 1L;

    /** 排序的字段 */
    private String field;
    /** 排序方式（正序还是反序） */
    private Direction direction;

        public Order() {
        }

    /**
     * 构造
     * @param field 排序字段
     */
    public Order(String field) {
        this.field = field;
    }

    /**
     * 构造
     *
     * @param field     排序字段
     * @param direction 排序方式
     */
    public Order(String field, Direction direction) {
        this(field);
        this.direction = direction;
    }


    /**
     * @return 排序字段
     */
    public String getField() {
        return this.field;
    }

    /**
     * 设置排序字段
     *
     * @param field 排序字段
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * @return 排序方向
     */
    public Direction getDirection() {
        return direction;
    }
    /**
     * 设置排序方向
     * @param direction 排序方向
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }
    
    @Override
    public String toString() {
        return new StringBuffer().append(this.field).append(SYMBOL_BLANK).append(null == direction ? EMPTY : direction).toString();
    }
}
