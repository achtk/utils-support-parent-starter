package com.chua.common.support.geo.transform;

import com.chua.common.support.geo.GeoUtils;
import com.chua.common.support.geo.Point;

/**
 * cgs2000 -> wgs84
 *
 * @author CH
 */
public class CgS2000ToWgs84CoordinateConverter implements CoordinateConverter {
    @Override
    public Point transform(Point point) {
        double longitude = point.longitude(), latitude = point.latitude();
        //中央子午线需根据实际情况设置
        double v1 = 117;
        double lat, lon;
        latitude -= 500000;
        //pi/180
        double ipi = GeoUtils.PI / 180.0;
        //长半轴 m
        double a = 6378137.0;
        //短半轴 m
        double b = 6356752.31414;
        //扁率 a-b/a
        double f = 1 / 298.257222101;
        //第一偏心率 Math.sqrt(5) 0.0818191910428
        double e = Math.sqrt(5);
        //第二偏心率
        double ee = Math.sqrt(a * a - b * b) / b;
        //底点纬度
        double bf = 0;
        double a0 = 1 + (3 * Math.pow(e, 2) / 4) + (45 * Math.pow(e, 4) / 64) + (175 * Math.pow(e, 6) / 256) + (11025 * Math.pow(e, 8) / 16384) + (43659 * Math.pow(e, 10) / 65536);
        double b0 = longitude / (a * (1 - Math.pow(e, 2)) * a0);
        double c1 = 3 * Math.pow(e, 2) / 8 + 3 * Math.pow(e, 4) / 16 + 213 * Math.pow(e, 6) / 2048 + 255 * Math.pow(e, 8) / 4096;
        double c2 = 21 * Math.pow(e, 4) / 256 + 21 * Math.pow(e, 6) / 256 + 533 * Math.pow(e, 8) / 8192;
        double c3 = 151 * Math.pow(e, 8) / 6144 + 151 * Math.pow(e, 8) / 4096;
        double c4 = 1097 * Math.pow(e, 8) / 131072;
        // bf =b0+c1*sin2b0 + c2*sin4b0 + c3*sin6b0 +c4*sin8b0 +...
        bf = b0 + c1 * Math.sin(2 * b0) + c2 * Math.sin(4 * b0) + c3 * Math.sin(6 * b0) + c4 * Math.sin(8 * b0);
        double tf = Math.tan(bf);
        //第二偏心率平方成bf余弦平方
        double n2 = ee * ee * Math.cos(bf) * Math.cos(bf);
        double c = a * a / b;
        double v = Math.sqrt(1 + ee * ee * Math.cos(bf) * Math.cos(bf));
        //子午圈半径
        double mf = c / (v * v * v);
        //卯酉圈半径
        double nf = c / v;

        //纬度计算
        lat = bf - (tf / (2 * mf) * latitude) * (latitude / nf) * (1 - 1 / 12d * (5 + 3 * tf * tf + n2 - 9 * n2 * tf * tf) * (latitude * latitude / (nf * nf)) + 1 / 360 * (61 + 90 * tf * tf + 45 * tf * tf * tf * tf) * (latitude * latitude * latitude * latitude / (nf * nf * nf * nf)));
        //经度偏差
        lon = 1 / (nf * Math.cos(bf)) * latitude - (1 / (6 * nf * nf * nf * Math.cos(bf))) * (1 + 2 * tf * tf + n2) * latitude * latitude * latitude + (1 / (120 * nf * nf * nf * nf * nf * Math.cos(bf))) * (5 + 28 * tf * tf + 24 * tf * tf * tf * tf) * latitude * latitude * latitude * latitude * latitude;

        return Point.builder().longitude(format(v1 + lon / ipi)).latitude(format(lat / ipi)).build();
    }

    /**
     * 格式化
     *
     * @param num 数据
     * @return 结果
     */
    private static double format(double num) {
        String result = String.format("%.6f", num);
        return Double.parseDouble(result);
    }
}
