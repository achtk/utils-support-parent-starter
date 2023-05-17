package com.chua.common.support.geo;

/**
 * v4
 *
 * @author CH
 */
public class Ipv4Info extends GeoCity implements Comparable<Ipv4Info> {
    /**
     * 起始IP数字表示
     */
    private long startLong;

    /**
     * 终止IP数字表示
     */
    private long endLong;

    public Ipv4Info(long startLong, long endLong) {
        this.startLong = startLong;
        this.endLong = endLong;
    }

    public Ipv4Info(long startLong, long endLong, String country, String province, String address, String isp, double lat, double lng, String city) {
        super();
        this.country(country)
                .province(province)
                .ip(address)
                .latitude(lat)
                .longitude(lng)
                .city(city)
                .isp(isp);
        this.startLong = startLong;
        this.endLong = endLong;
    }

    public long getStartLong() {
        return startLong;
    }

    public void setStartLong(long startLong) {
        this.startLong = startLong;
    }

    public long getEndLong() {
        return endLong;
    }

    public void setEndLong(long endLong) {
        this.endLong = endLong;
    }

    @Override
    public int compareTo(Ipv4Info o) {
        if (this.startLong == o.startLong || this.startLong == o.endLong || this.endLong == o.startLong || this.endLong == o.endLong) {
            return 0;
        } else if (this.startLong > o.endLong) {
            return 1;
        } else if (this.endLong < o.startLong) {
            return -1;
        } else if (this.startLong > o.startLong && this.endLong < o.endLong) {
            return 0;
        }
        return 0;
    }


    @Override
    public String toString() {
        return "IPv4Info{" +
                "startLong=" + startLong +
                ", endLong=" + endLong +
                "} " + super.toString();
    }
}
