package com.chua.example.image;

import com.chua.common.support.file.*;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author CH
 */
public class ImageExample {


    public static void main(String[] args) throws IOException {
        ResourceFile resourceFile = ResourceFileBuilder.builder().open("D:\\home\\微信图片_20221203112301.jpg");
        if (resourceFile.isImageFile()) {

            try (FileOutputStream fileOutputStream = new FileOutputStream("E:\\data\\temp.jpg")) {

                ImageFile imageFile = (ImageFile) resourceFile;
                ImageEditFile imageEditFile = imageFile.toEditFile();
                ExifFile exifFile = imageFile.toExifFile();
                exifFile.removeExif(fileOutputStream);
            }
            System.out.println();
        }

    }
}
