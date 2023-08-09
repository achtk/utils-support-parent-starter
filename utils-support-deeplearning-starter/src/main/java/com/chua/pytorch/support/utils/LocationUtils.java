package com.chua.pytorch.support.utils;

import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.Point;
import ai.djl.modality.cv.output.Rectangle;
import ai.djl.modality.cv.output.*;
import ai.djl.modality.cv.util.NDImageUtils;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import ai.djl.repository.MRL;
import ai.djl.repository.Repository;
import ai.djl.repository.zoo.BaseModelLoader;
import ai.djl.repository.zoo.ModelLoader;
import ai.djl.util.RandomUtils;
import com.chua.common.support.constant.PredictRectangle;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.reflection.FieldStation;
import com.chua.common.support.resource.ResourceProvider;
import com.chua.common.support.resource.resource.Resource;
import com.chua.common.support.utils.*;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件资源
 *
 * @author CH
 * @since 2022-04-02
 */
@Slf4j
public class LocationUtils {
    /**
     * 保存BufferedImage图片
     *
     * @author Calvin
     */
    public static void saveImage(BufferedImage img, String name, String path) {
        Image djlImg = ImageFactory.getInstance().fromImage(img); // 支持多种图片格式，自动适配
        Path outputDir = Paths.get(path);
        Path imagePath = outputDir.resolve(name);
        // OpenJDK 不能保存 jpg 图片的 alpha channel
        try {
            djlImg.save(Files.newOutputStream(imagePath), "png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存DJL图片
     *
     * @author Calvin
     */
    public static void saveImage(Image img, String path) {
        Path outputDir = Paths.get(path);
        try {
            FileUtils.forceMkdirParent(outputDir.toFile());
        } catch (IOException ignored) {
        }
        // OpenJDK 不能保存 jpg 图片的 alpha channel
        try (OutputStream outputStream = Files.newOutputStream(outputDir)) {
            img.save(outputStream, "png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存DJL图片
     *
     * @author Calvin
     */
    public static void saveImage(Image img, String name, String path) {
        Path outputDir = Paths.get(path);
        Path imagePath = outputDir.resolve(name);
        try {
            FileUtils.forceMkdirParent(imagePath.toFile());
        } catch (IOException ignored) {
        }
        // OpenJDK 不能保存 jpg 图片的 alpha channel
        try (OutputStream outputStream = Files.newOutputStream(imagePath)) {
            img.save(outputStream, "png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 保存图片,含检测框
     *
     * @author Calvin
     */
    public static void saveBoundingBoxImage(
            Image img, DetectedObjects detection, String name, String path) throws IOException {
        // Make image copy with alpha channel because original image was jpg
        img.drawBoundingBoxes(detection);
        Path outputDir = Paths.get(path);
        Files.createDirectories(outputDir);
        Path imagePath = outputDir.resolve(name);
        // OpenJDK can't save jpg with alpha channel
        try (OutputStream outputStream = Files.newOutputStream(imagePath)) {
            img.save(outputStream, "png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存图片,含检测框
     *
     * @author Calvin
     */
    public static void saveBoundingBoxImage(
            Image img, DetectedObjects detection, OutputStream outputStream) throws IOException {
        // Make image copy with alpha channel because original image was jpg
        img.drawBoundingBoxes(detection);
        // OpenJDK can't save jpg with alpha channel
        try {
            img.save(outputStream, "png");
        } finally {
            IoUtils.closeQuietly(outputStream);
        }
    }

    /**
     * 绘制人脸关键点
     *
     * @author Calvin
     */
    public static void drawLandmark(Image img, BoundingBox box, float[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            int x = getX(img, box, array[2 * i]);
            int y = getY(img, box, array[2 * i + 1]);
            Color c = new Color(0, 255, 0);
            drawImageRect((BufferedImage) img.getWrappedImage(), x, y, 1, 1, c);
        }
    }

    /**
     * 画检测框
     *
     * @author Calvin
     */
    public static void drawImageRect(BufferedImage image, int x, int y, int width, int height) {
        // 将绘制图像转换为Graphics2D
        Graphics2D g = (Graphics2D) image.getGraphics();
        try {
            g.setColor(new Color(246, 96, 0));
            // 声明画笔属性 ：粗 细（单位像素）末端无修饰 折线处呈尖角
            BasicStroke bStroke = new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
            g.setStroke(bStroke);
            g.drawRect(x, y, width, height);

        } finally {
            g.dispose();
        }
    }

    /**
     * 画检测框
     *
     * @author Calvin
     */
    public static void drawImageRect(
            BufferedImage image, int x, int y, int width, int height, Color c) {
        // 将绘制图像转换为Graphics2D
        Graphics2D g = (Graphics2D) image.getGraphics();
        try {
            g.setColor(c);
            // 声明画笔属性 ：粗 细（单位像素）末端无修饰 折线处呈尖角
            BasicStroke bStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
            g.setStroke(bStroke);
            g.drawRect(x, y, width, height);

        } finally {
            g.dispose();
        }
    }

    /**
     * 显示文字
     *
     * @author Calvin
     */
    public static void drawImageText(BufferedImage image, String text) {
        Graphics graphics = image.getGraphics();
        int fontSize = 100;
        Font font = new Font("楷体", Font.PLAIN, fontSize);
        try {
            graphics.setFont(font);
            graphics.setColor(new Color(246, 96, 0));
            int strWidth = graphics.getFontMetrics().stringWidth(text);
            graphics.drawString(text, fontSize - (strWidth / 2), fontSize + 30);
        } finally {
            graphics.dispose();
        }
    }

    /**
     * 返回外扩人脸 factor = 1, 100%, factor = 0.2, 20%
     *
     * @author Calvin
     */
    public static Image getSubImage(Image img, BoundingBox box, float factor) {
        Rectangle rect = box.getBounds();
        // 左上角坐标
        int x1 = (int) (rect.getX() * img.getWidth());
        int y1 = (int) (rect.getY() * img.getHeight());
        // 宽度，高度
        int w = (int) (rect.getWidth() * img.getWidth());
        int h = (int) (rect.getHeight() * img.getHeight());
        // 左上角坐标
        int x2 = x1 + w;
        int y2 = y1 + h;

        // 外扩大100%，防止对齐后人脸出现黑边
        int newX1 = Math.max((int) (x1 + x1 * factor / 2 - x2 * factor / 2), 0);
        int newX2 = Math.min((int) (x2 + x2 * factor / 2 - x1 * factor / 2), img.getWidth() - 1);
        int newY1 = Math.max((int) (y1 + y1 * factor / 2 - y2 * factor / 2), 0);
        int newY2 = Math.min((int) (y2 + y2 * factor / 2 - y1 * factor / 2), img.getHeight() - 1);
        int newW = newX2 - newX1;
        int newH = newY2 - newY1;

        return img.getSubImage(newX1, newY1, newW, newH);
    }

    private static int getX(Image img, BoundingBox box, float x) {
        Rectangle rect = box.getBounds();
        // 左上角坐标
        int x1 = (int) (rect.getX() * img.getWidth());
        // 宽度
        int w = (int) (rect.getWidth() * img.getWidth());

        return (int) (x * w + x1);
    }

    private static int getY(Image img, BoundingBox box, float y) {
        Rectangle rect = box.getBounds();
        // 左上角坐标
        int y1 = (int) (rect.getY() * img.getHeight());
        // 高度
        int h = (int) (rect.getHeight() * img.getHeight());

        return (int) (y * h + y1);
    }

    public static Image getImage(Object fileName) {
        if (fileName instanceof Image) {
            return (Image) fileName;
        }

        try {
            if (fileName instanceof String) {

                if (fileName.toString().startsWith("static")) {
                    return ImageFactory.getInstance().fromUrl(fileName.toString());
                }

                File file2 = Converter.convertIfNecessary(fileName.toString(), File.class);
                if (null != file2) {
                    return ImageFactory.getInstance().fromFile(file2.toPath());
                }

                Resource resource = ResourceProvider.of("classpath*:**/" + fileName).getResource();
                if (null != resource) {
                    return ImageFactory.getInstance().fromInputStream(resource.openStream());
                }

            } else if (fileName instanceof BufferedImage) {
                return ImageFactory.getInstance().fromImage(fileName);
            } else if (fileName instanceof URL) {
                return ImageFactory.getInstance().fromUrl(((URL) fileName));
            } else if (fileName instanceof URI) {
                return ImageFactory.getInstance().fromUrl(((URI) fileName).toURL());
            } else if (fileName instanceof File) {
                return ImageFactory.getInstance().fromImage(Thumbnails
                        .of((File) fileName)
                        .scale(1d).asBufferedImage()
                );
            } else if (fileName instanceof Path) {
                return ImageFactory.getInstance().fromFile(((Path) fileName));
            } else if (fileName instanceof InputStream) {
                return ImageFactory.getInstance().fromInputStream(((InputStream) fileName));
            } else {
                return ImageFactory.getInstance().fromFile(Paths.get(fileName.toString()));
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 图片等比缩放
     *
     * @param image        图片输入缓存流
     * @param outputWidth  图片压缩到的宽
     * @param outputHeight 图片压缩到的高
     * @return BufferedImage
     */
    private static BufferedImage compressPicMin(BufferedImage image,
                                         int outputWidth, int outputHeight) {
        // TODO Auto-generated method stub
        if (image == null) {
            return null;
        }

        //如果图片本身的宽和高均小于要压缩到的宽和高，则不压缩直接返回
        if (outputWidth > image.getWidth(null) && outputHeight > image.getHeight(null)) {
            return image;
        }

        int newWidth;
        int newHeight;
        //宽和高等比缩放的率
        double rate1 = (double) image.getWidth(null) / (double) outputWidth;
        double rate2 = (double) image.getHeight(null) / (double) outputHeight;
        //控制缩放大小
        double rate = rate1 < rate2 ? rate1 : rate2;
        newWidth = (int) (image.getWidth(null) / rate);
        newHeight = (int) (image.getHeight(null) / rate);

        BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        newImage.createGraphics().drawImage(image.getScaledInstance(newWidth, outputHeight, java.awt.Image.SCALE_SMOOTH), 0, 0, null);

        return newImage;
    }

    public static String parseLocation(String locations, String defaultValue) {
        if (!Strings.isNullOrEmpty(locations)) {
            File tp = null;
            try {
                tp = new File(StringUtils.count(locations, ":") > 1 ? URI.create(locations).toURL().getFile() : locations);
            } catch (MalformedURLException ignored) {
            }
            if (null == tp) {
                return defaultValue;
            }

            if (tp.exists()) {
                File[] files = tp.listFiles();
                if (null == files || files.length == 0) {
                    return defaultValue;
                } else {
                    return locations;
                }
            } else {
                return defaultValue;
            }
        }

        String[] urls = locations.split("\\s*,\\s*");
        List<ModelLoader> list = new ArrayList<>(urls.length);
        for (String url : urls) {
            if (!url.isEmpty()) {
                Repository repo = Repository.newInstance(url, url);
                List<MRL> mrls = repo.getResources();
                for (MRL mrl : mrls) {
                    list.add(new BaseModelLoader(mrl));
                }
            }
        }
        return list.isEmpty() ? defaultValue : locations;
    }

    public static PredictRectangle getPredictRectangle(Image img, BoundingBox boundingBox) {
        PredictRectangle rectangle = new PredictRectangle();
        Rectangle bounds = boundingBox.getBounds();
        Point point = boundingBox.getPoint();
        rectangle.setImageHeight(img.getHeight());
        rectangle.setImageWidth(img.getWidth());
        rectangle.setY(point.getY() * rectangle.getImageHeight());
        rectangle.setX(point.getX() * rectangle.getImageWidth());
        rectangle.setWidth(bounds.getWidth() * rectangle.getImageWidth());
        rectangle.setHeight(bounds.getHeight() * rectangle.getImageHeight());

        return rectangle;
    }

    public static List<String> getUrl(String url) {
        List<String> url1 = getUrl(url, "", false);
        if ("".equals(url1.get(0))) {
            return Collections.emptyList();
        }

        return url1;
    }

    public static List<String> getUrl(String url, String defaultAddress) {
        return getUrl(url, defaultAddress, false);
    }

    public static List<String> getUrl(String url, String defaultAddress, boolean isDirector) {
        List<String> urls = new LinkedList<>();
        String[] split = url.split(",");
//            doAnalysisClasspath(urls, split);

        for (String s : split) {
            if (FileUtils.exist(s)) {
                urls.add(FileUtils.toUri(s));
            }
        }

        doAnalysisCache(urls, split, isDirector);
        if (urls.isEmpty()) {
            FileUtils.doAnalysisUserHome(urls, split, isDirector);
        }

        if (urls.isEmpty()) {
            doAnalysisDjl(urls, split, isDirector);
        }

        if (urls.isEmpty()) {
            log.info("【未检测到】本地缓存模型");
            throw new IllegalArgumentException("【未检测到】本地缓存模型");
        }
        log.info("【检测到】本地缓存模型");
        return urls;
    }

    private static void doAnalysisCache(List<String> urls, String[] url, boolean isDirector) {
        String djlCacheDir = System.getProperty("DJL_CACHE_DIR");
        if (Strings.isNullOrEmpty(djlCacheDir)) {
            return;
        }
        log.info("检查缓存目录 : {}", new File(djlCacheDir).getAbsolutePath());
        try {
            Files.walkFileTree(Paths.get(djlCacheDir), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (ArrayUtils.contains(url, file.toFile().getName().replace("\\", "/"))) {
                        urls.add(file.toUri().toString());
                    }
                    return super.visitFile(file, attrs);
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    String path = dir.toFile().getPath();
                    if (path.contains(".svn") || path.contains(".idea") || path.contains(".log")) {
                        return FileVisitResult.CONTINUE;
                    }

                    if (ArrayUtils.contains(url, dir.toFile().getName().replace("\\", "/"))) {
                        urls.add(dir.toUri().toString());
                    }
                    return super.preVisitDirectory(dir, attrs);
                }
            });
        } catch (IOException ignored) {
        }

    }

    public static List<String> doAnalysis(String property, String[] url, boolean isDirector) {
        List<String> urls = new LinkedList<>();
        log.info("检测当前目录是否存在模型 : {}", new File(property).getAbsolutePath());
        try {
            Files.walkFileTree(Paths.get(property), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (isDirector) {
                        return FileVisitResult.CONTINUE;
                    }

                    if (ArrayUtils.contains(url, file.toFile().getName().replace("\\", "/"))) {
                        urls.add(file.toUri().toString());
                    }
                    return super.visitFile(file, attrs);
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (!isDirector) {
                        return FileVisitResult.CONTINUE;
                    }

                    if (ArrayUtils.contains(url, dir.toFile().getName().replace("\\", "/"))) {
                        urls.add(dir.toUri().toString());
                    }
                    return super.preVisitDirectory(dir, attrs);
                }
            });
        } catch (IOException ignored) {
        }
        return urls;
    }

    private static void doAnalysisCurrent(List<String> urls, String[] url, boolean isDirector) {
        String property = ".";
        log.info("检测当前目录是否存在模型 : {}", new File(property).getAbsolutePath());
        try {
            Files.walkFileTree(Paths.get(property), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (isDirector) {
                        return FileVisitResult.CONTINUE;
                    }

                    if (ArrayUtils.contains(url, file.toFile().getName().replace("\\", "/"))) {
                        urls.add(file.toUri().toString());
                    }
                    return super.visitFile(file, attrs);
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (!isDirector) {
                        return FileVisitResult.CONTINUE;
                    }

                    if (ArrayUtils.contains(url, dir.toFile().getName().replace("\\", "/"))) {
                        urls.add(dir.toUri().toString());
                    }
                    return super.preVisitDirectory(dir, attrs);
                }
            });
        } catch (IOException ignored) {
        }
    }

    private static void doAnalysisDjl(List<String> urls, String[] url, boolean isDirector) {
        String property = System.getProperty("user.home") + "/.djl.ai/";
        log.info("检测当前目录是否存在模型 : {}", new File(property).getAbsolutePath());

        try {
            Files.walkFileTree(Paths.get(property), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (isDirector) {
                        return FileVisitResult.CONTINUE;
                    }

                    if (ArrayUtils.contains(url, file.toFile().getName().replace("\\", "/"))) {
                        urls.add(file.toUri().toString());
                    }
                    return super.visitFile(file, attrs);
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (!isDirector) {
                        return FileVisitResult.CONTINUE;
                    }

                    if (ArrayUtils.contains(url, dir.toFile().getName().replace("\\", "/"))) {
                        urls.add(dir.toUri().toString());
                    }
                    return super.preVisitDirectory(dir, attrs);
                }
            });
        } catch (IOException ignored) {
        }
    }



    private static void doAnalysisClasspath(List<String> urls, String[] url) {
        log.info("检测类加载器下是否存在模型 : classpath*:**/{}", url);
        for (String s : url) {
            ResourceProvider resourceProvider = ResourceProvider.of("classpath*:**/" + s);
            Set<Resource> resources = resourceProvider.getResources();
            for (Resource resource : resources) {
                URL url1 = resource.getUrl();
                if (null == url1) {
                    continue;
                }
                try {
                    urls.add(url1.toURI().toString());
                } catch (URISyntaxException ignored) {
                }
            }
        }
    }

    public static Image rotateImg(Image image) {
        try (NDManager manager = NDManager.newBaseManager()) {
            NDArray rotated = NDImageUtils.rotate90(image.toNDArray(manager), 1);
            return ImageFactory.getInstance().fromNDArray(rotated);
        }
    }

    public static Image getSubImage(Object img, Object box) {
        return getSubImage(getImage(img), extendRect(box));
    }

    public static Image getSubImage(Image img, Object box) {
        return getSubImage(img, extendRect(box));
    }

    public static Image getSubImage(Object img, BoundingBox box) {
        return getSubImage(getImage(img), extendRect(box));
    }

    public static Image getSubImage(Image img, BoundingBox box) {
        Rectangle rect = box.getBounds();
        double[] extended = extendRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
        int width = img.getWidth();
        int height = img.getHeight();
        int[] recovered = {
                (int) (extended[0] * width),
                (int) (extended[1] * height),
                (int) (extended[2] * width),
                (int) (extended[3] * height)
        };
        return img.getSubImage(recovered[0], recovered[1], recovered[2], recovered[3]);
    }

    public static BoundingBox extendRect(Object boundingBox) {
        if (boundingBox instanceof BoundingBox) {
            return (BoundingBox) boundingBox;
        }
        if (boundingBox instanceof com.chua.common.support.constant.BoundingBox) {
            com.chua.common.support.pojo.Shape point = ((com.chua.common.support.constant.BoundingBox) boundingBox).getCorners().get(0);
            return new Rectangle(point.getX(), point.getY(),
                    ((com.chua.common.support.constant.BoundingBox) boundingBox).getWidth(),
                    ((com.chua.common.support.constant.BoundingBox) boundingBox).getHeight());
        }

        return null;
    }

    public static double[] extendRect(double xmin, double ymin, double width, double height) {
        double centerx = xmin + width / 2;
        double centery = ymin + height / 2;
        if (width > height) {
            width += height * 2.0;
            height *= 3.0;
        } else {
            height += width * 2.0;
            width *= 3.0;
        }
        double newX = centerx - width / 2 < 0 ? 0 : centerx - width / 2;
        double newY = centery - height / 2 < 0 ? 0 : centery - height / 2;
        double newWidth = newX + width > 1 ? 1 - newX : width;
        double newHeight = newY + height > 1 ? 1 - newY : height;
        return new double[]{newX, newY, newWidth, newHeight};
    }


    public static PredictResult convertPredictResult(Classifications.Classification item, Image img) {
        PredictResult predictResult = new PredictResult();

        predictResult.setScore((float) item.getProbability());
        predictResult.setClsScore((float) item.getProbability());
        predictResult.setText(item.getClassName());
        predictResult.setClsLabel(item.getClassName());
        return predictResult;
    }

    public static PredictResult convertPredictResult(DetectedObjects.DetectedObject item, Image img) {
        PredictResult predictResult = new PredictResult();

        predictResult.setScore((float) item.getProbability());
        predictResult.setClsScore((float) item.getProbability());
        BoundingBox boundingBox = item.getBoundingBox();
        List<com.chua.common.support.pojo.Shape> rs = new LinkedList<>();
        boundingBox.getPath().forEach(it -> {
            rs.add(new com.chua.common.support.pojo.Shape(it.getX(), it.getY()));
        });
        predictResult.setBoundingBox(com.chua.common.support.constant.BoundingBox
                .builder()
                .corners(rs)
                .width(boundingBox.getBounds().getWidth())
                .height(boundingBox.getBounds().getHeight())
                .build());
        predictResult.setText(item.getClassName());
        predictResult.setClsLabel(item.getClassName());
        return predictResult;
    }

    private static PredictRectangle convertPredictRectangle(BoundingBox boundingBox) {
        PredictRectangle rectangle = new PredictRectangle();
        Rectangle boundingBoxBounds = boundingBox.getBounds();
        Point point = boundingBox.getPoint();
        rectangle.setHeight(boundingBoxBounds.getHeight());
        rectangle.setWidth(boundingBoxBounds.getWidth());
        rectangle.setX(point.getX());
        rectangle.setY(point.getY());

        return rectangle;
    }

    public static BoundingBox getBoundingBox(PredictResult predictResult) {
        Object boundingBox = predictResult.getBoundingBox();
        if (boundingBox instanceof BoundingBox) {
            return (BoundingBox) boundingBox;
        }

        if (boundingBox instanceof com.chua.common.support.constant.BoundingBox) {
            com.chua.common.support.constant.BoundingBox b = (com.chua.common.support.constant.BoundingBox) boundingBox;
            List<com.chua.common.support.pojo.Shape> corners = b.getCorners();
            com.chua.common.support.pojo.Shape shape = corners.get(0);
            return new Landmark(shape.getX(), shape.getY(), b.getWidth(), b.getHeight(), corners.stream()
                    .map(it -> new Point(it.getX(), it.getY())).collect(Collectors.toList())
            );
        }


        PredictRectangle predictRectangle = (PredictRectangle) predictResult.getBoundingBox();
        return new Rectangle(predictRectangle.getX(), predictRectangle.getY(), predictRectangle.getWidth(), predictRectangle.getHeight());
    }

    public static BoundingBox getBoundingBox(Image img) {
        return new Rectangle(0, 0, img.getWidth(), img.getHeight());
    }

    public static BufferedImage saveBoundingBoxImage(List<PredictResult> predictResults, BufferedImage src) {
        Image image = getImage(src);
        Image duplicate = image.duplicate();
        List<String> names = new ArrayList<>();
        List<Double> prob = new ArrayList<>();
        List<BoundingBox> rect = new ArrayList<>();

        for (PredictResult predictResult : predictResults) {
            names.add(predictResult.getText());
            prob.add((double) predictResult.getScore());
            rect.add(extendRect((BoundingBox) predictResult.getBoundingBox()));
        }
        DetectedObjects detectedObjects = new DetectedObjects(names, prob, rect);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            saveBoundingBoxImage(duplicate, detectedObjects, outputStream);
            return BufferedImageUtils.getBufferedImage(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveBoundingBoxImage(List<PredictResult> predictResults, String source, String target) {
        Image image = getImage(source);
        Image duplicate = image.duplicate();
        List<String> names = new ArrayList<>();
        List<Double> prob = new ArrayList<>();
        List<BoundingBox> rect = new ArrayList<>();

        for (PredictResult predictResult : predictResults) {
            names.add(predictResult.getText());
            prob.add((double) predictResult.getScore());
            rect.add(extendRect(predictResult.getBoundingBox()));
        }

        try {
            FileUtils.forceMkdirParent(new File(target));
        } catch (IOException ignored) {
        }
        DetectedObjects detectedObjects = new DetectedObjects(names, prob, rect);
        try (OutputStream outputStream = Files.newOutputStream(Paths.get(target))) {
            saveBoundingBoxImage(duplicate, detectedObjects, outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 画检测框(有倾斜角)
     *
     * @author Calvin
     */
    public static void drawImageRect(BufferedImage image, NDArray box) {
        float[] points = box.toFloatArray();
        int[] xPoints = new int[5];
        int[] yPoints = new int[5];

        for (int i = 0; i < 4; i++) {
            xPoints[i] = (int) points[2 * i];
            yPoints[i] = (int) points[2 * i + 1];
        }
        xPoints[4] = xPoints[0];
        yPoints[4] = yPoints[0];

        // 将绘制图像转换为Graphics2D
        Graphics2D g = (Graphics2D) image.getGraphics();
        try {
            g.setColor(new Color(0, 255, 0));
            // 声明画笔属性 ：粗 细（单位像素）末端无修饰 折线处呈尖角
            BasicStroke bStroke = new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
            g.setStroke(bStroke);
            g.drawPolyline(xPoints, yPoints, 5); // xPoints, yPoints, nPoints
        } finally {
            g.dispose();
        }
    }

    /**
     * 画检测框(有倾斜角)和文本
     *
     * @author Calvin
     */
    public static void drawImageRectWithText(BufferedImage image, NDArray box, String text) {
        float[] points = box.toFloatArray();
        int[] xPoints = new int[5];
        int[] yPoints = new int[5];

        for (int i = 0; i < 4; i++) {
            xPoints[i] = (int) points[2 * i];
            yPoints[i] = (int) points[2 * i + 1];
        }
        xPoints[4] = xPoints[0];
        yPoints[4] = yPoints[0];

        // 将绘制图像转换为Graphics2D
        Graphics2D g = (Graphics2D) image.getGraphics();
        try {
            int fontSize = 32;
            Font font = new Font("楷体", Font.PLAIN, fontSize);
            g.setFont(font);
            g.setColor(new Color(0, 0, 255));
            // 声明画笔属性 ：粗 细（单位像素）末端无修饰 折线处呈尖角
            BasicStroke bStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
            g.setStroke(bStroke);
            g.drawPolyline(xPoints, yPoints, 5); // xPoints, yPoints, nPoints
            g.drawString(text, xPoints[0], yPoints[0]);
        } finally {
            g.dispose();
        }
    }

    public static Object toBoundingBox(BoundingBox boundingBox) {
        List<com.chua.common.support.pojo.Shape> points = new LinkedList<>();
        if (boundingBox instanceof Landmark) {
            List<Point> corners = (List<Point>) FieldStation.of(boundingBox).getValue("corners");
            for (Point corner : corners) {
                points.add(new com.chua.common.support.pojo.Shape(corner.getX(), corner.getY()));
            }
        } else {
            Spliterator<Point> spliterator = boundingBox.getBounds().getPath().spliterator();
            spliterator.forEachRemaining(it -> {
                points.add(new com.chua.common.support.pojo.Shape(it.getX(), it.getY()));
            });
        }

        return com.chua.common.support.constant.BoundingBox.builder()
                .height(boundingBox.getBounds().getHeight())
                .width(boundingBox.getBounds().getWidth())
                .corners(points).build();
    }


    private static class BufferedImageWrapper implements Image {

        private BufferedImage image;

        BufferedImageWrapper(BufferedImage image) {
            this.image = image;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getWidth() {
            return image.getWidth();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getHeight() {
            return image.getHeight();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object getWrappedImage() {
            return image;
        }

        @Override
        public Image resize(int width, int height, boolean copy) {
            return null;
        }

        @Override
        public Image getMask(int[][] mask) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Image getSubImage(int x, int y, int w, int h) {
            return new BufferedImageWrapper(image.getSubimage(x, y, w, h));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Image duplicate() {
            BufferedImage copy =
                    new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
            byte[] sourceData = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            byte[] biData = ((DataBufferByte) copy.getRaster().getDataBuffer()).getData();
            System.arraycopy(sourceData, 0, biData, 0, sourceData.length);
            return new BufferedImageWrapper(copy);
        }

        private void convertIdNeeded() {
            if (image.getType() == BufferedImage.TYPE_INT_ARGB) {
                return;
            }

            BufferedImage newImage =
                    new BufferedImage(
                            image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = newImage.createGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
            image = newImage;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public NDArray toNDArray(NDManager manager, Flag flag) {
            int width = image.getWidth();
            int height = image.getHeight();
            int channel;
            if (flag == Flag.GRAYSCALE) {
                channel = 1;
            } else {
                channel = 3;
            }

            ByteBuffer bb = manager.allocateDirect(channel * height * width);
            if (image.getType() == BufferedImage.TYPE_BYTE_GRAY) {
                int[] data = new int[width * height];
                image.getData().getPixels(0, 0, width, height, data);
                for (int gray : data) {
                    byte b = (byte) gray;
                    bb.put(b);
                    if (flag != Flag.GRAYSCALE) {
                        bb.put(b);
                        bb.put(b);
                    }
                }
            } else {
                // get an array of integer pixels in the default RGB color mode
                int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);
                for (int rgb : pixels) {
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;

                    if (flag == Flag.GRAYSCALE) {
                        int gray = Math.round(0.299f * red + 0.587f * green + 0.114f * blue);
                        bb.put((byte) gray);
                    } else {
                        bb.put((byte) red);
                        bb.put((byte) green);
                        bb.put((byte) blue);
                    }
                }
            }
            bb.rewind();
            return manager.create(bb, new Shape(height, width, channel), DataType.UINT8);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void save(OutputStream os, String type) throws IOException {
            ImageIO.write(image, type, os);
        }

        @Override
        public List<BoundingBox> findBoundingBoxes() {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void drawBoundingBoxes(DetectedObjects detections) {
            // Make image copy with alpha channel because original image was jpg
            convertIdNeeded();

            Graphics2D g = (Graphics2D) image.getGraphics();
            int stroke = 2;
            g.setStroke(new BasicStroke(stroke));
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();

            List<DetectedObjects.DetectedObject> list = detections.items();
            for (DetectedObjects.DetectedObject result : list) {
                String className = result.getClassName();
                BoundingBox box = result.getBoundingBox();
                g.setPaint(randomColor().darker());

                Rectangle rectangle = box.getBounds();
                int x = (int) (rectangle.getX() * imageWidth);
                int y = (int) (rectangle.getY() * imageHeight);
                g.drawRect(
                        x,
                        y,
                        (int) (rectangle.getWidth() * imageWidth),
                        (int) (rectangle.getHeight() * imageHeight));
                drawText(g, className, x, y, stroke, 4);
                // If we have a mask instead of a plain rectangle, draw tha mask
                if (box instanceof Mask) {
                    drawMask((Mask) box);
                } else if (box instanceof Landmark) {
                    drawLandmarks(box);
                }
            }
            g.dispose();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void drawJoints(Joints joints) {
            // Make image copy with alpha channel because original image was jpg
            convertIdNeeded();

            Graphics2D g = (Graphics2D) image.getGraphics();
            int stroke = 2;
            g.setStroke(new BasicStroke(stroke));

            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();

            for (Joints.Joint joint : joints.getJoints()) {
                g.setPaint(randomColor().darker());
                int x = (int) (joint.getX() * imageWidth);
                int y = (int) (joint.getY() * imageHeight);
                g.fillOval(x, y, 10, 10);
            }
            g.dispose();
        }

        @Override
        public void drawImage(Image overlay, boolean resize) {

        }


        private Color randomColor() {
            return new Color(RandomUtils.nextInt(255));
        }

        private void drawText(Graphics2D g, String text, int x, int y, int stroke, int padding) {
            FontMetrics metrics = g.getFontMetrics();
            x += stroke / 2;
            y += stroke / 2;
            int width = metrics.stringWidth(text) + padding * 2 - stroke / 2;
            int height = metrics.getHeight() + metrics.getDescent();
            int ascent = metrics.getAscent();
            java.awt.Rectangle background = new java.awt.Rectangle(x, y, width, height);
            g.fill(background);
            g.setPaint(Color.WHITE);
            g.drawString(text, x + padding, y + ascent);
        }

        private void drawMask(Mask mask) {
            float r = RandomUtils.nextFloat();
            float g = RandomUtils.nextFloat();
            float b = RandomUtils.nextFloat();
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            int x = (int) (mask.getX() * imageWidth);
            int y = (int) (mask.getY() * imageHeight);
            float[][] probDist = mask.getProbDist();
            // Correct some coordinates of box when going out of image
            if (x < 0) {
                x = 0;
            }
            if (y < 0) {
                y = 0;
            }

            BufferedImage maskImage =
                    new BufferedImage(
                            probDist.length, probDist[0].length, BufferedImage.TYPE_INT_ARGB);
            for (int xCor = 0; xCor < probDist.length; xCor++) {
                for (int yCor = 0; yCor < probDist[xCor].length; yCor++) {
                    float opacity = probDist[xCor][yCor] * 0.8f;
                    maskImage.setRGB(xCor, yCor, new Color(r, g, b, opacity).darker().getRGB());
                }
            }
            Graphics2D gR = (Graphics2D) image.getGraphics();
            gR.drawImage(maskImage, x, y, null);
            gR.dispose();
        }

        private void drawLandmarks(BoundingBox box) {
            Graphics2D g = (Graphics2D) image.getGraphics();
            g.setColor(new Color(246, 96, 0));
            BasicStroke bStroke = new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
            g.setStroke(bStroke);
            for (Point point : box.getPath()) {
                g.drawRect((int) point.getX(), (int) point.getY(), 2, 2);
            }
            g.dispose();
        }
    }
}
