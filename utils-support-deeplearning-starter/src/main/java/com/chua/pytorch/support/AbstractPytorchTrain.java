package com.chua.pytorch.support;

import ai.djl.MalformedModelException;
import ai.djl.Model;
import ai.djl.basicdataset.cv.classification.ImageFolder;
import ai.djl.basicmodelzoo.cv.classification.ResNetV1;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.Block;
import ai.djl.nn.Blocks;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.SymbolBlock;
import ai.djl.nn.core.Linear;
import ai.djl.repository.Repository;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.Trainer;
import ai.djl.training.evaluator.Accuracy;
import ai.djl.training.listener.TrainingListener;
import ai.djl.training.loss.Loss;
import ai.djl.training.util.ProgressBar;
import com.chua.common.support.train.AbstractTrain;
import com.chua.common.support.train.Listener;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.FileUtils;
import com.google.common.base.Strings;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * train
 *
 * @author CH
 */
public abstract class AbstractPytorchTrain<Chain> extends AbstractTrain<Chain> {
    protected List<String> cs;

    public AbstractPytorchTrain(int width, int height) {
        super(width, height);
    }

    @Override
    public List<String> label() {
        return cs;
    }

    public Model createModel(List<String> cs) {
        if (0 == modeType) {
            return getModel();
        }

        try {
            return getResnetModel(cs);
        } catch (IOException | ModelNotFoundException | MalformedModelException e) {
            throw new RuntimeException(e);
        }
    }

    private Model getModel() {
        //创建一个空模型的新实例
        Model model = Model.newInstance(modelName);
        //是构建神经网络所需的可组合单元；可以像像乐高积木一样将它们连结在一起，
        //形成一个复杂的网络
        Block resNet50 =
                //构建网络
                ResNetV1.builder()
                        .setImageShape(new Shape(3, height, width))
                        .setNumLayers(50)
                        .setOutSize(CollectionUtils.isEmpty(cs) ? numOfOutput : cs.size())
                        .build();

        //将神经网络设置到模型中
        model.setBlock(resNet50);
        return model;
    }

    private Model getResnetModel(List<String> cs)
            throws IOException, ModelNotFoundException, MalformedModelException {
        ImageClassificationTranslator translator =
                ImageClassificationTranslator.builder()
                        .addTransform(new ToTensor())
                        .optSynset(cs)
                        .optApplySoftmax(true)
                        .build();

        Criteria.Builder<Image, Classifications> optModelName = Criteria.builder()
                .setTypes(Image.class, Classifications.class)
                .optTranslator(translator)
                .optProgress(new ProgressBar())
                .optModelName(modelName);

        if (!Strings.isNullOrEmpty(modelUrl)) {
            optModelName.optModelUrls(modelUrl);
        }

        Criteria.Builder<Image, Classifications> builder = optModelName;
        // load the model
        Model model = ModelZoo.loadModel(builder.build());

        SequentialBlock newBlock = new SequentialBlock();
        SymbolBlock block = (SymbolBlock) model.getBlock();
        block.removeLastBlock();
        newBlock.add(block);
        // the original model don't include the flatten
        // so apply the flatten here
        newBlock.add(Blocks.batchFlattenBlock());
        newBlock.add(Linear.builder().setUnits(10).build());
        model.setBlock(newBlock);
        return model;
    }


    protected DefaultTrainingConfig setupTrainingConfig() {
        DefaultTrainingConfig trainingConfig = new DefaultTrainingConfig(Loss.softmaxCrossEntropyLoss())
                .addEvaluator(new Accuracy())
                .optDevices(null)
                .addTrainingListeners(TrainingListener.Defaults.logging());
        for (Listener listener : list) {
            trainingConfig.addTrainingListeners(new TrainingListener() {
                @Override
                public void onEpoch(Trainer trainer) {

                }

                @Override
                public void onTrainingBatch(Trainer trainer, BatchData batchData) {
                    listener.onTrainingBatch(trainer, batchData);
                }

                @Override
                public void onValidationBatch(Trainer trainer, BatchData batchData) {
                    listener.onValidationBatch(trainer, batchData);
                }

                @Override
                public void onTrainingBegin(Trainer trainer) {
                    listener.onTrainingBegin(trainer);
                }

                @Override
                public void onTrainingEnd(Trainer trainer) {
                    listener.onTrainingEnd(trainer);
                }
            });
        }

        return trainingConfig;
    }

    @SneakyThrows
    protected ImageFolder initDataset(String type, String path, String modelPath) throws IOException {
        ImageFolder dataset = ImageFolder.builder()
                .setRepository(Repository.newInstance(type, new File(path).toURI().toString()))
                .optMaxDepth(10)
                .addTransform(new Resize(width, height))
                .addTransform(new ToTensor())
                .setSampling(batchSize, true)
                .build();

        dataset.prepare(new ProgressBar());
        if ("TRAIN".equals(type)) {
            this.cs = dataset.getSynset();
            FileUtils.writeUtf8Lines(cs, Paths.get(modelPath).resolve("synset.txt").toUri().getPath());
        }
        return dataset;
    }


}
