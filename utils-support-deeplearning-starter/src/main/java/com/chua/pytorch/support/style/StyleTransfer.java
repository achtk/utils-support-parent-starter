package com.chua.pytorch.support.style;

import ai.djl.modality.cv.Image;
import ai.djl.repository.zoo.Criteria;
import ai.djl.training.util.ProgressBar;
import com.chua.pytorch.support.utils.LocationUtils;
import com.google.common.base.Joiner;
import lombok.Getter;

import java.util.List;

/**
 * 風格
 *
 * @author CH
 */
public class StyleTransfer implements AutoCloseable {

    @Getter
    private final Criteria<Image, Image> criteria;

    public StyleTransfer(Artist artist) {
        String modelName = "style_" + artist.toString().toLowerCase() + ".zip";
        List<String> url = LocationUtils.getUrl(modelName, "https://aias-home.oss-cn-beijing.aliyuncs.com/models/gan_models/" + modelName);

        this.criteria =
                Criteria.builder()
                        .setTypes(Image.class, Image.class)
                        .optEngine("PyTorch") // Use PyTorch engine
                        .optModelUrls(Joiner.on(',').join(url))
                        .optProgress(new ProgressBar())
                        .optTranslatorFactory(new StyleTransferTranslatorFactory())
                        .build();


    }

    @Override
    public void close() throws Exception {
    }

    public enum Artist {
        /**
         * 塞尚(Paul Cezanne, 1838～1906)
         */
        CEZANNE,
        /**
         * 莫奈 (Claude monet, 1840～1926)
         */
        MONET,
        /**
         * 日本浮世绘
         */
        UKIYOE,
        /**
         * 梵高 (Vincent Willem van Gogh, 1853~1890)
         */
        VANGOGH
    }
}
