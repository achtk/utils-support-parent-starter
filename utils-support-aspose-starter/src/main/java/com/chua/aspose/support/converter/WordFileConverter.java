package com.chua.aspose.support.converter;

import com.aspose.words.Document;
import com.aspose.words.SaveOptions;
import com.chua.common.support.file.transfer.AbstractFileConverter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Date;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_DOT;

/**
 * word pdf
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class WordFileConverter extends AbstractFileConverter {

    static {
        registerWord_v_22_5();
    }


    /**
     * aspose-words:jdk17:22.5 版本
     */
    @SneakyThrows
    private static void registerWord_v_22_5() {
        Class<?> zzjXClass = Class.forName("com.aspose.words.zzjX");
        Constructor<?> constructor = zzjXClass.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        Object zzjXInstance = constructor.newInstance();

        // zzZ7O
        Field zzZ7O = zzjXClass.getDeclaredField("zzZ7O");
        zzZ7O.setAccessible(true);
        zzZ7O.set(zzjXInstance, new Date(Long.MAX_VALUE));

        // zzBf
        Field zzZfB = zzjXClass.getDeclaredField("zzZfB");
        zzZfB.setAccessible(true);
        Class<?> zzYP3Class = Class.forName("com.aspose.words.zzYP3");
        Field zzBfField = zzYP3Class.getDeclaredField("zzBf");
        zzBfField.setAccessible(true);
        zzZfB.set(zzjXInstance, zzBfField.get(null));

        // zzZjA
        Field zzZjA = zzjXClass.getDeclaredField("zzZjA");
        zzZjA.setAccessible(true);
        zzZjA.set(null, zzjXInstance);


        Class<?> zzCnClass = Class.forName("com.aspose.words.zzCn");
        Field zzZyx = zzCnClass.getDeclaredField("zzZyx");
        zzZyx.setAccessible(true);
        zzZyx.set(null, 128);
        Field zzZ8w = zzCnClass.getDeclaredField("zzZ8w");
        zzZ8w.setAccessible(true);
        zzZ8w.set(null, false);
    }

    @Override
    public void convert(InputStream inputStream, String suffix, OutputStream outputStream) throws Exception {
        Document document = new Document(inputStream);
        document.save(outputStream, SaveOptions.createSaveOptions(SYMBOL_DOT + suffix));
    }

    @Override
    public void convert(InputStream inputStream, File output) throws Exception {
        Document document = new Document(inputStream);
        try (FileOutputStream fileOutputStream = new FileOutputStream(output)) {
            document.save(fileOutputStream, SaveOptions.createSaveOptions(output.getName()));
        }
    }

    @Override
    public String target() {
        return "doc,docx,rtf,dot,dotx,docm,odt,ott,html,mhtml,pdf,txt,md,tiff,jpeg,jpg,png,bmp,svg,emf,gif,pcl,epub,xps";
    }

    @Override
    public String source() {
        return "doc,docx,rtf,dot,dotx,docm,odt,ott,html,mhtml,pdf,txt,md";
    }
}
