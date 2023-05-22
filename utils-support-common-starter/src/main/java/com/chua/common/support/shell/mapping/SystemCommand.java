package com.chua.common.support.shell.mapping;

import com.chua.common.support.ansi.AnsiColor;
import com.chua.common.support.ansi.AnsiOutput;
import com.chua.common.support.collection.ImmutableBuilder;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.function.Splitter;
import com.chua.common.support.json.Json;
import com.chua.common.support.matcher.AntPathMatcher;
import com.chua.common.support.matcher.PathMatcher;
import com.chua.common.support.shell.ShellMapping;
import com.chua.common.support.shell.ShellParam;
import com.chua.common.support.shell.ShellPipe;
import com.chua.common.support.shell.adaptor.CommandAdaptor;
import com.chua.common.support.shell.adaptor.JsonCommandAdaptor;
import com.chua.common.support.shell.adaptor.TxtCommandAdaptor;
import com.chua.common.support.shell.adaptor.XmlCommandAdaptor;
import com.chua.common.support.utils.CmdUtils;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.view.view.TableView;

import java.io.File;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统命令
 *
 * @author CH
 */
public class SystemCommand {
    private static final String OS_NAME = System.getProperty("os.name").toUpperCase();

    private static final Map<String, CommandAdaptor> FILE_ADAPTOR = new ConcurrentHashMap<>();
    private static final CharSequence WIN = "WIN";

    static {
        FILE_ADAPTOR.put("xml", new XmlCommandAdaptor());
        FILE_ADAPTOR.put("txt", new TxtCommandAdaptor());
        FILE_ADAPTOR.put("log", new TxtCommandAdaptor());
        FILE_ADAPTOR.put("json", new JsonCommandAdaptor());
    }

    private static final String IP_CONFIG = "ipconfig";

    public static final DecimalFormat D = new DecimalFormat("0.00");
    private static final String IF_CONFIG = "ifconfig";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    /**
     * help
     *
     * @return help
     */
    @ShellMapping(value = {"mem"}, describe = "java内存情况")
    public String spring() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        Map<String, Object> rs = new LinkedHashMap<>();
        rs.put("head", ImmutableBuilder.builder().add("最大内存", "已使用内存", "剩余内存").newArrayList());
        rs.put("rows", Collections.singletonList(ImmutableBuilder.builder().add(
                StringUtils.getNetFileSizeDescription(maxMemory, D),
                StringUtils.getNetFileSizeDescription(totalMemory, D),
                StringUtils.getNetFileSizeDescription(freeMemory, D)).newArrayList()
        ));
        try {
            return "@table " + Json.toJson(rs);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * ip
     *
     * @return help
     */
    @ShellMapping(value = {"cat"}, describe = "显示文本文件")
    public String cat(
            @ShellParam(value = "file", example = {"cat --file xx.txt: 反编译文件"}, numberOfArgs = 2) String file
    ) {
        File file1 = new File(file);
        if (!file1.exists()) {
            return "文件不存在";
        }

        String extension = FileUtils.getExtension(file);
        CommandAdaptor commandAdaptor = FILE_ADAPTOR.get(extension);
        if (null == commandAdaptor) {
            commandAdaptor = new TxtCommandAdaptor();
        }

        return commandAdaptor.handler(file1.getAbsolutePath());
    }

    /**
     * ip
     *
     * @return help
     */
    @ShellMapping(value = {"ip"}, describe = "显示当前系统IP")
    public String ip() {
        if (OS_NAME.contains(WIN)) {
            return CmdUtils.exec(IP_CONFIG);
        }
        return CmdUtils.exec(IF_CONFIG);
    }

    /**
     * netstat
     *
     * @return port
     */
    @ShellMapping(value = {"netstat"}, describe = "显示当前系统端口")
    public String netstat() {
        return CmdUtils.exec("netstat -ano");
    }

    /**
     * ls
     */
    @ShellMapping(value = {"date"}, describe = "服务器时间")
    public String date() {
        return LocalDateTime.now().format(FORMATTER);
    }

    /**
     * ls
     */
    @ShellMapping(value = {"ls"}, describe = "显示当前目录文件")
    public String ls() {
        File file = new File(".");
        File[] files = file.listFiles();
        TableView tableView = new TableView(new TableView.ColumnDefine[]{
                new TableView.ColumnDefine(100, false, TableView.Align.LEFT),
                new TableView.ColumnDefine(30, false, TableView.Align.LEFT),
                new TableView.ColumnDefine(30, false, TableView.Align.LEFT),
        });
        tableView.addRow("文件名", "文件类型", "大小");
        for (File file1 : files) {
            tableView.addRow(file1.isDirectory() ?
                            AnsiOutput.toString(AnsiColor.BRIGHT_GREEN, file1.getName()) :
                            AnsiOutput.toString(AnsiColor.BRIGHT_BLUE, file1.getName()),
                    file1.isDirectory() ? "文件夹" : "文件", StringUtils.getNetFileSizeDescription(file1.length(), D));
        }

        return tableView.draw();
    }

    /**
     * grep
     */
    @ShellMapping(value = {"grep"}, describe = "过滤信息")
    public String grep(@ShellPipe String data, @ShellParam(value = "name", numberOfArgs = 1) String name) {
        if (StringUtils.isNullOrEmpty(name)) {
            return data;
        }

        List<String> split = Splitter.on("\n").omitEmptyStrings().trimResults().splitToList(data);
        if (split.size() == 1) {
            return data;
        }

        List<String> line = new LinkedList<>();
        int index = 0;
        for (int i = 0; i < split.size(); i++) {
            String s = split.get(i);
            if (s.contains(" ") || s.startsWith("+----")) {
                line.add(s);
                break;
            }

            if (split.size() > (i + 1)) {
                String s1 = split.get(i + 1);
                if (s1.contains(" ")) {
                    index = i + 1;
                    line.add(s1);
                    break;
                }
            }
            break;
        }

        boolean hasHeader = !line.isEmpty();
        for (int i = index + 1; i < split.size(); i++) {
            String s = split.get(i);
            if (isMatch(s, name)) {
                line.add(ansi(s, name));
            }
        }

        if (hasHeader && line.size() == 1) {
            return "";
        }
        return Joiner.on("\r\n").join(line);
    }

    private String ansi(String name, String s) {
        if (name.contains("*")) {
            return name;
        }
        return name.replace(s, AnsiOutput.toString(AnsiColor.RED, s));
    }

    final static PathMatcher MATCHER = new AntPathMatcher();

    private boolean isMatch(String s, String name) {
        if (name.contains("*")) {
            return MATCHER.match("*" + name + "*", s);
        }
        return s.contains(name);
    }
}
