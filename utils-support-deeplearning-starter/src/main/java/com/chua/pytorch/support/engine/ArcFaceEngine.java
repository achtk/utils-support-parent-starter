package com.chua.pytorch.support.engine;

import com.arcsoft.face.*;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectOrient;
import com.arcsoft.face.enums.ErrorInfo;
import com.arcsoft.face.toolkit.ImageInfo;
import com.chua.common.support.constant.BoundingBox;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.feature.detector.Detector;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.lang.function.Engine;
import com.chua.common.support.lang.function.Similar;
import com.chua.common.support.os.Platform;
import com.chua.common.support.pojo.Shape;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.arcsoft.face.toolkit.ImageFactory.getRGBData;

/**
 * arcface模型
 * @author CH
 */
@Slf4j
public class ArcFaceEngine implements Engine, InitializingAware {

    private DetectionConfiguration configuration;
    private FaceEngine faceEngine;
    private static String model = "libarcsoft_face";

    public ArcFaceEngine(DetectionConfiguration configuration) {
        this.configuration = configuration;
        this.afterPropertiesSet();

    }


    @Override
    public void afterPropertiesSet() {
        Path path = Platform.extractNativeBinary(model, configuration.cachePath());
        this.faceEngine = new FaceEngine(path.getParent().toFile().getPath());
        //激活引擎
        int errorCode = faceEngine.activeOnline("9gN1dRr4QVGZztS8iqwc2sBiLDGRUjRgfj3BiZsX21wk", "25TpjKV5ZRgthaJtCJWuGTonCDs7pBTRkVmHm4DKNzH9");

        if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
            System.out.println("引擎激活失败");
        }
        //引擎配置
        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
        engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_ALL_OUT);
        engineConfiguration.setDetectFaceMaxNum(10);
        engineConfiguration.setDetectFaceScaleVal(16);
        //功能配置
        FunctionConfiguration functionConfiguration = new FunctionConfiguration();
        functionConfiguration.setSupportAge(true);
        functionConfiguration.setSupportFace3dAngle(true);
        functionConfiguration.setSupportFaceDetect(true);
        functionConfiguration.setSupportFaceRecognition(true);
        functionConfiguration.setSupportGender(true);
        functionConfiguration.setSupportLiveness(true);
        functionConfiguration.setSupportIRLiveness(true);
        engineConfiguration.setFunctionConfiguration(functionConfiguration);
        //初始化引擎
        errorCode = faceEngine.init(engineConfiguration);

        if (errorCode != ErrorInfo.MOK.getValue()) {
            log.error("初始化引擎失败");
        }

    }

    @Override
    @SuppressWarnings("ALL")
    public <T> T get(Class<T> target) {
        if(Detector.class.isAssignableFrom(target)) {
            return (T) new Detector() {

                @Override
                public void close() throws Exception {

                }

                @Override
                public List<PredictResult> detect(Object face) {
                    List<PredictResult> rs = new LinkedList<>();
                    ImageInfo imageInfo = getRGBData(Converter.convertIfNecessary(face, File.class));
                    List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
                    faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
                    for (FaceInfo faceInfo : faceInfoList) {
                        PredictResult item = new PredictResult();
                        Rect rect = faceInfo.getRect();
                        item.setScore(faceInfo.getFaceId());
                        item.setNdArray(BoundingBox.builder()
                                        .height(Math.abs(rect.top - rect.bottom))
                                        .width(Math.abs(rect.right - rect.left))
                                        .corners(Lists.newArrayList(
                                                new Shape(rect.left, rect.top),
                                                new Shape(rect.left, rect.bottom),
                                                new Shape(rect.right, rect.top),
                                                new Shape(rect.right, rect.bottom)
                                        ))
                                .build());
                        rs.add(item);
                    }
                    return rs;
                }

                @Override
                public void detect(Object image, OutputStream outputStream) throws Exception {

                }
            };
        }
        if(Similar.class.isAssignableFrom(target)) {
            return (T) new Similar<byte[]>() {

                @Override
                public float calculateSimilar(byte[] t1, byte[] t2) {
                    FaceFeature targetFaceFeature = new FaceFeature();
                    targetFaceFeature.setFeatureData(t1);
                    FaceFeature sourceFaceFeature = new FaceFeature();
                    sourceFaceFeature.setFeatureData(t2);
                    FaceSimilar faceSimilar = new FaceSimilar();

                     faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, faceSimilar);

                    return faceSimilar.getScore();
                }
            };
        }
        return null;
    }
}
