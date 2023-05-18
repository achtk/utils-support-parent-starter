package com.chua.common.support.spi.condition;

import com.chua.common.support.annotations.SpiCondition;
import com.chua.common.support.constant.Projects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 是否有网
 *
 * @author CH
 */
public class PingCondition implements SpiCondition.Condition {
    @Override
    public boolean isCondition() {
        try {
            return ping("www.baidu.com", 35);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 是否有网
     *
     * @param targetName IP地址或域名
     * @param outTime    超时间隔，单位为毫秒
     * @return 是否有网
     * @throws IOException
     */
    public static boolean ping(String targetName, int outTime)
            throws IOException {
        Runtime runtime = Runtime.getRuntime();
        String pingCommand = "ping " + targetName + " -w " + outTime;
        Process process = runtime.exec(pingCommand);

        if (null == process) {
            return false;
        }

        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), Projects.isWindows() ? "GBK" : "UTF-8"))) {

            String line = null;

            while (null != (line = bufferedReader.readLine())) {
                if(line.contains("来自") && line.contains("回复") && line.contains("字节")) {
                    return true;
                }

                if (line.startsWith("bytes from", 3)) {
                    return true;
                }

                if (line.startsWith("from")) {
                    return true;
                }
            }

        }
        return false;
    }
}
