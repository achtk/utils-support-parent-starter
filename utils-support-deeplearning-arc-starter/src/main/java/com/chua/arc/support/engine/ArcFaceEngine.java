package com.chua.arc.support.engine;

import com.arcsoft.face.EngineConfiguration;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FunctionConfiguration;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectOrient;
import com.arcsoft.face.enums.ErrorInfo;
import com.chua.common.support.engine.Engine;
import com.chua.common.support.engine.EngineBase;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.feature.detector.Detector;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.lang.function.BodyAttribute;
import com.chua.common.support.lang.function.Ir;
import com.chua.common.support.lang.function.Similar;
import com.chua.common.support.os.Platform;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

/**
 * arcface模型
 *
 * @author CH
 */
@Slf4j
public class ArcFaceEngine extends EngineBase implements Engine, InitializingAware {

    private FaceEngine faceEngine;
    private static final String model = "libarcsoft_face";

    public ArcFaceEngine(DetectionConfiguration configuration) {
        super(configuration);
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
        if (Detector.class.isAssignableFrom(target)) {
            return (T) new ArcDetector(faceEngine);
        }

        if (Similar.class.isAssignableFrom(target)) {
            return (T) new ArcSimilar(faceEngine);
        }

        if (BodyAttribute.class.isAssignableFrom(target)) {
            return (T) new ArcBodyAttribute(faceEngine);
        }

        if (Ir.class.isAssignableFrom(target)) {
            return (T) new ArcIr(faceEngine);
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        faceEngine.unInit();
    }
}
