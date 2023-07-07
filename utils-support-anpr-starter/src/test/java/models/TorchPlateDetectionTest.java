package models;

import com.chua.anpr.support.domain.DrawImage;
import com.chua.anpr.support.domain.ImageMat;
import com.chua.anpr.support.domain.PlateInfo;
import com.chua.anpr.support.models.TorchPlateDetection;

import java.awt.*;
import java.util.HashMap;
import java.util.List;

public class TorchPlateDetectionTest {
    public static void main(String[] args) {
        TorchPlateDetection torchPlateDetection = new TorchPlateDetection("open-anpr-core/src/main/resources/models/plate_detect.onnx", 1);

        String imagePath = "open-anpr-core/src/test/resources/images/image001.jpg";
        ImageMat imageMat = ImageMat.fromImage(imagePath);
        List<PlateInfo> plateInfos = torchPlateDetection.inference(imageMat, 0.3f,0.5f, new HashMap<>());
        System.out.println(plateInfos);

        DrawImage drawImage = DrawImage.build(imagePath);
        for(PlateInfo plateInfo : plateInfos){
            PlateInfo.Point [] points = plateInfo.box.toArray();
            for(int i =0; i< points.length; i++){
                if(i+1 == points.length){
                    drawImage.drawLine(
                            new DrawImage.Point((int)points[i].x, (int)points[i].y),
                            new DrawImage.Point((int)points[0].x, (int)points[0].y),
                            2, Color.RED
                    );
                }else{
                    drawImage.drawLine(
                            new DrawImage.Point((int)points[i].x, (int)points[i].y),
                            new DrawImage.Point((int)points[i+1].x, (int)points[i+1].y),
                            2, Color.RED
                    );
                }
            }
        }
        ImageMat.fromCVMat(drawImage.toMat()).imShow();
    }

}
