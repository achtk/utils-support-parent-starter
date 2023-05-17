package com.chua.common.support.utils;

import com.chua.common.support.constant.CommonConstant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 文件类型
 *
 * @author CH
 */
public class FileTypeUtils {

    private static final Map<String, String> FILE_TYPE_MAP;
    public static final String DOCX = "docx";
    public static final String XLSX = "xlsx";
    public static final String PPTX = "pptx";
    public static final String JAR = CommonConstant.JAR;
    public static final String WAR = CommonConstant.WAR;
    public static final String OFD = "ofd";
    public static final String DOC = "doc";
    public static final String MSI = "msi";
    public static final String ZIP = CommonConstant.ZIP;
    public static final String XLS = "xls";

    static {
        FILE_TYPE_MAP = new ConcurrentSkipListMap<>((s1, s2) -> {
            int len1 = s1.length();
            int len2 = s2.length();
            if (len1 == len2) {
                return s1.compareTo(s2);
            } else {
                return len2 - len1;
            }
        });

        FILE_TYPE_MAP.put("ffd8ff", "jpg");
        FILE_TYPE_MAP.put("89504e47", "png");
        FILE_TYPE_MAP.put("4749463837", "gif");
        FILE_TYPE_MAP.put("4749463839", "gif");
        FILE_TYPE_MAP.put("49492a00227105008037", "tif");
        FILE_TYPE_MAP.put("424d228c010000000000", "bmp");
        FILE_TYPE_MAP.put("424d8240090000000000", "bmp");
        FILE_TYPE_MAP.put("424d8e1b030000000000", "bmp");
        FILE_TYPE_MAP.put("41433130313500000000", "dwg");
        FILE_TYPE_MAP.put("7b5c727466315c616e73", "rtf");
        FILE_TYPE_MAP.put("38425053000100000000", "psd");
        FILE_TYPE_MAP.put("46726f6d3a203d3f6762", "eml");
        FILE_TYPE_MAP.put("5374616E64617264204A", "mdb");
        FILE_TYPE_MAP.put("252150532D41646F6265", "ps");
        FILE_TYPE_MAP.put("255044462d312e", "pdf");
        FILE_TYPE_MAP.put("2e524d46000000120001", "rmvb");
        FILE_TYPE_MAP.put("464c5601050000000900", "flv");
        FILE_TYPE_MAP.put("0000001C66747970", "mp4");
        FILE_TYPE_MAP.put("00000020667479706", "mp4");
        FILE_TYPE_MAP.put("00000018667479706D70", "mp4");
        FILE_TYPE_MAP.put("49443303000000002176", "mp3");
        FILE_TYPE_MAP.put("000001ba210001000180", "mpg");
        FILE_TYPE_MAP.put("3026b2758e66cf11a6d9", "wmv");
        FILE_TYPE_MAP.put("52494646e27807005741", "wav");
        FILE_TYPE_MAP.put("52494646d07d60074156", "avi");
        FILE_TYPE_MAP.put("4d546864000000060001", "mid");
        FILE_TYPE_MAP.put("526172211a0700cf9073", "rar");
        FILE_TYPE_MAP.put("235468697320636f6e66", "ini");
        FILE_TYPE_MAP.put("504B03040a0000000000", "jar");
        FILE_TYPE_MAP.put("504B0304140008000800", "jar");

        FILE_TYPE_MAP.put("d0cf11e0a1b11ae10", "xls");
        FILE_TYPE_MAP.put("504B0304", "zip");
        FILE_TYPE_MAP.put("4d5a9000030000000400", "exe");
        FILE_TYPE_MAP.put("3c25402070616765206c", "jsp");
        FILE_TYPE_MAP.put("4d616e69666573742d56", "mf");
        FILE_TYPE_MAP.put("7061636b616765207765", "java");
        FILE_TYPE_MAP.put("406563686f206f66660d", "bat");
        FILE_TYPE_MAP.put("1f8b0800000000000000", "gz");
        FILE_TYPE_MAP.put("cafebabe0000002e0041", "class");
        FILE_TYPE_MAP.put("49545346030000006000", "chm");
        FILE_TYPE_MAP.put("04000000010000001300", "mxp");
        FILE_TYPE_MAP.put("6431303a637265617465", "torrent");
        FILE_TYPE_MAP.put("6D6F6F76", "mov");
        FILE_TYPE_MAP.put("FF575043", "wpd");
        FILE_TYPE_MAP.put("CFAD12FEC5FD746F", "dbx");
        FILE_TYPE_MAP.put("2142444E", "pst");
        FILE_TYPE_MAP.put("AC9EBD8F", "qdf");
        FILE_TYPE_MAP.put("E3828596", "pwl");
        FILE_TYPE_MAP.put("2E7261FD", "ram");
    }

    /**
     * 增加文件类型映射<br>
     * 如果已经存在将覆盖之前的映射
     *
     * @param fileStreamHexHead 文件流头部Hex信息
     * @param extName           文件扩展名
     * @return 之前已经存在的文件扩展名
     */
    public static String putFileType(String fileStreamHexHead, String extName) {
        return FILE_TYPE_MAP.put(fileStreamHexHead, extName);
    }

    /**
     * 移除文件类型映射
     *
     * @param fileStreamHexHead 文件流头部Hex信息
     * @return 移除的文件扩展名
     */
    public static String removeFileType(String fileStreamHexHead) {
        return FILE_TYPE_MAP.remove(fileStreamHexHead);
    }

    /**
     * 根据文件流的头部信息获得文件类型
     *
     * @param fileStreamHexHead 文件流头部16进制字符串
     * @return 文件类型，未找到为{@code null}
     */
    public static String getType(String fileStreamHexHead) {
        for (Map.Entry<String, String> fileTypeEntry : FILE_TYPE_MAP.entrySet()) {
            if (StringUtils.startWithIgnoreCase(fileStreamHexHead, fileTypeEntry.getKey())) {
                return fileTypeEntry.getValue();
            }
        }
        return null;
    }

    /**
     * 根据文件流的头部信息获得文件类型
     *
     * @param in {@link InputStream}
     * @return 类型，文件的扩展名，未找到为{@code null}
     * @throws IOException 读取流引起的异常
     */
    public static String getType(InputStream in) throws IOException {
        return getType(IoUtils.readHex28Upper(in));
    }


    /**
     * 根据文件流的头部信息获得文件类型
     *
     * <pre>
     *     1、无法识别类型默认按照扩展名识别
     *     2、xls、doc、msi头信息无法区分，按照扩展名区分
     *     3、zip可能为docx、xlsx、pptx、jar、war、ofd头信息无法区分，按照扩展名区分
     * </pre>
     *
     * @param in       {@link InputStream}
     * @param filename 文件名
     * @return 类型，文件的扩展名，未找到为{@code null}
     * @throws IOException 读取流引起的异常
     */
    public static String getType(InputStream in, String filename) throws IOException {
        String typeName = getType(in);

        if (null == typeName) {

            typeName = FileUtils.getExtension(filename);
        } else if (XLS.equals(typeName)) {

            final String extName = FileUtils.getExtension(filename);
            if (DOC.equalsIgnoreCase(extName)) {
                typeName = DOC;
            } else if (MSI.equalsIgnoreCase(extName)) {
                typeName = MSI;
            }
        } else if (ZIP.equals(typeName)) {

            final String extName = FileUtils.getExtension(filename);
            if (DOCX.equalsIgnoreCase(extName)) {
                typeName = DOCX;
            } else if (XLSX.equalsIgnoreCase(extName)) {
                typeName = XLSX;
            } else if (PPTX.equalsIgnoreCase(extName)) {
                typeName = PPTX;
            } else if (JAR.equalsIgnoreCase(extName)) {
                typeName = JAR;
            } else if (WAR.equalsIgnoreCase(extName)) {
                typeName = WAR;
            } else if (OFD.equalsIgnoreCase(extName)) {
                typeName = OFD;
            }
        } else if (JAR.equals(typeName)) {

            final String extName = FileUtils.getExtension(filename);
            if (XLSX.equalsIgnoreCase(extName)) {
                typeName = XLSX;
            } else if (DOCX.equalsIgnoreCase(extName)) {
                typeName = DOCX;
            }
        }
        return typeName;
    }

    /**
     * 根据文件流的头部信息获得文件类型
     *
     * <pre>
     *     1、无法识别类型默认按照扩展名识别
     *     2、xls、doc、msi头信息无法区分，按照扩展名区分
     *     3、zip可能为docx、xlsx、pptx、jar、war头信息无法区分，按照扩展名区分
     * </pre>
     *
     * @param file 文件 {@link File}
     * @return 类型，文件的扩展名，未找到为{@code null}
     * @throws IOException 读取文件引起的异常
     */
    public static String getType(File file) throws IOException {
        FileInputStream in = null;
        try {
            in = IoUtils.toStream(file);
            return getType(in, file.getName());
        } finally {
            IoUtils.closeQuietly(in);
        }
    }

    /**
     * 通过路径获得文件类型
     *
     * @param path 路径，绝对路径或相对ClassPath的路径
     * @return 类型
     * @throws IOException 读取文件引起的异常
     */
    public static String getTypeByPath(String path) throws IOException {
        return getType(FileUtils.file(path));
    }
}
