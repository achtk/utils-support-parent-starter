package com.chua.common.support.geo;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.lang.profile.ProfileProvider;
import com.chua.common.support.resource.ResourceProvider;
import com.chua.common.support.utils.IoUtils;
import lombok.*;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * qqwry
 *
 * @author CH
 */
@Spi("qqwry")
public class IpWryPosition extends ProfileProvider<IpPosition>
        implements IpPosition {
    private static final Pattern DB_VER = Pattern.compile("(\\d+)年(\\d+)月(\\d+)日.*");
    private static final int INDEX_RECORD_LENGTH = 7;
    private static final byte REDIRECT_MODE_1 = 0x01;
    private static final byte REDIRECT_MODE_2 = 0x02;
    private static final byte STRING_END = '\0';

    private byte[] data;
    private long indexHead;
    private long indexTail;
    private String databaseVersion;

    @Override
    @SneakyThrows
    public void afterPropertiesSet() {
        Object database1 = getString("database");
        if (null == database1) {
            this.data = initialClasspath();
        } else {
            this.data =  IoUtils.toByteArray(Converter.convertIfNecessary(database1, InputStream.class));
        }
        indexHead = readLong32(0);
        indexTail = readLong32(4);
        databaseVersion = parseDatabaseVersion();
    }

    /**
     * 初始化
     *
     * @return DatabaseReader
     */
    private byte[] initialClasspath() {
        try {
            this.data = IoUtils.toByteArray(ResourceProvider.of("classpath*:**/qqwry.dat").getResource().getUrl());
        } catch (Throwable ignored) {
        }

        return data;
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public GeoCity getCity(String ip) {
        IpZone ipZone = findIp(ip);
        GeoCity geoCity = new GeoCity();
        String mainInfo = ipZone.getMainInfo();
        String s = "省";
        String[] split = mainInfo.split(s);
        geoCity.ip(ip);

        if (split.length == 2) {
            geoCity.province(split[0] + s);
            geoCity.city(split[1]);
        } else {
            geoCity.city(mainInfo);
        }
        geoCity.isp(ipZone.getSubInfo());
        return geoCity;
    }

    @Override
    public GeoCity getCountry(String ip) {
        return getCity(ip);
    }

    /**
     * 索引
     */
    @AllArgsConstructor
    private static class QwyIndex {
        public final long minIP;
        public final long maxIP;
        public final int recordOffset;
    }

    /**
     * 数据
     */
    @AllArgsConstructor
    private static class QwyString {
        public final String string;
        /**
         * length including the \0 end byte
         */
        public final int length;

    }

    /**
     * 查询地址
     *
     * @param ip ip
     * @return 地址
     */
    public IpZone findIp(final String ip) {
        final long ipNum = toNumericIp(ip);
        final QwyIndex idx = searchIndex(ipNum);
        if (idx == null) {
            return new IpZone(ip);
        }
        return readIp(ip, idx);
    }

    /**
     * 中数
     *
     * @param begin 开始
     * @param end   结束
     * @return 中数
     */
    private long getMiddleOffset(final long begin, final long end) {
        long records = (end - begin) / INDEX_RECORD_LENGTH;
        records >>= 1;
        if (records == 0) {
            records = 1;
        }
        return begin + (records * INDEX_RECORD_LENGTH);
    }

    /**
     * 查询索引
     *
     * @param offset 位置
     * @return 索引
     */
    private QwyIndex readIndex(final int offset) {
        final long min = readLong32(offset);
        final int record = readInt24(offset + 4);
        final long max = readLong32(record);
        return new QwyIndex(min, max, record);
    }

    /**
     * readInt24
     *
     * @param offset 位置
     * @return readInt24
     */
    private int readInt24(final int offset) {
        int v = data[offset] & 0xFF;
        v |= ((data[offset + 1] << 8) & 0xFF00);
        v |= ((data[offset + 2] << 16) & 0xFF0000);
        return v;
    }

    /**
     * readIP
     *
     * @param ip  ip
     * @param idx 位置
     * @return readInt24
     */
    private IpZone readIp(final String ip, final QwyIndex idx) {
        final int pos = idx.recordOffset + 4;
        final byte mode = data[pos];
        final IpZone z = new IpZone(ip);
        if (mode == REDIRECT_MODE_1) {
            final int offset = readInt24(pos + 1);
            if (data[offset] == REDIRECT_MODE_2) {
                readMode2(z, offset);
            } else {
                final QwyString mainInfo = readString(offset);
                final String subInfo = readSubInfo(offset + mainInfo.length);
                z.setMainInfo(mainInfo.string);
                z.setSubInfo(subInfo);
            }
        } else if (mode == REDIRECT_MODE_2) {
            readMode2(z, pos);
        } else {
            final QwyString mainInfo = readString(pos);
            final String subInfo = readSubInfo(pos + mainInfo.length);
            z.setMainInfo(mainInfo.string);
            z.setSubInfo(subInfo);
        }
        return z;
    }

    private long readLong32(final int offset) {
        long v = data[offset] & 0xFFL;
        v |= (data[offset + 1] << 8L) & 0xFF00L;
        v |= ((data[offset + 2] << 16L) & 0xFF0000L);
        v |= ((data[offset + 3] << 24L) & 0xFF000000L);
        return v;
    }

    private void readMode2(final IpZone z, final int offset) {
        final int mainInfoOffset = readInt24(offset + 1);
        final String main = readString(mainInfoOffset).string;
        final String sub = readSubInfo(offset + 4);
        z.setMainInfo(main);
        z.setSubInfo(sub);
    }

    private QwyString readString(final int offset) {
        int i = 0;
        final byte[] buf = new byte[128];
        for (; ; i++) {
            final byte b = data[offset + i];
            if (STRING_END == b) {
                break;
            }
            buf[i] = b;
        }
        try {
            return new QwyString(new String(buf, 0, i, "GB18030"), i + 1);
        } catch (final UnsupportedEncodingException e) {
            return new QwyString("", 0);
        }
    }

    private String readSubInfo(final int offset) {
        final byte b = data[offset];
        if ((b == REDIRECT_MODE_1) || (b == REDIRECT_MODE_2)) {
            final int areaOffset = readInt24(offset + 1);
            if (areaOffset == 0) {
                return "";
            } else {
                return readString(areaOffset).string;
            }
        } else {
            return readString(offset).string;
        }
    }

    private QwyIndex searchIndex(final long ip) {
        long head = indexHead;
        long tail = indexTail;
        while (tail > head) {
            final long cur = getMiddleOffset(head, tail);
            final QwyIndex idx = readIndex((int) cur);
            if ((ip >= idx.minIP) && (ip <= idx.maxIP)) {
                return idx;
            }
            if ((cur == head) || (cur == tail)) {
                return idx;
            }
            if (ip < idx.minIP) {
                tail = cur;
            } else if (ip > idx.maxIP) {
                head = cur;
            } else {
                return idx;
            }
        }
        return null;
    }

    private long toNumericIp(final String s) {
        final String[] parts = s.split("\\.");
        int s4 = 4;
        if (parts.length != s4) {
            throw new IllegalArgumentException("ip = " + s);
        }
        long n = Long.parseLong(parts[0]) << 24L;
        n += Long.parseLong(parts[1]) << 16L;
        n += Long.parseLong(parts[2]) << 8L;
        n += Long.parseLong(parts[3]);
        return n;
    }

    /**
     * 版本
     */
    public String getDatabaseVersion() {
        return databaseVersion;
    }

    /**
     * 版本
     *
     * @return 版本
     */
    String parseDatabaseVersion() {
        IpZone ipz = findIp("255.255.255.255");
        Matcher m = DB_VER.matcher(ipz.getSubInfo());
        int s3 = 3;
        if (!m.matches() || m.groupCount() != s3) {
            return "0.0.0";
        }
        return String.format("%s.%s.%s", m.group(1), m.group(2), m.group(3));
    }


    /**
     * ipzone
     */
    @RequiredArgsConstructor
    @Data
    public class IpZone {
        @NonNull
        private final String ip;
        private String mainInfo = "";
        private String subInfo = "";

        @Override
        public String toString() {
            return new StringBuilder(mainInfo).append(subInfo).toString();
        }

    }

}
