package com.chua.anpr.support.extract;

import com.chua.anpr.support.domain.ExtParam;
import com.chua.anpr.support.domain.ImageMat;
import com.chua.anpr.support.domain.PlateImage;

import java.util.Map;

public interface PlateExtractor {

    /**
     * 车牌特征提取
     * @param image
     * @param extParam
     * @param params
     * @return
     */
    public PlateImage extract(ImageMat image, ExtParam extParam, Map<String, Object> params);

}
