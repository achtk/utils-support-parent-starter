package com.chua.pytorch.support.utils;

import ai.djl.modality.cv.output.Point;
import ai.djl.ndarray.NDArray;
import org.bytedeco.javacpp.indexer.DoubleRawIndexer;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point2f;

import java.util.List;

/**
 * @author CH
 */
public class NDArrayUtils {

    // NDArray 转 opencv_core.Mat
    public static Mat toOpenCVMat(NDArray points, int rows, int cols) {
        double[] doubleArray = points.toDoubleArray();
        // CV_32F = FloatRawIndexer
        // CV_64F = DoubleRawIndexer
        Mat mat = new Mat(rows, cols, opencv_core.CV_64F);

        DoubleRawIndexer ldIdx = mat.createIndexer();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                ldIdx.put(i, j, doubleArray[i * cols + j]);
            }
        }
        ldIdx.release();

        return mat;
    }

    // NDArray 转 opencv_core.Point2f
    public static Point2f toOpenCVPoint2f(NDArray points, int rows) {
        double[] doubleArray = points.toDoubleArray();
        Point2f points2f = new Point2f(rows);

        for (int i = 0; i < rows; i++) {
            points2f.position(i).x((float) doubleArray[i * 2]).y((float) doubleArray[i * 2 + 1]);
        }

        return points2f;
    }

    // Double array 转 opencv_core.Point2f
    public static Point2f toOpenCVPoint2f(double[] doubleArray, int rows) {
        Point2f points2f = new Point2f(rows);

        for (int i = 0; i < rows; i++) {
            points2f.position(i).x((float) doubleArray[i * 2]).y((float) doubleArray[i * 2 + 1]);
        }

        return points2f;
    }

    // list 转 opencv_core.Point2f
    public static Point2f toOpenCVPoint2f(List<Point> points, int rows) {
        Point2f points2f = new Point2f(points.size());

        for (int i = 0; i < rows; i++) {
            Point point = points.get(i);
            points2f.position(i).x((float) point.getX()).y((float) point.getY());
        }

        return points2f;
    }
}
