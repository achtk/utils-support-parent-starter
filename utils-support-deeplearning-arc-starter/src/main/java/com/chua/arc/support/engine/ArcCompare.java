package com.chua.arc.support.engine;

import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.toolkit.ImageFactory;
import com.arcsoft.face.toolkit.ImageInfo;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.lang.function.Compare;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * 相似度比
 */
public class ArcCompare implements Compare {
    private final FaceEngine faceEngine;

    public ArcCompare(FaceEngine faceEngine) {
        this.faceEngine = faceEngine;
    }

    @Override
    public float calculateSimilar(Object t1, Object t2) {
        //人脸检测
        ImageInfo imageInfo = ImageFactory.bufferedImage2ImageInfo(Converter.convertIfNecessary(t1, BufferedImage.class));
        List<FaceInfo> faceInfoList = new ArrayList<>();
        faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
        //人脸检测2
        ImageInfo imageInfo2 = ImageFactory.bufferedImage2ImageInfo(Converter.convertIfNecessary(t2, BufferedImage.class));
        List<FaceInfo> faceInfoList2 = new ArrayList<>();
        faceEngine.detectFaces(imageInfo2.getImageData(), imageInfo2.getWidth(), imageInfo2.getHeight(), imageInfo2.getImageFormat(), faceInfoList2);
        //特征提取
        FaceFeature feature = new FaceFeature();
        faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList.get(0), feature);


        //特征提取2
        FaceFeature feature1 = new FaceFeature();
        faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList2.get(0), feature);
        //特征比对
        FaceFeature targetFaceFeature = new FaceFeature();
        targetFaceFeature.setFeatureData(feature.getFeatureData());
        FaceFeature sourceFaceFeature = new FaceFeature();
        sourceFaceFeature.setFeatureData(feature1.getFeatureData());
        FaceSimilar faceSimilar = new FaceSimilar();
        faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, faceSimilar);

        return faceSimilar.getScore();
    }
}
