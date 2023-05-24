package com.chua.common.support.printer;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

import static com.chua.common.support.printer.Printer.Type.OUT;

/**
 * 文件打印器
 *
 * @author CH
 */
@Slf4j
public class FilePrinter implements Printer<File> {

    public static final String DEFAULT_DIRECTORY_SYMBOL = "└─";
    public static final String DEFAULT_FILE_SYMBOL = "|   ";
    private final String directorySymbol;
    private final String fileSymbol;

    public FilePrinter() {
        this(DEFAULT_DIRECTORY_SYMBOL, DEFAULT_FILE_SYMBOL);
    }

    public FilePrinter(String directorySymbol, String fileSymbol) {
        this.directorySymbol = directorySymbol;
        this.fileSymbol = fileSymbol;
    }

    @Override
    public String print(File file, Type type) {
        StringBuffer stringBuffer = new StringBuffer("").append(file.getAbsolutePath()).append("\r\n");
        fileTree(stringBuffer, file, 1);

        if (OUT == type) {
            return stringBuffer.toString();
        }

        if (type == Type.SYSTEM) {
            System.out.println(stringBuffer);
            return null;
        }

        stringBuffer.insert(0, "\r\n");
        if (log.isDebugEnabled()) {
            log.debug(stringBuffer.toString());
            return null;
        }

        if (log.isTraceEnabled()) {
            log.trace(stringBuffer.toString());
            return null;
        }

        log.info(stringBuffer.toString());
        return null;
    }

    /**
     * print
     *
     * @param stringBuffer 输出
     * @param f            文件
     * @param level        层级
     */
    private void fileTree(StringBuffer stringBuffer, File f, int level) {
        // 缩进量
        StringBuilder preStr = new StringBuilder();
        for (int i = 0; i < level; i++) {
            if (i == level - 1) {
                preStr.append("\t").append(directorySymbol);
            } else {
                // 级别 - 代表这个目下下地子文件夹
                preStr.append("\t").append(fileSymbol);
            }
        }
        // 返回一个抽象路径名数组，这些路径名表示此抽象路径名所表示目录中地文件
        File[] childs = f.listFiles();
        if (null == childs) {
            // 打印子文件的名字
            stringBuffer.append("\t").append(preStr + f.getName()).append("\r\n");
            return;
        }

        for (int i = 0; i < childs.length; i++) {
            // 打印子文件的名字
            stringBuffer.append(preStr + childs[i].getName()).append("\r\n");
            // 测试此抽象路径名表示地文件能否是一个目录
            if (childs[i].isDirectory()) {
                // 假如子目录下还有子目录，递归子目录调用此方法
                fileTree(stringBuffer, childs[i], level + 1);
            }
        }
    }
}
