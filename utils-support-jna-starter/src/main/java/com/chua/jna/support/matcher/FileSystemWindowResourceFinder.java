package com.chua.jna.support.matcher;

import com.chua.common.support.lang.Cost;
import com.chua.common.support.resource.ResourceConfiguration;
import com.chua.common.support.resource.finder.AbstractResourceFinder;
import com.chua.common.support.resource.resource.Resource;
import com.chua.common.support.utils.CmdUtils;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.IoUtils;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.WString;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.Buffer;
import java.nio.CharBuffer;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.chua.common.support.constant.CommonConstant.FILE_SYSTEM_URL_ALL_PREFIX;

/**
 * 系统资源查找器
 *
 * @author CH
 */
@Slf4j
public class FileSystemWindowResourceFinder extends AbstractResourceFinder {
    private static final String EVERYTHING = "Everything.exe";
    private static final String SYSTEM32 = "C:\\Windows\\System32";
    private static final String EVERYTHING_DLL = "win32-x86-64/Everything64.dll";
    private static final int BIG_SIZE = 100 * 1000;

    public FileSystemWindowResourceFinder(ResourceConfiguration configuration) {
        super(configuration);
    }

    static {
        checkIfEverythingStarted();
    }

    /**
     * 检测everything状态
     */
    private static void checkIfEverythingStarted() {
        URL dll = null;
        try {
            dll = IoUtils.newUrl(EVERYTHING_DLL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            FileUtils.copyFile(dll, SYSTEM32);
        } catch (Exception ignored) {
            String[] split = System.getProperty("java.library.path").split(";");
            for (String s : split) {
                try {
                    FileUtils.copyFile(dll, s);
                } catch (IOException ignored1) {
                }
            }
        }
        URL url = null;
        try {
            url = IoUtils.newUrl(EVERYTHING);
        } catch (IOException ignored) {
        }
        String userHome = new File(".").getAbsolutePath();
        try {
            FileUtils.copyFile(url, userHome);
        } catch (IOException e) {
            e.printStackTrace();
        }
        CmdUtils.execProcess(userHome + "/" + EVERYTHING + " -startup");
    }

    @Override
    public synchronized Set<Resource> find(String name) {
        name = FILE_SYSTEM_URL_ALL_PREFIX + name;
        long startTime = System.currentTimeMillis();
        Cost cost1 = Cost.debug("表达式: {}, [{}/{}] 总共被检索, 扫描{}个URL. 平均: {}/s");

        String newPath = FileUtils.normalize(name.substring(FILE_SYSTEM_URL_ALL_PREFIX.length())).replace("/", File.separator);
        Everything.EVERYTHING_64.Everything_SetSearchW(new WString(newPath));
        Everything.EVERYTHING_64.Everything_QueryW(true);
        Buffer p = CharBuffer.allocate(502);
        Set<Resource> result = new LinkedHashSet<>();

        int numResults = Everything.EVERYTHING_64.Everything_GetNumResults();
        for (int i = 0; i < numResults; i++) {
            Everything.EVERYTHING_64.Everything_GetResultFullPathNameW(i, p, 502);
            char[] buf = (char[]) p.array();
            int index = 0;
            for (int j = 0; j < buf.length; j++) {
                char c = buf[j];
                index = j;
                if (c == '\u0000') {
                    break;
                }
            }

            char[] newBuf = new char[index];
            System.arraycopy(buf, 0, newBuf, 0, index);
            Resource resource = Resource.create(new File(new String(newBuf).trim()));
            if (null != consumer) {
                consumer.accept(resource);
            }
            result.add(resource);
            p.clear();
        }


        int matchSize = result.size();
        cost1.console(name, matchSize, matchSize, matchSize, matchSize * 1000 / (System.currentTimeMillis() - startTime));
        return result;
    }


    public static interface Everything extends Library {
        Everything EVERYTHING_64 = Native.load("Everything64", Everything.class);

        static final int EVERYTHING_ERROR_MEMORY = 1;
        static final int EVERYTHING_ERROR_IPC = 2;
        static final int EVERYTHING_ERROR_REGISTERCLASSEX = 3;
        static final int EVERYTHING_ERROR_CREATEWINDOW = 4;
        static final int EVERYTHING_ERROR_CREATETHREAD = 5;
        static final int EVERYTHING_ERROR_INVALIDINDEX = 6;
        static final int EVERYTHING_ERROR_INVALIDCALL = 7;

        /**
         * 设置查询条件
         *
         * @param lpSearchString 查询条件
         * @return int
         */
        int Everything_SetSearchW(WString lpSearchString);

        /**
         * 设置查询路径
         *
         * @param bEnable 设置查询路径
         */
        void Everything_SetMatchPath(boolean bEnable);

        /**
         * 设置查询Case
         *
         * @param bEnable 设置查询Case
         */
        void Everything_SetMatchCase(boolean bEnable);

        /**
         * 设置查询全词
         *
         * @param bEnable 设置查询全词
         */
        void Everything_SetMatchWholeWord(boolean bEnable);

        /**
         * 设置查询正则
         *
         * @param bEnable 设置查询正则
         */
        void Everything_SetRegex(boolean bEnable);

        /**
         * 设置查询最大值
         *
         * @param dwMax 设置查询最大值
         */
        void Everything_SetMax(int dwMax);

        /**
         * 设置查询位置
         *
         * @param dwOffset 设置查询位置
         */
        void Everything_SetOffset(int dwOffset);

        /**
         * 获取查询路径
         *
         * @return boolean
         */
        boolean Everything_GetMatchPath();

        /**
         * 获取查询Case
         *
         * @return boolean
         */
        boolean Everything_GetMatchCase();

        /**
         * 获取查询全词
         *
         * @return boolean
         */
        boolean Everything_GetMatchWholeWord();

        /**
         * 获取查询正则
         *
         * @return boolean
         */
        boolean Everything_GetRegex();

        /**
         * 获取查询最大值
         *
         * @return int
         */
        int Everything_GetMax();

        /**
         * 获取查询位置
         *
         * @return int
         */
        int Everything_GetOffset();

        /**
         * 获取查询索引
         *
         * @return 索引
         */
        WString Everything_GetSearchW();

        /**
         * 获取查询异常信息
         *
         * @return int
         */
        int Everything_GetLastError();

        /**
         * 设置查询是否排序
         *
         * @param bWait 设置查询是否排序
         * @return boolean
         */
        boolean Everything_QueryW(boolean bWait);

        /**
         * 路径排序
         */
        void Everything_SortResultsByPath();

        /**
         * 文件数
         *
         * @return 文件数
         */
        int Everything_GetNumFileResults();

        /**
         * 文件夹数
         *
         * @return 文件夹数
         */
        int Everything_GetNumFolderResults();

        /**
         * 结果数
         *
         * @return 结果数
         */
        int Everything_GetNumResults();

        /**
         * 子文件数
         *
         * @return 子文件数
         */
        int Everything_GetTotFileResults();

        /**
         * 子文件夹数
         *
         * @return 子文件夹数
         */
        int Everything_GetTotFolderResults();

        /**
         * 子结果数
         *
         * @return 子结果数
         */
        int Everything_GetTotResults();

        /**
         * 是否是Volume
         *
         * @param nIndex 索引
         * @return boolean
         */
        boolean Everything_IsVolumeResult(int nIndex);

        /**
         * 是否是Folder
         *
         * @param nIndex 索引
         * @return boolean
         */
        boolean Everything_IsFolderResult(int nIndex);

        /**
         * 是否是File
         *
         * @param nIndex 索引
         * @return boolean
         */
        boolean Everything_IsFileResult(int nIndex);

        /**
         * 获取结果全路径名
         *
         * @param nIndex    索引
         * @param lpString  名称
         * @param nMaxCount 最大数量
         */
        void Everything_GetResultFullPathNameW(int nIndex, Buffer lpString, int nMaxCount);

        /**
         * 重置
         */
        void Everything_Reset();
    }
}
