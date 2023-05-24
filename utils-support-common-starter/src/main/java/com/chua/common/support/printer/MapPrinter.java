package com.chua.common.support.printer;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static com.chua.common.support.printer.Printer.Type.OUT;


/**
 * 文件打印器
 *
 * @author CH
 */
@Slf4j
public class MapPrinter implements Printer<Map> {

    public static final String DEFAULT_DIRECTORY_SYMBOL = "└─";
    public static final String DEFAULT_FILE_SYMBOL = "|   ";
    private final String directorySymbol;
    private final String fileSymbol;

    public MapPrinter() {
        this(DEFAULT_DIRECTORY_SYMBOL, DEFAULT_FILE_SYMBOL);
    }

    public MapPrinter(String directorySymbol, String fileSymbol) {
        this.directorySymbol = directorySymbol;
        this.fileSymbol = fileSymbol;
    }

    @Override
    public String print(Map map, Type type) {
        StringBuffer stringBuffer = new StringBuffer("\r\n");
        fileTree(stringBuffer, map, 1);

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
     * @param map          文件
     * @param level        层级
     */
    private void fileTree(StringBuffer stringBuffer, Map map, int level) {
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

        map.forEach((k, v) -> {
            stringBuffer.append("\t").append(preStr).append("/").append(k).append("\r\n");
            if (v instanceof Map && !((Map<?, ?>) v).isEmpty()) {
                fileTree(stringBuffer, (Map) v, level + 1);
            }
        });
    }
}
