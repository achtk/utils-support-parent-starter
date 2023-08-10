package com.chua.arc.support.engine;

import com.arcsoft.face.*;
import com.arcsoft.face.toolkit.ImageFactory;
import com.arcsoft.face.toolkit.ImageInfo;
import com.arcsoft.face.toolkit.ImageInfoEx;
import com.chua.common.support.constant.BoundingBox;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.lang.function.*;
import com.chua.common.support.pojo.Shape;
import com.google.common.collect.Lists;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * 人体属性
 *
 * @author CH
 */
public class ArcBodyAttribute implements BodyAttribute {
    private final FaceEngine faceEngine;
    FunctionConfiguration configuration = new FunctionConfiguration();

    {
        configuration.setSupportAge(true);
        configuration.setSupportFaceDetect(true);
        configuration.setSupportGender(true);
        configuration.setSupportLiveness(true);
    }

    public ArcBodyAttribute(FaceEngine faceEngine) {
        this.faceEngine = faceEngine;

    }

    @Override
    public List<Body> detect(Object face) {
        List<FaceInfo> faceInfoList = new ArrayList<>();
        ImageInfo imageInfo = ImageFactory.bufferedImage2ImageInfo(Converter.convertIfNecessary(face, BufferedImage.class));
        ImageInfoEx imageInfoEx = new ImageInfoEx();
        imageInfoEx.setHeight(imageInfo.getHeight());
        imageInfoEx.setWidth(imageInfo.getWidth());
        imageInfoEx.setImageFormat(imageInfo.getImageFormat());
        imageInfoEx.setImageDataPlanes(new byte[][]{imageInfo.getImageData()});
        imageInfoEx.setImageStrides(new int[]{imageInfo.getWidth() * 3});
        faceEngine.process(imageInfoEx, faceInfoList, configuration);
        //性别检测
        List<GenderInfo> genderInfoList = new ArrayList<>();
        faceEngine.getGender(genderInfoList);
        //年龄检测
        List<AgeInfo> ageInfoList = new ArrayList<>();
        faceEngine.getAge(ageInfoList);
        //活体检测
        List<LivenessInfo> livenessInfoList = new ArrayList<>();
        faceEngine.getLiveness(livenessInfoList);

        List<Body> rs = new ArrayList<>();
        for (int i = 0; i < faceInfoList.size(); i++) {
            FaceInfo faceInfo = faceInfoList.get(i);
            Body item = new Body();
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

            try {
                item.setGender(new StandardGender(genderInfoList.get(i).getGender()));
            } catch (Exception ignore) {
            }

            try {
                item.setAge(new StandardAge(ageInfoList.get(i).getAge()));
            } catch (Exception ignore) {
            }

            try {
                item.setLiveness(new StandardLiveness(livenessInfoList.get(i).getLiveness()));
            } catch (Exception ignore) {
            }
            rs.add(item);
        }
        return rs;
    }
}
