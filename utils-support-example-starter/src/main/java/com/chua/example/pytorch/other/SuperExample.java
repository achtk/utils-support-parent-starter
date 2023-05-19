package com.chua.example.pytorch.other;

import ai.djl.modality.cv.Image;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.pytorch.support.resolution.SuperResolver;
import com.chua.pytorch.support.utils.LocationUtils;

/**
 * @author CH
 */
public class SuperExample {

    public static void main(String[] args) throws Exception {
        SuperResolver superResolver = new SuperResolver(DetectionConfiguration.builder().build());
        Image image = superResolver.resolve(LocationUtils.getImage("Z:\\works\\resource\\f62e02b5eb39e55b4d4c3b91be6dc58.jpg"));
        LocationUtils.saveImage(image, "Z:\\works\\resource\\f62e02b5eb39e55b4d4c3b91be6dc58_out.jpg");
    }
}
