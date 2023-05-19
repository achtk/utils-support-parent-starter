package com.chua.pytorch.support.animals;

import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.utils.StringUtils;
import com.chua.pytorch.support.AbstractPytorchDetector;
import com.chua.pytorch.support.utils.LocationUtils;
import lombok.SneakyThrows;

import java.util.LinkedList;
import java.util.List;

/**
 * 动物分类
 *
 * @author CH
 */
public class LightAnimalsClassDetector extends AbstractPytorchDetector<Classifications> {
    @SneakyThrows
    public LightAnimalsClassDetector(DetectionConfiguration configuration) {
        super(configuration,
                new AnimalTranslator(),
                "PaddlePaddle",
                "inference",
                StringUtils.defaultString(configuration.modelPath(), "light-animals"),
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/mobilenet_animals.zip",
                true);
    }

    @Override
    protected Class<Classifications> type() {
        return Classifications.class;
    }

    @Override
    protected List<PredictResult> toDetect(Classifications classifications, Image img) {
        List<PredictResult> results = new LinkedList<>();
        List<Classifications.Classification> items = classifications.items();
        double sum = 0;
        double max = 0;
        double[] probArr = new double[items.size()];

        for (int i = 0; i < items.size(); i++) {
            Classifications.Classification item = items.get(i);
            double prob = item.getProbability();
            probArr[i] = prob;
            if (prob > max) {
                max = prob;
            }
        }

        for (int i = 0; i < items.size(); i++) {
            probArr[i] = Math.exp(probArr[i] - max);
            sum = sum + probArr[i];
        }

        for (int i = 0; i < items.size(); i++) {
            Classifications.Classification item = items.get(i);
            results.add(LocationUtils.convertPredictResult(item, img));
        }

        return results;
    }


    @Override
    public void close() throws Exception {
        super.close();
    }

}
