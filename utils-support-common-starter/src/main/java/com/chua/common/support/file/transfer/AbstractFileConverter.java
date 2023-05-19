package com.chua.common.support.file.transfer;

import com.chua.common.support.lang.profile.DelegateProfile;
import com.chua.common.support.utils.FileUtils;

import java.io.*;

/**
 * 图片转化
 *
 * @author CH
 */
public abstract class AbstractFileConverter extends
        DelegateProfile implements FileConverter {

    protected static String DEFAULT_PIC = "jpg,jpeg,jpe,tiff,tif,bmp,png,ico";
    protected static String DEFAULT_SIMPLE_PIC = "JPG,jpg,bmp,BMP,WBMP,png,PNG,raw,RAW,JPEG,pnm,PNM,wbmp,jpeg";
    protected static String DEFAULT_VIDEO = "avi,wmv,mpg,mpeg,vob,dat,3gp,mp4,mkv,rm,rmvb,mov,flv,mpe,asf,ram,swf";


    @Override
    public void convert(File file, String suffix, OutputStream outputStream) throws Exception {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            convert(FileUtils.getExtension(file), inputStream, suffix, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
