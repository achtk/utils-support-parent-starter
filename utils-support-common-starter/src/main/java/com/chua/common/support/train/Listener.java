package com.chua.common.support.train;

/**
 * 监听
 *
 * @author CH
 */
public interface Listener {

    /**
     * 训练
     *
     * @param trainer   训练器
     * @param batchData 数据
     */
    void onTrainingBatch(Object trainer, Object batchData);

    /**
     * 训练
     *
     * @param trainer   训练器
     * @param batchData 数据
     */
    void onValidationBatch(Object trainer, Object batchData);

    /**
     * 训练
     *
     * @param trainer 训练器
     */
    void onTrainingBegin(Object trainer);

    /**
     * 训练
     *
     * @param trainer 训练器
     */
    void onTrainingEnd(Object trainer);
}
