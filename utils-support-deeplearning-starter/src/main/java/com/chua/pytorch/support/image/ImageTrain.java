package com.chua.pytorch.support.image;

import ai.djl.Model;
import ai.djl.basicdataset.cv.classification.ImageFolder;
import ai.djl.metric.Metrics;
import ai.djl.ndarray.types.Shape;
import ai.djl.training.Trainer;
import ai.djl.training.TrainingConfig;
import ai.djl.training.TrainingResult;
import ai.djl.training.dataset.Dataset;
import ai.djl.training.dataset.RandomAccessDataset;
import com.chua.pytorch.support.AbstractPytorchTrain;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static ai.djl.training.EasyTrain.fit;

/**
 * image train
 *
 * @author CH
 */
public class ImageTrain extends AbstractPytorchTrain<ImageTrain> {

    private static final String DEFAULT_MODEL = "https://aias-home.oss-cn-beijing.aliyuncs.com/models/resnet50_v2.zip";

    public ImageTrain(int width, int height) {
        super(width, height);
        this.modelUrl = DEFAULT_MODEL;
    }


    @Override
    public void train(String trainPath, String modelPath) throws Exception {
        // 识别训练数据的位置
        String trainingDatasetRoot = trainPath + "/train";
        // 识别验证数据的位置
        String validateDatasetRoot = trainPath + "/validation";
        Dataset trainingDataset = null;
        List<String> synset = null;
        Dataset validateDataset = null;
        if (1 == trainType) {
            if (!Files.exists(Paths.get(trainingDatasetRoot)) || !Files.exists(Paths.get(validateDatasetRoot))) {
                random(trainPath);
            }
            // 创建训练数据 ImageFolder 数据集
            trainingDataset = initDataset(Dataset.Usage.TRAIN.name(), trainingDatasetRoot, modelPath);
            cs = ((ImageFolder) trainingDataset).getSynset();
            //创建验证数据 ImageFolder 数据集
            validateDataset = initDataset(Dataset.Usage.VALIDATION.name(), validateDatasetRoot, modelPath);
        } else {
            // 创建训练数据 ImageFolder 数据集
            ImageFolder trainingDataset1 = initDataset(Dataset.Usage.TRAIN.name(), trainPath, modelPath);
            RandomAccessDataset[] randomAccessDatasets = trainingDataset1.randomSplit(8, 2);
            cs = trainingDataset1.getSynset();
            trainingDataset = randomAccessDatasets[0];
            validateDataset = randomAccessDatasets[1];
        }


        try (Model model = createModel(cs)) {
            TrainingConfig config = setupTrainingConfig();

            try (Trainer trainer = model.newTrainer(config)) {
                trainer.setMetrics(new Metrics());

                Shape inputShape = new Shape(1, 3, height, width);

                // 根据相应输入的形状初始化训练器
                trainer.initialize(inputShape);

                //在数据中查找模式
                fit(trainer, numEpoch, trainingDataset, validateDataset);

                TrainingResult result = trainer.getTrainingResult();
                //设置模型属性
                model.setProperty("Epoch", String.valueOf(result.getEpoch()));
                model.setProperty(
                        "Accuracy",
                        String.format("%.5f", result.getValidateEvaluation("Accuracy")));
                model.setProperty("Loss", String.format("%.5f", result.getValidateLoss()));
                model.setBlock(model.getBlock());

                // 训练完成后保存模型，为后面的推理做准备
                //模型保存为 xxx-0000.params
                model.save(Paths.get(modelPath), modelName);
            }
        }
    }

}
