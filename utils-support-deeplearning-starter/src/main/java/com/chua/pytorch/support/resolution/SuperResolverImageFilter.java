package com.chua.pytorch.support.resolution;

import ai.djl.modality.cv.Image;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.image.filter.AbstractImageFilter;
import com.chua.pytorch.support.utils.LocationUtils;

import java.awt.image.BufferedImage;

/**
 * 图像分辨率增强
 * @author CH
 */
public class SuperResolverImageFilter extends AbstractImageFilter {
    static SuperResolver superResolver = null;
    static {
        try {
            superResolver = new SuperResolver(DetectionConfiguration.builder().build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        if(null == superResolver) {
            return src;
        }

        Image image = superResolver.resolve(LocationUtils.getImage(src));
        return (BufferedImage) image.getWrappedImage();
    }
}
