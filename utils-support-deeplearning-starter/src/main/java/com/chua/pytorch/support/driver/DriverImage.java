package com.chua.pytorch.support.driver;

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import com.chua.common.support.lang.process.ProgressBarBuilder;
import com.chua.common.support.lang.process.ProgressBarStyle;
import com.chua.common.support.utils.StringUtils;
import com.chua.ffmpeg.support.utils.VideoUtils;
import com.chua.pytorch.support.utils.LocationUtils;
import com.google.common.base.Joiner;
import lombok.Data;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 驱动图片
 *
 * @author CH
 */
@Data
@SuppressWarnings("ALL")
public class DriverImage implements AutoCloseable {

    private final ZooModel<Image, Map> detectorModelZoo;
    private final ZooModel<List, Image> generatorModelZoo;
    /**
     * kp模型
     */
    private String kpDetectorModel;
    /**
     * 识别模型
     */
    private String detectorModel;

    public DriverImage() throws Exception {
        this(null, null, "./env");
    }

    public DriverImage(String kpDetectorModel, String detectorModel, String cachePath) throws Exception {
        System.setProperty("DJL_CACHE_DIR", cachePath);
        this.kpDetectorModel = kpDetectorModel;
        this.detectorModel = detectorModel;

        this.detectorModelZoo = ModelZoo.loadModel(detector());
        this.generatorModelZoo = ModelZoo.loadModel(generator());
    }

    /**
     * 驱动图片
     *
     * @param image       待驱动图片
     * @param driverVideo 驱动的视频
     * @param outputFile  输出
     */
    public void driver(File inputStream, File driverVideo, File outputFile) throws Exception {
        // 获取视频关键帧
        List<BufferedImage> driverFrames = VideoUtils.getKeyFrame(driverVideo);
        Image image = LocationUtils.getImage(inputStream);
        List<BufferedImage> imgList = new ArrayList();

        try (Predictor<Image, Map> detector = detectorModelZoo.newPredictor();
             Predictor<List, Image> generator = generatorModelZoo.newPredictor()) {
            Map kpSource = detector.predict(image);
            Map kpDrivingInitial = detector.predict(LocationUtils.getImage(driverFrames.get(0)));

            int total = driverFrames.size();
            // 进度条打印
            try (com.chua.common.support.lang.process.ProgressBar bar =
                         ProgressBarBuilder.newBuilder()
                                 .setTaskName("视频合成")
                                 .setStyle(ProgressBarStyle.COLORFUL_UNICODE_BLOCK_SIMPLE)
                                 .setInitialMax(total)
                                 .build();) {
                for (BufferedImage bufferedImage : driverFrames) {
                    bar.step();
                    List<Object> g = new ArrayList<>();
                    Map kpDriving = detector.predict(LocationUtils.getImage(bufferedImage));
                    g.add(image);
                    g.add(kpDriving);
                    g.add(kpSource);
                    g.add(kpDrivingInitial);
                    imgList.add((BufferedImage) generator.predict(g).getWrappedImage());
                }
            }

            VideoUtils.save(driverVideo, outputFile, imgList, "mp4");
        }
    }


    public Criteria<Image, Map> detector() {
        List<String> model1 = LocationUtils.getUrl(
                StringUtils.defaultString(kpDetectorModel, "kpdetector"),
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/gan_models/kpdetector.zip", true);

        Criteria<Image, Map> kpDetector =
                Criteria.builder()
                        .setTypes(Image.class, Map.class)
                        .optTranslator(new NPtKTranslator())
                        .optEngine("PyTorch")
                        .optProgress(new ProgressBar())
                        .optModelUrls(Joiner.on(',').join(model1))
                        // .optModelPath(Paths.get("/Users/calvin/Documents/build/pytorch_models/AI_MODEL/kpdetector"))
                        .build();
        return kpDetector;
    }

    public Criteria<List, Image> generator() {
        List<String> model1 = LocationUtils.getUrl(
                StringUtils.defaultString(detectorModel, "generator"),
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/gan_models/generator.zip", true);

        Criteria<List, Image> generator =
                Criteria.builder()
                        .setTypes(List.class, Image.class)
                        .optEngine("PyTorch")
                        .optTranslator(new PtGTranslator())
                        .optProgress(new ProgressBar())
                        .optModelUrls(Joiner.on(',').join(model1))
                        // .optModelPath(Paths.get("/Users/calvin/Documents/build/pytorch_models/AI_MODEL/generator"))
                        .build();
        return generator;
    }

    @Override
    public void close() throws Exception {
        detectorModelZoo.close();
        generatorModelZoo.close();
    }
}
