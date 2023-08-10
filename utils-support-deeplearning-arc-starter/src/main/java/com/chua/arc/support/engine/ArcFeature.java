package com.chua.arc.support.engine;

import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.toolkit.ImageFactory;
import com.arcsoft.face.toolkit.ImageInfo;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.feature.Feature;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 特征值提取
 * @author CH
 */
public class ArcFeature implements Feature<byte[]> {
    private final FaceEngine faceEngine;

    public ArcFeature(FaceEngine faceEngine) {
        this.faceEngine = faceEngine;
    }

    @Override
    public byte[] predict(Object img) {
        //人脸检测
        ImageInfo imageInfo = ImageFactory.getRGBData(Converter.convertIfNecessary(img, File.class));
        List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
        faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
        FaceFeature faceFeature = new FaceFeature();
        faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList.get(0), faceFeature);
        return faceFeature.getFeatureData();

    }
}
