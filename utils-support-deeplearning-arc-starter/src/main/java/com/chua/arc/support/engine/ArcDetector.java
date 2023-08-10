package com.chua.arc.support.engine;

import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.Rect;
import com.arcsoft.face.toolkit.ImageInfo;
import com.chua.common.support.constant.BoundingBox;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.feature.detector.Detector;
import com.chua.common.support.pojo.Shape;
import com.google.common.collect.Lists;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.arcsoft.face.toolkit.ImageFactory.getRGBData;

/**
 * 人脸检测
 *
 * @author CH
 */
public class ArcDetector implements Detector {
    private final FaceEngine faceEngine;

    public ArcDetector(FaceEngine faceEngine) {
        this.faceEngine = faceEngine;
    }

    @Override
    public List<PredictResult> predict(Object face) {
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

    @Override
    public void close() throws Exception {

    }
}
