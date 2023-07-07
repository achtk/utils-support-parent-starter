package com.chua.anpr.support.base;

import com.chua.anpr.support.domain.ImageMat;
import com.chua.anpr.support.domain.PlateInfo;
import com.chua.anpr.support.domain.PlateInfo.ParseInfo;

import java.util.Map;

public interface PlateRecognition {

    ParseInfo inference(ImageMat image, Boolean single, Map<String, Object> params);
}
