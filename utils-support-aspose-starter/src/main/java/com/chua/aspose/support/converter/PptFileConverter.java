package com.chua.aspose.support.converter;

import com.aspose.slides.Presentation;
import com.aspose.words.SaveOptions;
import com.chua.common.support.file.transfer.AbstractFileConverter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Date;

/**
 * word pdf
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class PptFileConverter extends AbstractFileConverter {

    static {
        registerPPT_v_21_10();
    }


    /**
     * aspose-slides:21.10 版本有效
     */
    @SneakyThrows
    private static void registerPPT_v_21_10() {
        Date licenseExpiry = new Date(Long.MAX_VALUE);

        Class<?> publicClass = Class.forName("com.aspose.slides.internal.of.public");
        Object publicInstance = publicClass.newInstance();

        Field publicTry = publicClass.getDeclaredField("try");
        publicTry.setAccessible(true);
        publicTry.set(null, publicInstance);

        Field publicInt = publicClass.getDeclaredField("int");
        publicInt.setAccessible(true);
        publicInt.set(publicInstance, licenseExpiry);

        Field publicNew = publicClass.getDeclaredField("new");
        publicNew.setAccessible(true);
        publicNew.set(publicInstance, licenseExpiry);

        Field publicIf = publicClass.getDeclaredField("if");
        publicIf.setAccessible(true);
        publicIf.set(publicInstance, 2);

        Class<?> nativeClass = Class.forName("com.aspose.slides.internal.of.native");
        Field nativeDo = nativeClass.getDeclaredField("do");
        nativeDo.setAccessible(true);
        nativeDo.set(null, publicInstance);
    }

    @SneakyThrows
    @Override
    public void convert(InputStream inputStream, String suffix, OutputStream outputStream) {
        Presentation presentation = new Presentation(inputStream);
        presentation.save(outputStream, SaveOptions.createSaveOptions("." + suffix).getSaveFormat());
    }

    @SneakyThrows
    @Override
    public void convert(InputStream inputStream, File output) {
        Presentation presentation = new Presentation(inputStream);
        try (FileOutputStream fileOutputStream = new FileOutputStream(output)) {
            presentation.save(fileOutputStream, SaveOptions.createSaveOptions(output.getName()).getSaveFormat());
        }
    }

    @Override
    public String target() {
        return "ppt,pptx,pps,pot,ppsx,pptm,ppsm,potx,potm,odp,otp,xps,jpeg,jpg,png,bmp,tiff,gif,svg,html,swf";
    }

    @Override
    public String source() {
        return "ppt,pptx,pps,pot,ppsx,pptm,ppsm,potx,potm,odp,otp";
    }
}
