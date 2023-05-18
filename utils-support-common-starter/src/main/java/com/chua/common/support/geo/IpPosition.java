package com.chua.common.support.geo;

import com.chua.common.support.function.InitializingAware;

import java.awt.geom.Path2D;
import java.util.List;


/**
 * geo定位
 *
 * @author CH
 */
public interface IpPosition extends InitializingAware, AutoCloseable {
    double MIN_LAT = -90;
    double MAX_LAT = 90;
    double MIN_LNG = -180;
    double MAX_LNG = 180;
    double EARTH_RADIUS = 6378137;

    /**
     * 获取rad
     *
     * @param d 值
     * @return rad
     */
    static double rad(double d) {
        return d * Math.PI / 180.0d;
    }

    /**
     * 经度最小值
     *
     * @param mPoints 经纬度集合
     * @return 经度最小值
     */
    static double getMinLongitude(List<Point> mPoints) {
        double minLongitude = MAX_LNG;
        if (mPoints.size() > 0) {
            minLongitude = mPoints.get(0).longitude();
            for (Point latlng : mPoints) {
                // 经度最小值
                if (latlng.longitude() < minLongitude) {
                    minLongitude = latlng.longitude();
                }
            }
        }
        return minLongitude;
    }

    /**
     * 经度最大值
     *
     * @param mPoints 经纬度集合
     * @return 经度最大值
     */
    static double getMaxLongitude(List<Point> mPoints) {
        double maxLongitude = MIN_LNG;
        if (mPoints.size() > 0) {
            maxLongitude = mPoints.get(0).longitude();
            for (Point latlng : mPoints) {
                // 经度最大值
                if (latlng.longitude() > maxLongitude) {
                    maxLongitude = latlng.longitude();
                }
            }
        }
        return maxLongitude;
    }

    /**
     * 纬度最小值
     *
     * @param mPoints 经纬度集合
     * @return 纬度最小值
     */
    static double getMinLatitude(List<Point> mPoints) {
        double minLatitude = MAX_LAT;
        if (mPoints.size() > 0) {
            minLatitude = mPoints.get(0).latitude();
            for (Point latlng : mPoints) {
                // 纬度最小值
                if (latlng.latitude() < minLatitude) {
                    minLatitude = latlng.latitude();
                }
            }
        }
        return minLatitude;
    }

    /**
     * 纬度最大值
     *
     * @param mPoints 经纬度集合
     * @return 纬度最大值
     */
    static double getMaxLatitude(List<Point> mPoints) {
        double maxLatitude = MIN_LAT;
        if (mPoints.size() > 0) {
            maxLatitude = mPoints.get(0).latitude();
            for (Point latlng : mPoints) {
                // 纬度最大值
                if (latlng.latitude() > maxLatitude) {
                    maxLatitude = latlng.latitude();
                }
            }
        }
        return maxLatitude;
    }

    /**
     * 获取距离
     *
     * @param point1 点1
     * @param point2 点2
     * @return 距离
     */
    static double getDistance(Point point1, Point point2) {
        double radLat1 = rad(point1.latitude());
        double radLat2 = rad(point2.latitude());
        double a = radLat1 - radLat2;
        double b = rad(point1.longitude()) - rad(point2.longitude());
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        return s;
    }

    /**
     * 判断经纬度是否在区域中
     *
     * @param circle 圆的经纬度
     * @param radius 半径
     * @param point  经纬度
     * @return 判断经纬度是否在区域中
     */
    static boolean inCircle(Point point, Point circle, double radius) {
        double distance = getDistance(point, circle);
        return distance < radius;
    }

    /**
     * 判断经纬度是否在区域中以及边界
     *
     * @param circle 圆的经纬度
     * @param radius 半径
     * @param point  经纬度
     * @return 判断经纬度是否在区域中
     */
    static boolean inCircleOrBorder(Point point, Point circle, double radius) {
        double distance = getDistance(point, circle);
        return distance <= radius;
    }

    /**
     * 判断经纬度是否在区域中
     *
     * @param pointList 区域
     * @param point     经纬度
     * @return 判断经纬度是否在区域中
     */
    static boolean inPolygon(List<Point> pointList, Point point) {
        Path2D.Double generalPath = new Path2D.Double();
        Point point1 = pointList.get(0);
        generalPath.moveTo(point.longitude(), point1.latitude());

        List<Point> points = pointList.subList(1, pointList.size());
        for (Point point2 : points) {
            generalPath.lineTo(point2.longitude(), point2.latitude());
        }
        generalPath.lineTo(point1.longitude(), point1.latitude());
        generalPath.closePath();

        return generalPath.contains(point.longitude(), point.latitude());
    }

    /**
     * 获取不规则多边形中心点
     *
     * @param pointList 经纬度集合
     * @return 多边形中心
     */
    static Point getCenterPoint(List<Point> pointList) {
        double latitude = (getMinLatitude(pointList) + getMaxLatitude(pointList)) / 2;
        double longitude = (getMinLongitude(pointList) + getMaxLongitude(pointList)) / 2;
        return new Point(latitude, longitude);
    }


    /**
     * 获取不规则多边形重心点
     *
     * @param pointList 经纬度集合
     * @return 重心点
     */
    static Point getGravityPoint(List<Point> pointList) {
        //多边形面积
        double area = 0.0;
        // 重心的x、y
        double gx = 0.0, gy = 0.0;
        for (int i = 1; i <= pointList.size(); i++) {
            double iLat = pointList.get(i % pointList.size()).latitude();
            double iLng = pointList.get(i % pointList.size()).longitude();
            double nextLat = pointList.get(i - 1).latitude();
            double nextLng = pointList.get(i - 1).longitude();
            double temp = (iLat * nextLng - iLng * nextLat) / 2.0;
            area += temp;
            gx += temp * (iLat + nextLat) / 3.0;
            gy += temp * (iLng + nextLng) / 3.0;
        }
        gx = gx / area;
        gy = gy / area;

        return new Point(gx, gy);
    }

    /**
     * ip获取城市
     *
     * @param ip ip
     * @return 城市
     */
    GeoCity getCity(String ip);

    /**
     * ip获取国家
     *
     * @param ip ip
     * @return 城市
     */
    GeoCity getCountry(String ip);
}
