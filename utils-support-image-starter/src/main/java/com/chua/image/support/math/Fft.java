/*
Copyright 2006 Jerry Huxtable

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.chua.image.support.math;

import static com.chua.common.support.constant.NumberConstant.TWE;

/**
 * FFT
 *
 * @author CH
 */
public class Fft {

    protected float[] w1;
    protected float[] w2;
    protected float[] w3;

    public Fft(int logn) {
        w1 = new float[logn];
        w2 = new float[logn];
        w3 = new float[logn];
        int n = 1;
        for (int k = 0; k < logn; k++) {
            n <<= 1;
            double angle = -2.0 * Math.PI / n;
            w1[k] = (float) Math.sin(0.5 * angle);
            w2[k] = -2.0f * w1[k] * w1[k];
            w3[k] = (float) Math.sin(angle);
        }
    }

    public void transform1D(float[] real, float[] imag, int logN, int n, boolean forward) {
        scramble(n, real, imag);
        butterflies(n, logN, forward ? 1 : -1, real, imag);
    }

    public void transform2D(float[] real, float[] imag, int cols, int rows, boolean forward) {
        int log2cols = log2(cols);
        int log2rows = log2(rows);
        int n = Math.max(rows, cols);
        float[] rtemp = new float[n];
        float[] itemp = new float[n];

        // FFT the rows
        for (int y = 0; y < rows; y++) {
            int offset = y * cols;
            System.arraycopy(real, offset, rtemp, 0, cols);
            System.arraycopy(imag, offset, itemp, 0, cols);
            transform1D(rtemp, itemp, log2cols, cols, forward);
            System.arraycopy(rtemp, 0, real, offset, cols);
            System.arraycopy(itemp, 0, imag, offset, cols);
        }

        // FFT the columns
        for (int x = 0; x < cols; x++) {
            int index = x;
            for (int y = 0; y < rows; y++) {
                rtemp[y] = real[index];
                itemp[y] = imag[index];
                index += cols;
            }
            transform1D(rtemp, itemp, log2rows, rows, forward);
            index = x;
            for (int y = 0; y < rows; y++) {
                real[index] = rtemp[y];
                imag[index] = itemp[y];
                index += cols;
            }
        }
    }

    private void scramble(int n, float[] real, float[] imag) {
        int j = 0;

        for (int i = 0; i < n; i++) {
            if (i > j) {
                float t;
                t = real[j];
                real[j] = real[i];
                real[i] = t;
                t = imag[j];
                imag[j] = imag[i];
                imag[i] = t;
            }
            int m = n >> 1;
            while (j >= m && m >= TWE) {
                j -= m;
                m >>= 1;
            }
            j += m;
        }
    }

    private void butterflies(int n, int logN, int direction, float[] real, float[] imag) {
        int i1 = 1;

        for (int k = 0; k < logN; k++) {
            float wRe, wIm, wpRe, wpIm, tempRe, tempIm, wt;
            int halfN = i1;
            i1 <<= 1;
            wt = direction * w1[k];
            wpRe = w2[k];
            wpIm = direction * w3[k];
            wRe = 1.0f;
            wIm = 0.0f;
            for (int offset = 0; offset < halfN; offset++) {
                for (int i = offset; i < i1; i += i1) {
                    int j = i + halfN;
                    float re = real[j];
                    float im = imag[j];
                    tempRe = (wRe * re) - (wIm * im);
                    tempIm = (wIm * re) + (wRe * im);
                    real[j] = real[i] - tempRe;
                    real[i] += tempRe;
                    imag[j] = imag[i] - tempIm;
                    imag[i] += tempIm;
                }
                wt = wRe;
                wRe = wt * wpRe - wIm * wpIm + wRe;
                wIm = wIm * wpRe + wt * wpIm + wIm;
            }
        }
        if (direction == -1) {
            float nr = 1.0f / i1;
            for (int i = 0; i < i1; i++) {
                real[i] *= nr;
                imag[i] *= nr;
            }
        }
    }

    private int log2(int n) {
        int m = 1;
        int log2n = 0;

        while (m < n) {
            m *= 2;
            log2n++;
        }
        return m == n ? log2n : -1;
    }

}
