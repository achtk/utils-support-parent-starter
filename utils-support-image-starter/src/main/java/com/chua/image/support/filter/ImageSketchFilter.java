package com.chua.image.support.filter;

import com.chua.common.support.utils.ImageUtils;
import com.chua.image.support.composite.ColorDodgeComposite;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * 素描滤镜
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/16
 */
public class ImageSketchFilter extends AbstractImageFilter {
    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        src = ImageUtils.convertimagetoArgb(src);
        //图像灰度化
        ImageGrayscaleFilter grayScaleFilter = new ImageGrayscaleFilter();
        BufferedImage grayScale = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
        grayScaleFilter.filter(src, grayScale);
        //灰度图像反色
        BufferedImage inverted = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
        ImageInvertFilter invertFilter = new ImageInvertFilter();
        invertFilter.filter(grayScale, inverted);
        //高斯模糊处理
        ImageGaussianFilter gaussianFilter = new ImageGaussianFilter(20);
        BufferedImage gaussianFiltered = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
        gaussianFilter.filter(inverted, gaussianFiltered);
        // 灰度图像和高斯模糊反向图混合
        ColorDodgeComposite cdc = new ColorDodgeComposite(1.0f);
        CompositeContext cc = cdc.createContext(inverted.getColorModel(), grayScale.getColorModel(), null);
        WritableRaster invertedR = gaussianFiltered.getRaster();
        WritableRaster grayScaleR = grayScale.getRaster();
        BufferedImage composite = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
        WritableRaster colorDodgedR = composite.getRaster();
        cc.compose(invertedR, grayScaleR, colorDodgedR);
        return composite;
    }
}
