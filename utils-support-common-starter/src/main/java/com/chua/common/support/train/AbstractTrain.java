package com.chua.common.support.train;

import java.util.LinkedList;
import java.util.List;

/**
 * 训练
 *
 * @author CH
 */
public abstract class AbstractTrain<Chain> implements Train<AbstractTrain<Chain>> {
    private static final int NUM_EPOCH_DEFAULT = 2;
    private static final int NUM_OF_OUTPUT_DEFAULT = 4;
    private static final int BATCH_SIZE_DEFAULT = 32;
    private static final int MAX_GPUS_DEFAULT = 4;
    protected int numOfOutput = NUM_OF_OUTPUT_DEFAULT;
    /**
     * 训练模式
     * 0: 加载整个文件夹
     * 1: 加载train/validation
     */
    protected int trainType = 1;
    /**
     * 训练模式
     * 0: resetnet
     * 1: model + load
     */
    protected int modeType = 0;
    protected int maxGpus = MAX_GPUS_DEFAULT;
    protected int batchSize = BATCH_SIZE_DEFAULT;

    protected List<Listener> list = new LinkedList<>();
    protected String modelName;
    protected int numEpoch = NUM_EPOCH_DEFAULT;
    protected String modelUrl;

    protected int width;
    protected int height;
    private List<String> label;

    public AbstractTrain(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public AbstractTrain<Chain> trainType(int trainType) {
        this.trainType = trainType;
        return this;
    }

    @Override
    public AbstractTrain<Chain> modeType(int modeType) {
        this.modeType = modeType;
        return this;
    }

    @Override
    public AbstractTrain<Chain> modelUrl(String modelUrl) {
        this.modelUrl = modelUrl;
        return this;
    }

    @Override
    public AbstractTrain<Chain> numOfOutput(int numOfOutput) {
        this.numOfOutput = numOfOutput;
        return this;
    }

    @Override
    public AbstractTrain<Chain> maxGpus(int maxGpus) {
        this.maxGpus = maxGpus;
        return this;
    }

    @Override
    public AbstractTrain<Chain> batchSize(int batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    @Override
    public AbstractTrain<Chain> numEpoch(int numEpoch) {
        this.numEpoch = numEpoch;
        return this;
    }

    @Override
    public AbstractTrain<Chain> modelName(String modelName) {
        this.modelName = modelName;
        return this;
    }

    @Override
    public AbstractTrain<Chain> addListener(Listener listener) {
        this.list.add(listener);
        return this;
    }

    @Override
    public List<String> label() {
        return label;
    }

    @Override
    public void label(List<String> label) {
        this.label = label;
    }

    @Override
    public void afterPropertiesSet() {

    }
}
