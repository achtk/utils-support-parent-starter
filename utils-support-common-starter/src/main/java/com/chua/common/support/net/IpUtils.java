package com.chua.common.support.net;

import com.chua.common.support.geo.IPv6Info;
import com.chua.common.support.geo.Ipv4Info;
import com.chua.common.support.matcher.PathMatcher;
import com.chua.common.support.range.IpRange;
import com.chua.common.support.utils.StringUtils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.regex.Pattern;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * 地址处理
 *
 * @author CH
 */
public class IpUtils {

    private static final Pattern IP_V4_PATTERN = Pattern.compile("([0-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}");
    /**
     * 判断给定字符串是否是单个IPv4地址
     *
     * @param str
     * @return
     */
    public static boolean isIp(String str) {
        if (str == null) {
            return false;
        }
        if (IP_V4_PATTERN.matcher(str).matches()) {
            return true;
        }

        return false;
    }

    /**
     * 判断给定数字是否是合法的端口（数字范围在0到65535之间）
     *
     * @param port 端口数字
     * @return true：是合法端口、false：不是合法端口
     */
    public static boolean isValidPort(int port) {
        return port >= 0 && port <= 65535;
    }
    /**
     * 目标地址是否匹配源地址
     *
     * @param sourceAddress 源地址
     * @param targetAddress 目标地址
     * @return 目标地址是否匹配源地址
     */
    public static boolean isMatch(String sourceAddress, String targetAddress) {
        if (!sourceAddress.contains(SYMBOL_ASTERISK) && !sourceAddress.contains(SYMBOL_MINS)) {
            return sourceAddress.equals(targetAddress);
        }
        if (sourceAddress.contains(SYMBOL_ASTERISK) && !sourceAddress.contains(SYMBOL_MINS)) {
            return PathMatcher.INSTANCE.match(sourceAddress, targetAddress);
        }

        if (!sourceAddress.contains(SYMBOL_ASTERISK) && sourceAddress.equals(SYMBOL_MINS)) {
            String[] split = sourceAddress.split(SYMBOL_COMMA, 2);
            if (split.length != 2) {
                return false;
            }
            String start = split[0];
            String end = split[1];
            IpRange ipRange = new IpRange(start, end);
            return ipRange.inRange(targetAddress);
        }

        return false;
    }


    /**
     * 把字符串IP转换成long
     *
     * @param ipStr 字符串IP
     * @return IP对应的long值
     */
    public static long ip2Long(String ipStr) {
        String[] ip = ipStr.split("\\.");
        return (Long.valueOf(ip[0]) << 24) + (Long.valueOf(ip[1]) << 16)
                + (Long.valueOf(ip[2]) << 8) + Long.valueOf(ip[3]);
    }
    /**
     * 把字符串IP转换成byte
     *
     * @param ipStr 字符串IP
     * @return IP对应的byte值
     */
    public static byte[] ip2Byte(String ipStr) {
        return int2Byte(((Long)ip2Long(ipStr)).intValue());
    }
    /**
     * 把字符串IP转换成byte
     *
     * @param ip 字符串IP
     * @return IP对应的byte值
     */
    public static byte[] int2Byte(int ip) {
        byte[] targets = new byte[4];
        targets[3] = (byte) (ip & 0xFF);
        targets[2] = (byte) (ip >> 8 & 0xFF);
        targets[1] = (byte) (ip >> 16 & 0xFF);
        targets[0] = (byte) (ip >> 24 & 0xFF);
        return targets;
    }
    /**
     * 把IP的long值转换成字符串
     *
     * @param ipLong IP的long值
     * @return long值对应的字符串
     */
    public static String long2Ip(long ipLong) {
        StringBuilder ip = new StringBuilder();
        ip.append(ipLong >>> 24).append(".");
        ip.append((ipLong >>> 16) & 0xFF).append(".");
        ip.append((ipLong >>> 8) & 0xFF).append(".");
        ip.append(ipLong & 0xFF);
        return ip.toString();
    }

    /**
     * 校验IP是否合法
     *
     * @param ip ip
     * @return 结果
     */
    public static boolean isMatch(String ip) {
        return StringUtils.isNotEmpty(ip) && ip.matches(IPV4);
    }


    /**
     * 获取本地地址
     *
     * @return 本地地址
     */
    public static String getLocalHost() {
        return NetUtils.getLocalIpv4();
    }

    /**
     * 获取有效的端口
     *
     * @return 有效的端口
     */
    public static int getAvailablePort() {
        return NetUtils.getAvailablePort();
    }

    /**
     * hex -> byte array
     *
     * @param hex hex
     * @return byte[]
     */
    public static byte[] hexStringToByte(String hex) {
        byte[] b = new byte[hex.length() / 2];
        int j = 0;
        for (int i = 0; i < b.length; i++) {
            char c0 = hex.charAt(j++);
            char c1 = hex.charAt(j++);
            b[i] = (byte) ((parse(c0) << 4) | parse(c1));
        }
        return b;
    }

    /**
     * 解析char
     *
     * @param c char
     * @return int
     */
    public static int parse(char c) {
        if (c >= 'a') {
            return (c - 'a' + 10) & 0x0f;
        }
        if (c >= 'A') {
            return (c - 'A' + 10) & 0x0f;
        }
        return (c - '0') & 0x0f;
    }

    /**
     * 檢驗
     *
     * @param ipv4Bytes ipv4
     * @return 檢驗
     */
    private static Ipv4Info getIpv4Info(byte[] ipv4Bytes) {
        if (ipv4Bytes.length != 4) {
            throw new IllegalArgumentException("ipv4 must be 4 bytes length");
        }

        return null;

    }

    /**
     * byte -> string
     *
     * @param ipv4Bytes ip
     * @return string
     */
    public static String ipv4BytesToString(byte[] ipv4Bytes) {
        return ipv4BytesToString(ipv4Bytes, 0, ipv4Bytes.length);
    }

    /**
     * byte -> string
     *
     * @param ipv4Bytes ip
     * @param offset    offset
     * @param length    length ( == 4)
     * @return string
     */
    public static String ipv4BytesToString(byte[] ipv4Bytes, int offset, int length) {
        if (length != 4) {
            throw new IllegalArgumentException("ipv4 must be 4 bytes length");
        }
        StringBuilder builder = new StringBuilder();
        for (int i = offset; i < offset + length; i++) {
            int ipSegment = Byte.toUnsignedInt(ipv4Bytes[i]);
            if (i != offset) {
                builder.append('.');
            }
            builder.append(ipSegment);
        }
        return builder.toString();
    }

    /**
     * byte -> string
     *
     * @param ipBytes ip
     * @param offset  offset
     * @param length  length ( == 4 || == 16)
     * @return string
     */
    public static String ipBytesToString(byte[] ipBytes, int offset, int length) {
        if (length == 4) {
            return ipv4BytesToString(ipBytes, offset, length);
        } else if (length == 16) {
            return getShortIpv6(ipBytes, offset, length);
        } else {
            throw new IllegalArgumentException("Illegal ip length");
        }
    }

    /**
     * 檢驗
     *
     * @param ipv6Bytes ipv6
     * @return 檢驗
     */
    public static IPv6Info getIpv6Info(byte[] ipv6Bytes) {
        if (ipv6Bytes.length != 16) {
            throw new IllegalArgumentException("ipv6 must be 16 bytes length");
        }

        // bytes 转 BigInteger
        // bytes 转 人易读格式
        BigInteger integer = new BigInteger(ipv6Bytes);

        //IPv6InfoUtils.getIpInfo()

        return null;
    }

    /**
     * 获取IPv6简写格式
     *
     * @param ipv6Bytes v6
     * @param offset    offset
     * @param length    length
     * @return string
     */
    public static String getShortIpv6(byte[] ipv6Bytes, int offset, int length) {
        StringBuilder builder = new StringBuilder();
        int endPosition = offset + length;
        int zeroCount = 0;
        for (int i = offset; i < endPosition; i += 2) {
            int i1 = ipv6Bytes[i] & 0xFF;
            int i2 = ipv6Bytes[i + 1] & 0xFF;

            if (i == 0 && i1 == 0 && i2 == 0) {
                builder.append(':');
            }

            if (i1 != 0) {
                String s1 = Integer.toHexString(i1);
                builder.append(s1);
            }

            String s2 = Integer.toHexString(i2);
            if (s2.length() < 2 && i1 != 0) {
                builder.append('0');
            }
            builder.append(s2);
            if (i != ipv6Bytes.length - 2) {
                builder.append(':');
            }

            if (i1 == 0 && i2 == 0) {
                zeroCount++;
            } else {
                zeroCount = 0;
            }

            if (zeroCount > 1) {
                if (zeroCount == 2) {
                    builder.delete(builder.length() - 4, builder.length());
                    builder.append(':');
                } else {
                    builder.delete(builder.length() - 2, builder.length());
                }
            }

            boolean b = zeroCount == 8 || (i == 14 && zeroCount > 1);
            if (b) {
                builder.append(':');
            }

        }

        String ip = builder.toString();
        if (ip.endsWith(":") && !ip.endsWith("::")) {
            ip = ip.substring(0, ip.lastIndexOf(':'));
        }
        return ip;
    }

    /**
     * format hex -> 0000:0000xxxxx
     *
     * @param ipv6Hex hex
     * @return v6
     */
    public static String formatIpv6(String ipv6Hex) {
        char[] chars = ipv6Hex.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            builder.append(chars[i]);
            if ((i + 1) % 4 == 0 && i != chars.length - 1) {
                builder.append(':');
            }
        }
        return builder.toString();
    }

    /**
     * 解析ip
     *
     * @param ip ip
     * @return 結果
     */
    public static String parseIp(List<Integer> ip) {
        if (ip.size() == 1) {
            long l = Integer.toUnsignedLong(ip.get(0));
            return long2Ip(l);
        } else {
            return leftPadZero(Integer.toUnsignedString(ip.get(0), 16), 8) +
                    leftPadZero(Integer.toUnsignedString(ip.get(1), 16), 8) +
                    leftPadZero(Integer.toUnsignedString(ip.get(2), 16), 8) +
                    leftPadZero(Integer.toUnsignedString(ip.get(3), 16), 8);
        }
    }

    /**
     * 解析ip
     *
     * @param ipBytes     ip
     * @param offset offset
     * @param length length
     * @return 結果
     */
    public static String parseIpBytes(byte[] ipBytes, int offset, int length) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(ipBytes, offset, length);
        if (length == 4) {
            long l = Integer.toUnsignedLong(byteBuffer.getInt());
            return long2Ip(l);
        } else {
            return leftPadZero(Integer.toUnsignedString(byteBuffer.getInt(), 16), 8) +
                    leftPadZero(Integer.toUnsignedString(byteBuffer.getInt(), 16), 8) +
                    leftPadZero(Integer.toUnsignedString(byteBuffer.getInt(), 16), 8) +
                    leftPadZero(Integer.toUnsignedString(byteBuffer.getInt(), 16), 8);
        }
    }

    /**
     * pad zero
     * @param source source
     * @param size length
     * @return string
     */
    public static String leftPadZero(String source, int size) {
        if (source.length() >= size) {
            return source;
        } else {
            StringBuilder builder = new StringBuilder(source);
            for (int i = 0; i < size - source.length(); i++) {
                builder.insert(0, '0');
            }
            return builder.toString();
        }
    }

    /**
     * 获取完整的ipv6
     * @param ipv6 v6
     * @return  完整的ipv6
     */
    public static String getFullIpv6(String ipv6) {
        //入参为::时，此时全为0
        if ("::".equals(ipv6)) {
            return "0000:0000:0000:0000:0000:0000:0000:0000";
        }
        //入参已::结尾时，直接在后缀加0
        if (ipv6.endsWith("::")) {
            ipv6 += "0";
        }
        String[] arrs = ipv6.split(SYMBOL_COLON);
        StringBuilder symbol = new StringBuilder("::");
        int arrleng = arrs.length;
        while (arrleng < 8) {
            symbol.append(SYMBOL_COLON_CHAR);
            arrleng++;
        }
        ipv6 = ipv6.replace("::", symbol.toString());
        StringBuilder fullip = new StringBuilder();
        for (String ip : ipv6.split(SYMBOL_COLON)) {
            StringBuilder ipBuilder = new StringBuilder(ip);
            while (ipBuilder.length() < 4) {
                ipBuilder.insert(0, "0");
            }
            ip = ipBuilder.toString();
            fullip.append(ip).append(SYMBOL_COLON_CHAR);
        }
        return fullip.substring(0, fullip.length() - 1);
    }

    /**
     * ipv6地址转有符号byte[17]
     * @param ipv6 v6
     * @return byte[17]
     */
    public static byte[] ipv6ToBytes(String ipv6) {
        byte[] ret = new byte[17];
        ret[0] = 0;
        int ib = 16;
        boolean comFlag = false;
        if (ipv6.startsWith(":")) {
            ipv6 = ipv6.substring(1);
        }
        String[] groups = ipv6.split(":");
        for (int ig = groups.length - 1; ig > -1; ig--) {
            if (groups[ig].contains(".")) {
                // 出现ipv4混合模式
                byte[] temp = ipv4ToBytes(groups[ig]);
                ret[ib--] = temp[4];
                ret[ib--] = temp[3];
                ret[ib--] = temp[2];
                ret[ib--] = temp[1];
                comFlag = true;
            } else if ("".equals(groups[ig])) {
                // 出现零长度压缩,计算缺少的组数
                int zlg = 9 - (groups.length + (comFlag ? 1 : 0));
                while (zlg-- > 0) {
                    ret[ib--] = 0;
                    ret[ib--] = 0;
                }
            } else {
                int temp = Integer.parseInt(groups[ig], 16);
                ret[ib--] = (byte) temp;
                ret[ib--] = (byte) (temp >> 8);
            }
        }
        return ret;
    }

    /**
     * ipv4地址转有符号byte[5]
     * @param ipv4 v4
     * @return byte[5]
     */
    public static byte[] ipv4ToBytes(String ipv4) {
        byte[] ret = new byte[5];
        ret[0] = 0;
        // 先找到IP地址字符串中.的位置
        int position1 = ipv4.indexOf(".");
        int position2 = ipv4.indexOf(".", position1 + 1);
        int position3 = ipv4.indexOf(".", position2 + 1);
        // 将每个.之间的字符串转换成整型
        ret[1] = (byte) Integer.parseInt(ipv4.substring(0, position1));
        ret[2] = (byte) Integer.parseInt(ipv4.substring(position1 + 1,
                position2));
        ret[3] = (byte) Integer.parseInt(ipv4.substring(position2 + 1,
                position3));
        ret[4] = (byte) Integer.parseInt(ipv4.substring(position3 + 1));
        return ret;
    }

}
