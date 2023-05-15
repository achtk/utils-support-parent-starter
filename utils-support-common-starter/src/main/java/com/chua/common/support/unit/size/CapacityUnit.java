package com.chua.common.support.unit.size;


/**
 * 数据单位封装<p>
 * 此类来自于：Spring-framework
 *
 * <pre>
 *     BYTES      1B      2^0     1
 *     KILOBYTES  1KB     2^10    1,024
 *     MEGABYTES  1MB     2^20    1,048,576
 *     GIGABYTES  1GB     2^30    1,073,741,824
 *     TERABYTES  1TB     2^40    1,099,511,627,776
 * </pre>
 *
 * @author Sam Brannen，Stephane Nicoll
 * @since 5.3.10
 */
public enum CapacityUnit {

    /**
     * Bytes, 后缀表示为： {@code B}.
     */
    BYTES("B", CapacitySize.ofBytes(1)),

    /**
     * Kilobytes, 后缀表示为： {@code KB}.
     */
    KILOBYTES("KB", CapacitySize.ofKilobytes(1)),

    /**
     * Megabytes, 后缀表示为： {@code MB}.
     */
    MEGABYTES("MB", CapacitySize.ofMegabytes(1)),

    /**
     * Gigabytes, 后缀表示为： {@code GB}.
     */
    GIGABYTES("GB", CapacitySize.ofGigabytes(1)),

    /**
     * Terabytes, 后缀表示为： {@code TB}.
     */
    TERABYTES("TB", CapacitySize.ofTerabytes(1));

    public static final String[] UNIT_NAMES = new String[]{"B", "KB", "MB", "GB", "TB", "PB", "EB"};

    private final String suffix;

    private final CapacitySize size;


    CapacityUnit(String suffix, CapacitySize size) {
        this.suffix = suffix;
        this.size = size;
    }

    /**
     * 通过后缀返回对应的 DataUnit
     *
     * @param suffix 单位后缀
     * @return 匹配到的{@link DataUnit}
     * @throws IllegalArgumentException 后缀无法识别报错
     */
    public static CapacityUnit fromSuffix(String suffix) {
        for (CapacityUnit candidate : values()) {
            // 支持类似于 3MB，3M，3m等写法
            if (candidate.suffix.startsWith(suffix)) {
                return candidate;
            }
        }
        throw new IllegalArgumentException("Unknown data unit suffix '" + suffix + "'");
    }

    CapacitySize size() {
        return this.size;
    }

}
