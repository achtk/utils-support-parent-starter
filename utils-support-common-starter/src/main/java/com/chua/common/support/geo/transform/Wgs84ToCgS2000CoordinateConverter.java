package com.chua.common.support.geo.transform;

import com.chua.common.support.geo.GeoUtils;
import com.chua.common.support.geo.Point;

/**
 * wgs84 -> cgs2000
 *
 * @author CH
 */
public class Wgs84ToCgS2000CoordinateConverter implements CoordinateConverter {
    @Override
    public Point transform(Point point) {
        double longitude1, latitude1, longitude0, x0, y0, xVal, yVal;
        //NN曲率半径，测量学里面用N表示
        //M为子午线弧长，测量学里用大X表示
        //fai为底点纬度，由子午弧长反算公式得到，测量学里用Bf表示
        //R为底点所对的曲率半径，测量学里用Nf表示
        double a, f, e2, ee, nn, t, c, a1, m, iPi;
        //3.1415926535898/180.0;
        iPi = GeoUtils.PI / 180.0;
        //CGCS2000坐标系参数
        a = 6378137.0;
        f = 1 / 298.257222101;
        //wgs84坐标系参数
        //a=6378137.0; f=1/298.2572236;
        //中央子午线 根据实际进行配置
        longitude0 = 117;
        //中央子午线转换为弧度
        longitude0 = longitude0 * iPi;
        //经度转换为弧度
        longitude1 = point.longitude() * iPi;
        //纬度转换为弧度
        latitude1 = point.latitude() * iPi;
        e2 = 2 * f - f * f;
        ee = e2 * (1.0 - e2);
        nn = a / Math.sqrt(1.0 - e2 * Math.sin(latitude1) * Math.sin(latitude1));
        t = Math.tan(latitude1) * Math.tan(latitude1);
        c = ee * Math.cos(latitude1) * Math.cos(latitude1);
        a1 = (longitude1 - longitude0) * Math.cos(latitude1);
        m = a * ((1 - e2 / 4 - 3 * e2 * e2 / 64 - 5 * e2 * e2 * e2 / 256) * latitude1 - (3 * e2 / 8 + 3 * e2 * e2 / 32 + 45 * e2 * e2
                * e2 / 1024) * Math.sin(2 * latitude1)
                + (15 * e2 * e2 / 256 + 45 * e2 * e2 * e2 / 1024) * Math.sin(4 * latitude1) - (35 * e2 * e2 * e2 / 3072) * Math.sin(6 * latitude1));
        xVal = nn * (a1 + (1 - t + c) * a1 * a1 * a1 / 6 + (5 - 18 * t + t * t + 72 * c - 58 * ee) * a1 * a1 * a1 * a1 * a1 / 120);
        yVal = m + nn * Math.tan(latitude1) * (a1 * a1 / 2 + (5 - t + 9 * c + 4 * c * c) * a1 * a1 * a1 * a1 / 24
                + (61 - 58 * t + t * t + 600 * c - 330 * ee) * a1 * a1 * a1 * a1 * a1 * a1 / 720);
        x0 = 500000L;
        y0 = 0;
        xVal = xVal + x0;
        yVal = yVal + y0;

        return Point.builder().longitude(xVal).latitude(yVal).build();
    }
}
