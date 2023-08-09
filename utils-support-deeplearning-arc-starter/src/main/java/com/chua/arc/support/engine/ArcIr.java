package com.chua.arc.support.engine;

import com.arcsoft.face.*;
import com.arcsoft.face.toolkit.ImageInfo;
import com.chua.common.support.constant.BoundingBox;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.lang.function.Body;
import com.chua.common.support.lang.function.Ir;
import com.chua.common.support.lang.function.StandardLiveness;
import com.chua.common.support.pojo.Shape;
import com.google.common.collect.Lists;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.arcsoft.face.toolkit.ImageFactory.getGrayData;

/**
 * IR/RGB活体检测
 * 检测是否真人，预防攻击
 *
 * @author CH
 */
public class ArcIr implements Ir {
    private final FaceEngine faceEngine;
    FunctionConfiguration configuration = new FunctionConfiguration();

    {
        configuration.setSupportAge(false);
        configuration.setSupportFaceDetect(false);
        configuration.setSupportGender(false);
        configuration.setSupportLiveness(false);
        configuration.setSupportIRLiveness(true);
    }

    public ArcIr(FaceEngine faceEngine) {
        this.faceEngine = faceEngine;
    }

    @Override
    public List<Body> live(Object face) {
        List<FaceInfo> faceInfoListGray = new ArrayList<FaceInfo>();
        //IR属性处理
        ImageInfo imageInfoGray = getGrayData(Converter.convertIfNecessary(face, File.class));
        faceEngine.processIr(imageInfoGray.getImageData(), imageInfoGray.getWidth(), imageInfoGray.getHeight(), imageInfoGray.getImageFormat(),
                faceInfoListGray, configuration);
        List<Body> rs = new ArrayList<>();
        //IR活体检测
        List<IrLivenessInfo> irLivenessInfo = new ArrayList<>();
        faceEngine.getLivenessIr(irLivenessInfo);
        for (int i = 0; i < faceInfoListGray.size(); i++) {
            FaceInfo faceInfo = faceInfoListGray.get(i);
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
                item.setLiveness(new StandardLiveness(irLivenessInfo.get(i).getLiveness()));
            } catch (Exception ignore) {
            }
            rs.add(item);
        }
        return rs;
    }
}
