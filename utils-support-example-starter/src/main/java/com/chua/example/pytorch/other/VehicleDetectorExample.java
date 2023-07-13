package com.chua.example.pytorch.other;

import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.pytorch.support.vehicle.detect.VehicleDetector;

import java.util.List;

/**
 * @author CH
 */
public class VehicleDetectorExample {

    public static void main(String[] args) {
        VehicleDetector vehicleDetector = new VehicleDetector(DetectionConfiguration.builder()
                .cachePath("E:\\workspace\\environment").build());
        List<PredictResult> detect = vehicleDetector.detect("E:\\images\\image003.jpg");
        System.out.println();
    }
}
