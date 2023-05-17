package com.chua.hanlp.support.train;

import com.chua.common.support.lang.train.nlp.NlpTrain;
import com.chua.common.support.lang.train.nlp.TrainConfig;
import com.hankcs.hanlp.classification.classifiers.IClassifier;
import com.hankcs.hanlp.classification.classifiers.NaiveBayesClassifier;
import com.hankcs.hanlp.classification.models.NaiveBayesModel;
import com.hankcs.hanlp.corpus.io.IOUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * han训练模式
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/5/14
 */
@Slf4j
public class HanTrain implements NlpTrain {
    private static final String SER = "seg.ser";
    private TrainConfig trainConfig;
    private NaiveBayesClassifier classifier;

    @Override
    public String classify(String source, String classify) {
        //分类的结果 返回类型值
        return classifier.classify(source);
    }

    @Override
    public NlpTrain initial(TrainConfig trainConfig) {
        this.trainConfig = trainConfig;
        this.classifier = new NaiveBayesClassifier((NaiveBayesModel) IOUtil.readObjectFrom(trainConfig.getModelPath()));
        return this;
    }

    @Override
    public Map<String, Double> predict(String source) {
        return classifier.predict(source);
    }

    @Override
    public void train() throws IOException {
        NaiveBayesModel naiveBayesModel = trainOrLoadModel();
        this.classifier = new NaiveBayesClassifier(naiveBayesModel);
    }

    /**
     * 模型
     *
     * @return 模型
     * @throws IOException IOException
     */
    private NaiveBayesModel trainOrLoadModel() throws IOException {
        File corpusFolder = new File(trainConfig.getCorpusPath());
        File modelFolder = new File(Optional.ofNullable(trainConfig.getModelPath()).orElse(""), SER);

        if (trainConfig.isDeleteModel() && modelFolder.exists()) {
            modelFolder.delete();
        }
        NaiveBayesModel model = (NaiveBayesModel) IOUtil.readObjectFrom(modelFolder.getAbsolutePath());
        if (model != null) {
            return model;
        }

        if (!corpusFolder.exists() || !corpusFolder.isDirectory()) {
            log.error("没有文本分类语料");
            throw new IllegalArgumentException("没有文本分类语料");
        }

        // 创建分类器，更高级的功能请参考IClassifier的接口定义
        IClassifier classifier = new NaiveBayesClassifier();
        // 训练后的模型支持持久化，下次就不必训练了
        classifier.train(trainConfig.getCorpusPath());
        model = (NaiveBayesModel) classifier.getModel();
        IOUtil.saveObjectTo(model, trainConfig.getModelPath() + "/" + SER);
        return model;
    }
}
