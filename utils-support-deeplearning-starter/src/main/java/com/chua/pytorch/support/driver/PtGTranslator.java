package com.chua.pytorch.support.driver;


import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.util.NDImageUtils;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;

import java.util.List;
import java.util.Map;

public class PtGTranslator implements Translator<List, Image> {

    private NDArray copy;

    public PtGTranslator() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NDList processInput(TranslatorContext ctx, List input) {
        NDArray source = ((Image) input.get(0)).toNDArray(ctx.getNDManager());
        source = NDImageUtils.resize(source, 256, 256);
        source = source.div(255);
        source = source.transpose(2, 0, 1);
        source = source.toType(DataType.FLOAT32, false);
        source = source.broadcast(new Shape(1, 3, 256, 256));

    	/*Shape shape = source.getShape();
    	source = source.broadcast(new Shape(1,shape.get(0),shape.get(1),shape.get(2)));*/

        Map<String, NDArray> kp_driving = (Map<String, NDArray>) input.get(1);
        NDArray kpDrivingV = kp_driving.get("value");
        kpDrivingV = kpDrivingV.expandDims(0);
//        kp_driving_v = kp_driving_v.broadcast(new Shape(1,10,2));
        NDArray kpDrivingJ = kp_driving.get("jacobian");
        kpDrivingJ = kpDrivingJ.expandDims(0);
//        kp_driving_j = kp_driving_j.broadcast(new Shape(1,10,2,2));


        Map<String, NDArray> kp_source = (Map<String, NDArray>) input.get(2);
        NDArray kpSourceV = kp_source.get("value");
        kpSourceV = kpSourceV.expandDims(0);
//        kp_source_v = kp_source_v.broadcast(new Shape(1,10,2));
        NDArray kpSourceJ = kp_source.get("jacobian");
        kpSourceJ = kpSourceJ.expandDims(0);
//        kp_source_j = kp_source_j.broadcast(new Shape(1,10,2,2));


        Map<String, NDArray> kp_driving_initial = (Map<String, NDArray>) input.get(3);
        NDArray kpInitialV = kp_driving_initial.get("value");
        kpInitialV = kpInitialV.expandDims(0);
//        kp_initial_v = kp_initial_v.broadcast(new Shape(1,10,2));
        NDArray kpInitialJ = kp_driving_initial.get("jacobian");
        kpInitialJ = kpInitialJ.expandDims(0);
//        kp_initial_j = kp_initial_j.broadcast(new Shape(1,10,2,2));

        NDList re = new NDList();
        re.add(source);
        re.add(kpDrivingV);
        re.add(kpDrivingJ);
        re.add(kpSourceV);
        re.add(kpSourceJ);
        re.add(kpInitialV);
        re.add(kpInitialJ);

        return re;
    }

    @Override
    public Image processOutput(TranslatorContext ctx, NDList list) {
        for (NDArray ig : list) {
            if (ig.getName().equals("prediction")) {
                NDArray img = ig.get(0);
                img = img.mul(255).toType(DataType.UINT8, true);
                //System.out.println(img.toDebugString(1000000000, 1000, 1000, 1000));
                //saveBoundingBoxImage(ImageFactory.getInstance().fromNDArray(img));
                return ImageFactory.getInstance().fromNDArray(img);
            }
        }
        return null;
    }

    @Override
    public Batchifier getBatchifier() {
        return null;
    }

}