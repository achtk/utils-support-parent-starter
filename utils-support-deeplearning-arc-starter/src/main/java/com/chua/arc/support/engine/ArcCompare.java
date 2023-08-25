package com.chua.arc.support.engine;

import com.arcsoft.face.*;
import com.arcsoft.face.toolkit.ImageFactory;
import com.arcsoft.face.toolkit.ImageInfo;
import com.chua.common.support.constant.BoundingBox;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.feature.FeatureComparison;
import com.chua.common.support.lang.function.Compare;
import com.chua.common.support.pojo.Shape;

import java.io.File;
import java.util.*;
import java.util.function.Function;

/**
 * 相似度比
 * @author CH
 */
public class ArcCompare implements Compare {
    private final FaceEngine faceEngine;

    public ArcCompare(FaceEngine faceEngine) {
        this.faceEngine = faceEngine;
    }
    public List<PredictResult> calculateSimilar(FaceFeature t1, FaceFeature t2) {
        //特征比对
        FaceFeature targetFaceFeature = new FaceFeature();
        targetFaceFeature.setFeatureData(t1.getFeatureData());
        FaceFeature sourceFaceFeature = new FaceFeature();
        sourceFaceFeature.setFeatureData(t2.getFeatureData());
        FaceSimilar faceSimilar = new FaceSimilar();
        faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, faceSimilar);
        PredictResult item = new PredictResult();
        item.setScore(faceSimilar.getScore());
        return Collections.singletonList(item);
    }

    @Override
    public List<PredictResult> calculateSimilar(Object t1, Object t2) {
        List<PredictResult> rs = new LinkedList<>();
        if(t1 instanceof FaceFeature && t2 instanceof FaceFeature) {
            return calculateSimilar((FaceFeature)t1, (FaceFeature)t2);
        }
        //人脸检测
        ImageInfo imageInfo = ImageFactory.getRGBData(Converter.convertIfNecessary(t1, File.class));
        List<FaceInfo> faceInfoList = new ArrayList<>();
        faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
        if(faceInfoList.isEmpty()) {
            rs.add(PredictResult.empty());
            return rs;
        }
        //人脸检测2
        ImageInfo imageInfo2 = ImageFactory.getRGBData(Converter.convertIfNecessary(t2, File.class));
        List<FaceInfo> faceInfoList2 = new ArrayList<>();
        faceEngine.detectFaces(imageInfo2.getImageData(), imageInfo2.getWidth(), imageInfo2.getHeight(), imageInfo2.getImageFormat(), faceInfoList2);
        if(faceInfoList2.isEmpty()) {
            rs.add(PredictResult.empty());
            return rs;
        }
        Map<FaceInfo, FaceFeature> cache = new HashMap<>(faceInfoList.size() * faceInfoList2.size());
        for (FaceInfo faceInfo : faceInfoList) {
            Rect rect = faceInfo.getRect();
            List<Shape> shapes = new LinkedList<>();
            shapes.add( new Shape(rect.left, rect.top));
            shapes.add( new Shape(rect.right, rect.top));
            shapes.add( new Shape(rect.right, rect.bottom));
            shapes.add( new Shape(rect.left, rect.bottom));
            BoundingBox boundingBox1 = BoundingBox.builder()
                    .height(Math.abs(rect.top - rect.bottom))
                    .width(Math.abs(rect.right - rect.left))
                    .corners(shapes)
                    .build();

            //特征提取
            FaceFeature feature = new FaceFeature();
            faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfo, feature);
            for (FaceInfo faceInfo2 : faceInfoList2) {
                Rect rect2 = faceInfo2.getRect();
                PredictResult item = new PredictResult();
                //特征提取2
                FaceFeature feature1 = cache.computeIfAbsent(faceInfo2, new Function<FaceInfo, FaceFeature>() {
                    @Override
                    public FaceFeature apply(FaceInfo faceInfo) {
                        FaceFeature feature1 = new FaceFeature();
                        faceEngine.extractFaceFeature(imageInfo2.getImageData(), imageInfo2.getWidth(), imageInfo2.getHeight(), imageInfo2.getImageFormat(), faceInfo2, feature1);
                        return feature1;
                    }
                });
                //特征比对
                FaceFeature targetFaceFeature = new FaceFeature();
                targetFaceFeature.setFeatureData(feature.getFeatureData());
                FaceFeature sourceFaceFeature = new FaceFeature();
                sourceFaceFeature.setFeatureData(feature1.getFeatureData());
                FaceSimilar faceSimilar = new FaceSimilar();
                faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, faceSimilar);
                List<Shape> shapes2 = new LinkedList<>();
                shapes2.add( new Shape(rect2.left, rect2.top));
                shapes2.add( new Shape(rect2.right, rect2.top));
                shapes2.add( new Shape(rect2.right, rect2.bottom));
                shapes2.add( new Shape(rect2.left, rect2.bottom));
                item.setSign1(boundingBox1);
                item.setSign2(BoundingBox.builder()
                        .height(Math.abs(rect2.top - rect2.bottom))
                        .width(Math.abs(rect2.right - rect2.left))
                        .corners(shapes2)
                        .build());
                item.setScore(faceSimilar.getScore());
                item.setClsScore(FeatureComparison.calculateSimilar(feature.getFeatureData(), feature1.getFeatureData()));
                rs.add(item);
            }
        }


        return rs;
    }
}
