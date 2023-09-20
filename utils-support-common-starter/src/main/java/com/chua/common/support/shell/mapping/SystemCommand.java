package com.chua.common.support.shell.mapping;

import com.chua.common.support.ansi.AnsiColor;
import com.chua.common.support.ansi.AnsiOutput;
import com.chua.common.support.collection.ImmutableBuilder;
import com.chua.common.support.constant.Projects;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.function.Splitter;
import com.chua.common.support.json.Json;
import com.chua.common.support.lang.date.DateTime;
import com.chua.common.support.matcher.AntPathMatcher;
import com.chua.common.support.matcher.PathMatcher;
import com.chua.common.support.shell.*;
import com.chua.common.support.utils.CmdUtils;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.NumberUtils;
import com.chua.common.support.utils.StringUtils;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_ASTERISK;
import static com.chua.common.support.constant.CommonConstant.SYMBOL_COMMA;

/**
 * 系统命令
 *
 * @author CH
 */
public class SystemCommand {
    private static final String OS_NAME = System.getProperty("os.name").toUpperCase();
    RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

    private static final CharSequence WIN = "WIN";

    private static final String IP_CONFIG = "ipconfig";

    public static final DecimalFormat D = new DecimalFormat("0.00");
    private static final String IF_CONFIG = "ifconfig";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    /**
     * help
     *
     * @return help
     */
    @ShellMapping(value = {"jvm"}, describe = "JVM")
    public ShellResult jvm() {
        Properties props = System.getProperties();
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        ShellTable table = new ShellTable(
                "最大内存",
                "已使用内存",
                "剩余内存",
                "Java版本",
                "启动时间",
                "进程ID"
        );
        List lists = Collections.singletonList(ImmutableBuilder.builder().add(
                        StringUtils.getNetFileSizeDescription(maxMemory, D),
                        StringUtils.getNetFileSizeDescription(totalMemory, D),
                        StringUtils.getNetFileSizeDescription(freeMemory, D),
                        props.getProperty("java.version"),
                        DateTime.of(runtimeMXBean.getStartTime()).toStandard(),
                        Projects.getPid()
                ).newArrayList()
        );
        table.addRows(lists);
        try {
            return ShellResult.table(Json.toJson(table));
        } catch (Exception e) {
            return ShellResult.error();
        }
    }

    /**
     * ip
     *
     * @return help
     */
    @ShellMapping(value = {"ip"}, describe = "显示当前系统IP")
    public ShellResult ip() {
        if (OS_NAME.contains(WIN)) {
            return ShellResult.builder().mode(ShellMode.CODE).result(CmdUtils.exec(IP_CONFIG)).build();
        }
        return ShellResult.builder().mode(ShellMode.CODE).result(CmdUtils.exec(IF_CONFIG)).build();
    }

    /**
     * netstat
     *
     * @return port
     */
    @ShellMapping(value = {"netstat"}, describe = "显示当前系统端口")
    public ShellResult netstat() {
        return ShellResult.builder().mode(ShellMode.CODE).result(CmdUtils.exec("netstat -ano")).build();
    }

    /**
     * ls
     */
    @ShellMapping(value = {"date"}, describe = "服务器时间")
    public ShellResult date() {
        return ShellResult.text(LocalDateTime.now().format(FORMATTER));
    }

    /**
     * ls
     */
    @ShellMapping(value = {"ls"}, describe = "显示当前目录文件")
    public ShellResult ls() {
        File file = new File(".");
        File[] files = file.listFiles();
        ShellTable shellTable = new ShellTable("文件名", "文件类型", "大小");
        for (File file1 : files) {
            shellTable.addRow(file1.isDirectory() ?
                            AnsiOutput.toString(AnsiColor.BRIGHT_GREEN, file1.getName()) :
                            AnsiOutput.toString(AnsiColor.BRIGHT_BLUE, file1.getName()),
                    file1.isDirectory() ? "文件夹" : "文件", StringUtils.getNetFileSizeDescription(file1.length(), D));
        }

        return ShellResult.table(shellTable.toString());
    }

    /**
     * grep
     */
    @ShellMapping(value = {"grep"}, describe = "过滤信息")
    public ShellResult grep(@ShellPipe ShellResult data, @ShellParam(value = "name", numberOfArgs = 1) String name) {
        if (StringUtils.isNullOrEmpty(name)) {
            return data;
        }

        ShellMode mode = data.getMode();
        boolean hasHeader = false;
        List<String> line = new LinkedList<>();
        List<String> split = Splitter.on("\n").omitEmptyStrings().trimResults().splitToList(data.getResult());
        if (mode == ShellMode.TABLE) {
            ShellTable shellTable = Json.fromJson(data.getResult(), ShellTable.class);
            ShellTable rs = new ShellTable(shellTable.getHead());
            List<Collection<String>> rows = shellTable.getRows();
            for (Collection<String> row : rows) {
                boolean isMatch = false;
                lo:
                for (String s : row) {
                    if (isMatch(s, name)) {
                        isMatch = true;
                        break lo;
                    }
                }
                if (isMatch) {
                    rs.addRow(row);
                }
            }
            return ShellResult.table(rs.toString());
        }

        for (String s : split) {
            if (isMatch(s, name)) {
                line.add(ansi(s, name));
            }
        }
        if(mode == ShellMode.CODE) {
            return ShellResult.builder().mode(ShellMode.CODE).result(Joiner.on("\r\n").join(line)).build();
        }
        return ShellResult.ansi(Joiner.on("\r\n").join(line));

    }
    /**
     * grep
     */
    @ShellMapping(value = {"less"}, describe = "过滤信息")
    public ShellResult less(@ShellPipe ShellResult data, @ShellParam(value = "num", numberOfArgs = 1) String name) {
        if (StringUtils.isNullOrEmpty(name)) {
            return data;
        }

        int start = 0;
        int end = 0;
        if(name.contains(SYMBOL_COMMA)) {
            String[] split = name.split(SYMBOL_COMMA);
            start = NumberUtils.toInt(split[0], 0);
            end = NumberUtils.toInt(split[1], 10);
        } else {
            end = NumberUtils.toInt(name, 10);
        }

        ShellMode mode = data.getMode();
        if(mode == ShellMode.TABLE) {
            ShellTable shellTable = Json.fromJson(data.getResult(), ShellTable.class);
            List<Collection<String>> rows = shellTable.getRows();
            shellTable.setRows(CollectionUtils.sub(rows, start, end));
            return ShellResult.table(shellTable.toString());
        }
        String[] split = data.getResult().split("\r\n");
        return ShellResult.builder().mode(data.getMode()).result(CollectionUtils.sub(Arrays.asList(split), start, end).toString()).build();

    }

    private String ansi(String name, String s) {
        if (name.contains(SYMBOL_ASTERISK)) {
            return name;
        }
        return name.replace(s, AnsiOutput.toString(AnsiColor.RED, s));
    }

    final static PathMatcher MATCHER = new AntPathMatcher();

    private boolean isMatch(String s, String name) {
        if (name.contains(SYMBOL_ASTERISK)) {
            return MATCHER.match(SYMBOL_ASTERISK + name + SYMBOL_ASTERISK, s);
        }
        return s.contains(name);
    }
}
