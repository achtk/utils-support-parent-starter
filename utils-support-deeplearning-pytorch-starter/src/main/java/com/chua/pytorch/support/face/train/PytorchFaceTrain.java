package com.chua.pytorch.support.face.train;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import com.chua.common.support.feature.Feature;
import com.chua.common.support.train.AbstractTrain;
import com.chua.pytorch.support.face.net.FaceLabelNet;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * pytorch
 *
 * @author CH
 */
@Slf4j
public class PytorchFaceTrain extends AbstractTrain<PytorchFaceTrain> {

    private final Feature<float[]> feature;
    private final FaceLabelNet faceLabelNet;
    private final String oldModelPath;

    public PytorchFaceTrain(Feature<float[]> feature,
                            FaceLabelNet faceLabelNet,
                            String oldModelPath) {
        super(0, 0);
        this.feature = feature;
        this.faceLabelNet = faceLabelNet;
        this.oldModelPath = oldModelPath;
    }

    public void train(String modelName, String trainPath, String modelPath) throws Exception {
        try {
            faceLabelNet.load(oldModelPath);
        } catch (Exception ignored) {
        }

        dataSet(trainPath, faceLabelNet);
        faceLabelNet.saveNet(new File(modelPath, modelName).getAbsolutePath());
    }

    /**
     * 数据集
     *
     * @param trainPath    训练路径
     * @param faceLabelNet 网络
     */
    private void dataSet(String trainPath, FaceLabelNet faceLabelNet) {
        int i = 0;
        File data = new File(trainPath);
        if (data.isDirectory()) {
            File[] labs = data.listFiles();
            if (labs != null && labs.length > 0) {
                for (File lab : labs) {
                    if (lab.isDirectory()) {
                        i++;
                        String labName = lab.getName();
                        log.info("labName: {}, index: {}", labName, i);
                        loadData(faceLabelNet, lab, labName);
                    }
                }
            } else {
                throw new IllegalArgumentException("Path:" + trainPath + " is not empty.");
            }
        } else {
            throw new IllegalArgumentException("Path:" + trainPath + " is not data directory.");
        }
    }

    /**
     * 加载数据
     *
     * @param faceLabelNet 网络
     * @param lab          标签
     * @param labName      名称
     */
    private void loadData(FaceLabelNet faceLabelNet, File lab, String labName) {
        File[] faces = lab.listFiles();
        if (faces != null) {
            for (File faceF : faces) {
                Path facePath = Paths.get(faceF.getAbsolutePath());
                try {
                    Image img = ImageFactory.getInstance().fromFile(facePath);
                    float[] ndArray = feature.predict(img);
                    faceLabelNet.addFeature(labName, ndArray);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void train(String trainPath, String modelPath) throws Exception {
        train(modelName, trainPath, modelPath);
    }
}
