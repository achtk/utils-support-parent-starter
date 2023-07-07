package com.chua.anpr.support.extract;

import com.chua.anpr.support.base.PlateDetection;
import com.chua.anpr.support.base.PlateRecognition;
import com.chua.anpr.support.domain.ExtParam;
import com.chua.anpr.support.domain.ImageMat;
import com.chua.anpr.support.domain.PlateImage;
import com.chua.anpr.support.domain.PlateInfo;
import com.chua.anpr.support.utils.CropUtil;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PlateExtractorImpl implements PlateExtractor {

    private PlateDetection plateDetection;
    private PlateRecognition plateRecognition;

    public PlateExtractorImpl(PlateDetection plateDetection, PlateRecognition plateRecognition) {
        this.plateDetection = plateDetection;
        this.plateRecognition = plateRecognition;
    }

    @Override
    public PlateImage extract(ImageMat image, ExtParam extParam, Map<String, Object> params) {
        List<PlateInfo> plateInfos = plateDetection.inference(image, extParam.getScoreTh(),extParam.getIouTh(), new HashMap<>());
        if(plateInfos.isEmpty()){
            //防止由于车牌占用大，导致检测模型识别失败
            Mat tempMat = new Mat();
            int t = image.toCvMat().height() / 2;
            int b = image.toCvMat().height() / 2;
            int l = image.toCvMat().width()  / 2;
            int r = image.toCvMat().width()  / 2;
            Core.copyMakeBorder(image.toCvMat(), tempMat, t, b, l, r, Core.BORDER_CONSTANT);
            plateInfos = plateDetection.inference(ImageMat.fromCVMat(tempMat), extParam.getScoreTh(),extParam.getIouTh(), new HashMap<>());
            for(PlateInfo plateInfo : plateInfos){
                plateInfo.box = plateInfo.box.move(l, 0, t, 0);
                plateInfo.box.leftTop.x = Math.max(0, plateInfo.box.leftTop.x);
                plateInfo.box.leftTop.y = Math.max(0, plateInfo.box.leftTop.y);
                plateInfo.box.rightTop.x = Math.min(image.getWidth(), plateInfo.box.rightTop.x);
                plateInfo.box.rightTop.y = Math.max(0, plateInfo.box.rightTop.y);
                plateInfo.box.rightBottom.x = Math.min(image.getWidth(), plateInfo.box.rightBottom.x);
                plateInfo.box.rightBottom.y = Math.min(image.getHeight(), plateInfo.box.rightBottom.y);
                plateInfo.box.leftBottom.x = Math.max(0, plateInfo.box.leftBottom.x);
                plateInfo.box.leftBottom.y = Math.min(image.getHeight(), plateInfo.box.leftBottom.y);
            }
            tempMat.release();
        }
        //取车牌topK
        int topK = (extParam.getTopK()  > 0) ? extParam.getTopK() : 5;
        if(plateInfos.size() > topK){
            plateInfos = plateInfos.subList(0, topK);
        }
        //解析车牌信息
        for(PlateInfo plateInfo : plateInfos){
            Mat crop = CropUtil.crop(image.toCvMat(), plateInfo.box);
            plateInfo.parseInfo = plateRecognition.inference(ImageMat.fromCVMat(crop), plateInfo.single, new HashMap<>());
        }
        //清洗数据
        Iterator<PlateInfo> iterator = plateInfos.iterator();
         while (iterator.hasNext()) {
             PlateInfo.ParseInfo parseInfo = iterator.next().parseInfo;
             if(null == parseInfo || null == parseInfo.plateNo || parseInfo.plateNo.length() <= 4){
                 iterator.remove();
             }
         }
        //返回数据
        return PlateImage.build(image.toBase64AndNoReleaseMat(), plateInfos);
    }
}
