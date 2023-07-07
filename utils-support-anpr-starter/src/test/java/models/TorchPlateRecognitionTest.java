package models;

import com.chua.anpr.support.domain.ImageMat;
import com.chua.anpr.support.domain.PlateInfo;
import com.chua.anpr.support.models.TorchPlateDetection;
import com.chua.anpr.support.models.TorchPlateRecognition;
import com.chua.anpr.support.utils.CropUtil;
import org.opencv.core.Mat;

import java.util.HashMap;
import java.util.List;

public class TorchPlateRecognitionTest {

    public static void main(String[] args) {
        TorchPlateDetection torchPlateDetection = new TorchPlateDetection("open-anpr-core/src/main/resources/models/plate_detect.onnx", 1);
        TorchPlateRecognition torchPlateRecognition = new TorchPlateRecognition("open-anpr-core/src/main/resources/models/plate_rec_color.onnx", 1);

        String imagePath = "open-anpr-core/src/test/resources/images";
        ImageMat imageMat = ImageMat.fromImage(imagePath);
        List<PlateInfo> plateInfos = torchPlateDetection.inference(imageMat, 0.3f,0.5f, new HashMap<>());
        System.out.println(plateInfos);
        for(PlateInfo plateInfo : plateInfos){
            Mat crop = CropUtil.crop(imageMat.toCvMat(), plateInfo.box);
//            ImageMat.fromCVMat(crop).imShow();
            PlateInfo.ParseInfo parseInfo = torchPlateRecognition.inference(ImageMat.fromCVMat(crop), plateInfo.single, new HashMap<>());
            System.out.println(parseInfo);
        }
    }
}
