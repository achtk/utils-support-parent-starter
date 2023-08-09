package com.chua.paddlepaddle.support.coco;

import ai.djl.Model;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.BoundingBox;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.output.Rectangle;
import ai.djl.modality.cv.util.NDImageUtils;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import ai.djl.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CH
 */
public class TrafficTranslator implements Translator<Image, DetectedObjects> {

    private List<String> className;

    TrafficTranslator() {
    }

    @Override
    public void prepare(TranslatorContext ctx) throws IOException {
        Model model = ctx.getModel();
        try (InputStream is = model.getArtifact("label_file.txt").openStream()) {
            className = Utils.readLines(is, true);
            //            classes.add(0, "blank");
            //            classes.add("");
        }
    }

    @Override
    public DetectedObjects processOutput(TranslatorContext ctx, NDList list) {
        return processImageOutput(list);
    }

    @Override
    public NDList processInput(TranslatorContext ctx, Image input) {
        NDArray array = input.toNDArray(ctx.getNDManager(), Image.Flag.COLOR);
        array = NDImageUtils.resize(array, 512, 512);
        if (!array.getDataType().equals(DataType.FLOAT32)) {
            array = array.toType(DataType.FLOAT32, false);
        }
        //      array = array.div(255f);
        NDArray mean = ctx.getNDManager().create(new float[]{104f, 117f, 123f}, new Shape(1, 1, 3));
        mean = mean.getNDArrayInternal().getArray();
        NDArray std = ctx.getNDManager().create(new float[]{1f, 1f, 1f}, new Shape(1, 1, 3));
        std = std.getNDArrayInternal().getArray();
        array = array.sub(mean);
        array = array.div(std);

        array = array.transpose(2, 0, 1); // HWC -> CHW RGB
        array = array.expandDims(0);

        return new NDList(array);
    }

    @Override
    public Batchifier getBatchifier() {
        return null;
    }

    DetectedObjects processImageOutput(NDList list) {
        NDArray result = list.singletonOrThrow();
        float[] probabilities = result.get(":,1").toFloatArray();
        List<String> names = new ArrayList<>();
        List<Double> prob = new ArrayList<>();
        List<BoundingBox> boxes = new ArrayList<>();
        for (int i = 0; i < probabilities.length; i++) {
            if (probabilities[i] < 0.55) continue;

            float[] array = result.get(i).toFloatArray();
            //        [  0.          0.9627503 172.78745    22.62915   420.2703    919.949    ]
            //        [  0.          0.8364255 497.77234   161.08307   594.4088    480.63745  ]
            //        [  0.          0.7247823  94.354065  177.53668   169.24417   429.2456   ]
            //        [  0.          0.5549363  18.81821   209.29712   116.40645   471.8595   ]
            // 1-person 行人 2-bicycle 自行车 3-car 小汽车 4-motorcycle 摩托车 6-bus 公共汽车 8-truck 货车

            int index = (int) array[0];
            names.add(className.get(index));
            // array[0] category_id
            // array[1] confidence
            // bbox
            // array[2]
            // array[3]
            // array[4]
            // array[5]
            prob.add((double) probabilities[i]);
            // x, y , w , h
            // dt['left'], dt['top'], dt['right'], dt['bottom'] = clip_bbox(bbox, org_img_width,
            // org_img_height)
            boxes.add(new Rectangle(array[2], array[3], array[4] - array[2], array[5] - array[3]));
        }
        return new DetectedObjects(names, prob, boxes);
    }
}

