package com.chua.common.support.geo;

import java.awt.geom.Path2D;

import static java.lang.Math.round;

/**
 * 经纬度
 *
 * @author CH
 */
public class GeoUtils {

    private static final double X_PI = 3.14159265358979324 * 3000.0 / 180.0;
    public static final double PI = 3.1415926535897932384626;
    /**
     * 赤道半径
     */
    private static final double A = 6378137.0;
    private static final double EE = 0.00669342162296594323;

    /**
     * 地球半径：6378.137KM
     */
    private static final double EARTH_RADIUS = 6378.137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 得到两点间的距离 米
     *
     * @param point1 第一点经纬度
     * @param point2 第二点经纬度
     * @return 得到两点间的距离
     */
    public static double getDistanceOfMeter(Point point1, Point point2) {
        double radLat1 = rad(point1.latitude());
        double radLat2 = rad(point2.latitude());
        double a = radLat1 - radLat2;
        double b = rad(point1.longitude()) - rad(point2.longitude());
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        return round(s * 10000d) / 10d;
    }


    /**
     * 判断是否在多边形区域内
     *
     * @param checkPoint    要判断的点的经纬度
     * @param polygonPoints 区域各顶点的经纬度数组
     * @return 判断是否在多边形区域内
     */
    public static boolean isInPolygon(Point checkPoint, Point[] polygonPoints) {
        Path2D.Double generalPath = new Path2D.Double();
        Point point1 = polygonPoints[0];
        generalPath.moveTo(checkPoint.longitude(), checkPoint.latitude());

        for (int i = 1, pointsSize = polygonPoints.length; i < pointsSize; i++) {
            Point point2 = polygonPoints[i];
            generalPath.lineTo(point2.longitude(), point2.latitude());
        }
        generalPath.lineTo(point1.longitude(), point1.latitude());
        generalPath.closePath();

        return generalPath.contains(checkPoint.longitude(), checkPoint.latitude());
    }

    /**
     * 判断一个点是否在圆形区域内，单位米
     *
     * @param checkPoint   监测点
     * @param circlePoint  圆心
     * @param circleRadius 半径
     */
    public static boolean isInCircle(Point checkPoint, Point circlePoint, double circleRadius) {
        double distance = getDistanceOfMeter(checkPoint, circlePoint);
        return distance < circleRadius;
    }


    /**
     * 判断一个点是否在圆形区域内以及边界，单位米
     *
     * @param checkPoint   监测点
     * @param circlePoint  圆心
     * @param circleRadius 半径
     */
    public static boolean isInCircleBoundary(Point checkPoint, Point circlePoint, double circleRadius) {
        double distance = getDistanceOfMeter(checkPoint, circlePoint);
        return distance <= circleRadius;
    }

    /**
     * 百度坐标（BD09）转 GCJ02
     *
     * @param lng 百度经度
     * @param lat 百度纬度
     * @return GCJ02 坐标：[经度，纬度]
     */
    public static double[] transformBd09ToGcJ02(double lng, double lat) {
        double x = lng - 0.0065;
        double y = lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * X_PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * X_PI);
        double gcjLng = z * Math.cos(theta);
        double gcjLat = z * Math.sin(theta);
        return new double[]{gcjLng, gcjLat};
    }

    /**
     * GCJ02 转百度坐标
     *
     * @param lng GCJ02 经度
     * @param lat GCJ02 纬度
     * @return 百度坐标：[经度，纬度]
     */
    public static double[] transformGcJ02ToBd09(double lng, double lat) {
        double z = Math.sqrt(lng * lng + lat * lat) + 0.00002 * Math.sin(lat * X_PI);
        double theta = Math.atan2(lat, lng) + 0.000003 * Math.cos(lng * X_PI);
        double bdLng = z * Math.cos(theta) + 0.0065;
        double bdLat = z * Math.sin(theta) + 0.006;
        return new double[]{bdLng, bdLat};
    }

    /**
     * GCJ02 转 WGS84
     *
     * @param lng 经度
     * @param lat 纬度
     * @return WGS84坐标：[经度，纬度]
     */
    public static double[] transformGcJ02ToWgS84(double lng, double lat) {
        if (outOfChina(lng, lat)) {
            return new double[]{lng, lat};
        } else {
            double dLat = transformLat(lng - 105.0, lat - 35.0);
            double dLng = transformLng(lng - 105.0, lat - 35.0);
            double radLat = lat / 180.0 * PI;
            double magic = Math.sin(radLat);
            magic = 1 - EE * magic * magic;
            double sqrtMagic = Math.sqrt(magic);
            dLat = (dLat * 180.0) / ((A * (1 - EE)) / (magic * sqrtMagic) * PI);
            dLng = (dLng * 180.0) / (A / sqrtMagic * Math.cos(radLat) * PI);
            double mgLat = lat + dLat;
            double mgLng = lng + dLng;
            return new double[]{lng * 2 - mgLng, lat * 2 - mgLat};
        }
    }

    /**
     * WGS84 坐标 转 GCJ02
     *
     * @param lng 经度
     * @param lat 纬度
     * @return GCJ02 坐标：[经度，纬度]
     */
    public static double[] transformWgs84ToGcJ02(double lng, double lat) {
        if (outOfChina(lng, lat)) {
            return new double[]{lng, lat};
        } else {
            double dLat = transformLat(lng - 105.0, lat - 35.0);
            double dLng = transformLng(lng - 105.0, lat - 35.0);
            double redLat = lat / 180.0 * PI;
            double magic = Math.sin(redLat);
            magic = 1 - EE * magic * magic;
            double sqrtMagic = Math.sqrt(magic);
            dLat = (dLat * 180.0) / ((A * (1 - EE)) / (magic * sqrtMagic) * PI);
            dLng = (dLng * 180.0) / (A / sqrtMagic * Math.cos(redLat) * PI);
            double mgLat = lat + dLat;
            double mgLng = lng + dLng;
            return new double[]{mgLng, mgLat};
        }
    }

    /**
     * 百度坐标BD09 转 WGS84
     *
     * @param lng 经度
     * @param lat 纬度
     * @return WGS84 坐标：[经度，纬度]
     */
    public static double[] transformBd09ToWgs84(double lng, double lat) {
        double[] lngLat = transformBd09ToGcJ02(lng, lat);

        return transformGcJ02ToWgS84(lngLat[0], lngLat[1]);
    }

    /**
     * WGS84 转 百度坐标BD09
     *
     * @param lng 经度
     * @param lat 纬度
     * @return BD09 坐标：[经度，纬度]
     */
    public static double[] transformWgs84ToBd09(double lng, double lat) {
        double[] lngLat = transformWgs84ToGcJ02(lng, lat);

        return transformGcJ02ToBd09(lngLat[0], lngLat[1]);
    }

    /**
     * 纬度转化
     *
     * @param lng 经度
     * @param lat 纬度
     * @return 结果
     */
    private static double transformLat(double lng, double lat) {
        double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * 经度转化
     *
     * @param lng 经度
     * @param lat 纬度
     * @return 结果
     */
    private static double transformLng(double lng, double lat) {
        double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }


    /**
     * 判断坐标是否不在国内
     *
     * @param lng 经度
     * @param lat 纬度
     * @return 坐标是否在国内
     */
    public static boolean outOfChina(double lng, double lat) {
        return (lng < 72.004 || lng > 137.8347) || (lat < 0.8293 || lat > 55.8271);
    }

}
