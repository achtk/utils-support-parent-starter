package com.chua.example.pytorch.other;

import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.resource.repository.Repository;
import com.chua.pytorch.support.fire.FireSmokeDetector;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author CH
 */
public class FireSmokeExample {
    public static void main(String[] args) throws Exception {
        FireSmokeDetector fireSmokeDetector = new FireSmokeDetector(DetectionConfiguration.DEFAULT);
        fireSmokeDetector.detect(Repository.classpath().first("fire_smoke.png").toUri(), Files.newOutputStream(Paths.get("D:/1/fire_smoke_out.png")));
    }
}
