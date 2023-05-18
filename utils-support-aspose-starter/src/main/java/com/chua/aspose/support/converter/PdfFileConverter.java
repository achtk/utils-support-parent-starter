package com.chua.aspose.support.converter;

import com.aspose.pdf.Document;
import com.aspose.words.SaveFormat;
import com.chua.common.support.file.transfer.AbstractFileConverter;
import com.chua.common.support.utils.FileUtils;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Date;

/**
 * word pdf
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class PdfFileConverter extends AbstractFileConverter {

    static {
        registerPdf_v_21_7();
    }


    /**
     * aspose-pdf:21.7 版本
     */
    @SneakyThrows
    private static void registerPdf_v_21_7() {
        Date licenseExpiry = new Date(Long.MAX_VALUE);
        Class<?> l9yClass = Class.forName("com.aspose.pdf.l9y");
        Constructor<?> constructor = l9yClass.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        Object l9yInstance = constructor.newInstance();

        // lc
        Field lc = l9yClass.getDeclaredField("lc");
        lc.setAccessible(true);
        lc.set(l9yInstance, licenseExpiry);
        // ly
        Field ly = l9yClass.getDeclaredField("ly");
        ly.setAccessible(true);
        ly.set(l9yInstance, licenseExpiry);

        // l0if
        Field l0if = l9yClass.getDeclaredField("l0if");
        l0if.setAccessible(true);

        Class<?> l9nClass = Class.forName("com.aspose.pdf.l9n");
        Field lfField = l9nClass.getDeclaredField("lf");
        lfField.setAccessible(true);
        Object lf = lfField.get(null); // 处理枚举
        l0if.set(l9yInstance, lf);

        Class<?> l9yLfClass = Class.forName("com.aspose.pdf.l9y$lf");
        Field l9y$lf = l9yLfClass.getDeclaredField("lI");
        l9y$lf.setAccessible(true);
        l9y$lf.set(null, l9yInstance);


        Class<?> l19jClass = Class.forName("com.aspose.pdf.l19j");
        Field l19jlI = l19jClass.getDeclaredField("lI");
        l19jlI.setAccessible(true);
        l19jlI.set(null, 128);
        Field l19jLf = l19jClass.getDeclaredField("lf");
        l19jLf.setAccessible(true);
        l19jLf.set(null, false);
    }

    @SneakyThrows
    @Override
    public void convert(InputStream inputStream, String suffix, OutputStream outputStream) {
        Document document = new Document(inputStream);
        document.save(outputStream, SaveFormat.fromName(suffix.toUpperCase()));
    }

    @SneakyThrows
    @Override
    public void convert(InputStream inputStream, File output) {
        Document document = new Document(inputStream);
        try (FileOutputStream fileOutputStream = new FileOutputStream(output)) {
            document.save(fileOutputStream, SaveFormat.fromName(FileUtils.getExtension(output).toUpperCase()));
        }
    }

    @Override
    public String target() {
        return "pdf,xps,epub,html,tex,xml,svg,doc,docx,xls,xlsx,pptx,jpeg,jpg,png,bmp,tiff,emf,xml,text";
    }

    @Override
    public String source() {
        return "pdf,xps,epub,html,tex,xml,svg";
    }
}
