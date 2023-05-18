package com.chua.aspose.support.converter;

import com.aspose.cells.FileFormatUtil;
import com.aspose.cells.Workbook;
import com.chua.common.support.file.transfer.AbstractFileConverter;
import com.chua.common.support.utils.FileUtils;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * word pdf
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class ExcelFileConverter extends AbstractFileConverter {

    static {
        registerExcel_v_22_6();
    }


    /**
     * aspose-cells:22.6 版本
     */
    @SneakyThrows
    private static void registerExcel_v_22_6() {
        String licenseExpiry = "20991231";

        // License
        Class<?> licenseClass = Class.forName("com.aspose.cells.License");
        Field a = licenseClass.getDeclaredField("a");
        a.setAccessible(true);
        a.set(null, licenseExpiry);

        // k65
        Class<?> k65Class = Class.forName("com.aspose.cells.k65");
        Field k65A = k65Class.getDeclaredField("a");
        k65A.setAccessible(true);

        Constructor<?> constructor = k65Class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        Object k65Instance = constructor.newInstance();
        k65A.set(null, k65Instance);

        Field k56C = k65Class.getDeclaredField("c");
        k56C.setAccessible(true);
        k56C.set(k65Instance, licenseExpiry);

        // e0n
        Class<?> e0nClass = Class.forName("com.aspose.cells.e0n");
        Field e0nA = e0nClass.getDeclaredField("a");
        e0nA.setAccessible(true);
        e0nA.set(null, false);
    }

    @SneakyThrows
    @Override
    public void convert(InputStream inputStream, String suffix, OutputStream outputStream) {
        Workbook wb = new Workbook(inputStream);
        wb.save(outputStream, FileFormatUtil.extensionToSaveFormat(suffix));
    }

    @SneakyThrows
    @Override
    public void convert(InputStream inputStream, File output) {
        Workbook wb = new Workbook(inputStream);
        try (FileOutputStream fileOutputStream = new FileOutputStream(output)) {
            wb.save(fileOutputStream, FileFormatUtil.extensionToSaveFormat(FileUtils.getExtension(output)));
        }
    }

    @Override
    public String target() {
        return "pdf,xps,dif,xls,xlsx,xlsb,xlt,xltx,xltm,xlsm,xml,ods,csv,tsv,html,mhtml,jpeg,jpg,png,bmp,svg,tiff,emf,gif";
    }

    @Override
    public String source() {
        return "xls,xlsx,xlsb,xlt,xltx,xltm,xlsm,xml,ods,csv,tsv,html,mhtml";
    }
}
