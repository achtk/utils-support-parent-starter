package com.chua.common.support.task.disruptor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 消息
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/4/30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisruptorMessage<E> implements Serializable {
    /**
     * 元素
     */
    private E element;
    /**
     * 序号
     */
    private long seq;
    /**
     * 批处理结束
     */
    private boolean endOfBatch;
}
