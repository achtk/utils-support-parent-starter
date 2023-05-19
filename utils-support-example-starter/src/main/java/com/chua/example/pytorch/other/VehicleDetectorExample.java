package com.chua.example.pytorch.other;

import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.pytorch.support.vehicle.detect.VehicleDetector;

/**
 * @author CH
 */
public class VehicleDetectorExample {

    public static void main(String[] args) {
        VehicleDetector vehicleDetector = new VehicleDetector(DetectionConfiguration.DEFAULT);
        vehicleDetector.detect("");
    }
}
