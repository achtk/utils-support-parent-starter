package com.chua.common.support.image.filter;

import com.chua.common.support.utils.BufferedImageUtils;
import com.chua.common.support.utils.ThreadUtils;
import lombok.NoArgsConstructor;

import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;

/**
 * Gaussian Blur
 *
 * @author CH
 */
@NoArgsConstructor
public class ImageGaussianBlurFilter extends AbstractImageFilter {

    private float[] kernel = new float[0];
    private double sigma = 2;
    ExecutorService mExecutor;
    CompletionService<Void> service;

    public ImageGaussianBlurFilter(float[] kernel, double sigma) {
        this.kernel = kernel;
        this.sigma = sigma;
    }

    /**
     * <p> here is 1D Gaussian        , </p>
     *
     * @param inPixels  inPixels
     * @param outPixels outPixels
     * @param width     width
     * @param height    height
     */
    private void blur(byte[] inPixels, byte[] outPixels, int width, int height) {
        int subCol = 0;
        int index = 0, index2 = 0;
        float sum = 0;
        int k = kernel.length - 1;
        for (int row = 0; row < height; row++) {
            int c = 0;
            index = row;
            for (int col = 0; col < width; col++) {
                sum = 0;
                for (int m = -k; m < kernel.length; m++) {
                    subCol = col + m;
                    if (subCol < 0 || subCol >= width) {
                        subCol = 0;
                    }
                    index2 = row * width + subCol;
                    c = inPixels[index2] & 0xff;
                    sum += c * kernel[Math.abs(m)];
                }
                outPixels[index] = (byte) BufferedImageUtils.clamp(sum);
                index += height;
            }
        }
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        final int size = width * height;
        int dims = 3;
        makeGaussianKernel(sigma, 0.002, Math.min(width, height));
        mExecutor = ThreadUtils.newFixedThreadExecutor(dims, "task");
        service = new ExecutorCompletionService<>(mExecutor);

        // save result
        for (int i = 0; i < dims; i++) {

            final int temp = i;
            service.submit(new Callable<Void>() {
                public Void call() throws Exception {
                    byte[] inPixels = toColorByte(temp);
                    byte[] temp = new byte[size];
                    blur(inPixels, temp, width, height); // H Gaussian
                    blur(temp, inPixels, height, width); // V Gaussain
                    return null;
                }
            });
        }

        for (int i = 0; i < dims; i++) {
            try {
                service.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        mExecutor.shutdown();
        return toBitmap();
    }


    public void makeGaussianKernel(final double sigma, final double accuracy, int maxRadius) {
        int kRadius = (int) Math.ceil(sigma * Math.sqrt(-2 * Math.log(accuracy))) + 1;
        if (maxRadius < 50) {
            maxRadius = 50;
        }
        if (kRadius > maxRadius) {
            kRadius = maxRadius;
        }
        kernel = new float[kRadius];
        for (int i = 0; i < kRadius; i++) {
            kernel[i] = (float) (Math.exp(-0.5 * i * i / sigma / sigma));
        }
        double sum;
        if (kRadius < maxRadius) {
            sum = kernel[0];
            for (int i = 1; i < kRadius; i++) {
                sum += 2 * kernel[i];
            }
        } else {
            sum = sigma * Math.sqrt(2 * Math.PI);
        }

        for (int i = 0; i < kRadius; i++) {
            double v = (kernel[i] / sum);
            kernel[i] = (float) v;
        }
    }
}
