package com.chua.common.support.train;

import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.RandomUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 训练
 *
 * @author CH
 */
public interface Train<Chain> extends InitializingAware {
    /**
     * 标签
     *
     * @return 标签
     */
    List<String> label();

    /**
     * 标签
     *
     * @param label 标签
     */
    void label(List<String> label);

    /**
     * 训练
     *
     * @param modelPath 输出目录
     * @param trainPath 训练集
     * @throws Exception ex
     */
    void train(String trainPath, String modelPath) throws Exception;

    /**
     * modeType
     *
     * @param modeType modeType
     * @return this
     */
    Chain modeType(int modeType);

    /**
     * trainType
     *
     * @param trainType trainType
     * @return this
     */
    Chain trainType(int trainType);

    /**
     * modelUrl
     *
     * @param modelUrl modelUrl
     * @return this
     */
    Chain modelUrl(String modelUrl);

    /**
     * numOfOutput
     *
     * @param numOfOutput numOfOutput
     * @return this
     */
    Chain numOfOutput(int numOfOutput);

    /**
     * gpu
     *
     * @param maxGpus gpu
     * @return this
     */
    Chain maxGpus(int maxGpus);

    /**
     * 批处理
     *
     * @param batchSize 批处理
     * @return this
     */
    Chain batchSize(int batchSize);

    /**
     * 训练次数
     *
     * @param numEpoch 训练次数
     * @return this
     */
    Chain numEpoch(int numEpoch);

    /**
     * 模型名称
     *
     * @param modelName 模型名称
     * @return this
     */
    Chain modelName(String modelName);

    /**
     * 监听
     *
     * @param listener 监听
     * @return this
     */
    Chain addListener(Listener listener);


    /**
     * 随机目录
     *
     * @param trainPath trainPath
     */
    default void random(String trainPath) {

        File temp = new File(trainPath);
        File[] files = temp.listFiles();

        if (null == files) {
            return;
        }

        // 识别训练数据的位置
        File trainingDatasetRoot = new File(trainPath + "/train");
        // 识别验证数据的位置
        File validateDatasetRoot = new File(trainPath + "/validation");
        try {
            FileUtils.forceMkdir(trainingDatasetRoot);
            FileUtils.forceMkdir(validateDatasetRoot);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        doAnalysis(files, trainingDatasetRoot, validateDatasetRoot);
    }

    default void doAnalysis(File[] files, File train, File validate) {
        if (null == files || files.length == 0) {
            return;
        }

        //检测里面是否全是文件
        if (isSubFiles(files)) {
            validateSubFile(files, train, validate);
            return;
        }

        validateDirector(files, train, validate);
    }

    default void validateDirector(File[] files, File train, File validate) {
        for (File file : files) {
            File train1 = new File(train, file.getName());
            File validate1 = new File(validate, file.getName());
            try {
                FileUtils.forceMkdir(train1);
                FileUtils.forceMkdir(validate1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            doAnalysis(file.listFiles(), train1, validate1);
            try {
                file.delete();
            } catch (Exception ignored) {
            }
        }
    }

    default void validateSubFile(File[] files, File train, File validate) {
        int validateNum = files.length / 3;
        for (int i = 0; i < validateNum; i++) {
            int index = RandomUtils.randomInt(files.length);
            try {
                FileUtils.move(files[index], validate);
            } catch (IOException ignored) {
            }
        }

        for (File file1 : files) {
            if (!file1.exists()) {
                continue;
            }

            try {
                FileUtils.move(file1, train);
            } catch (IOException ignored) {
            }
        }
    }

    default boolean isSubFiles(File[] files) {
        return files[0].isFile();
    }

}
