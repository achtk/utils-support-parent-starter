package com.chua.common.support.geo.transform;

import com.chua.common.support.collection.ImmutableBuilder;
import com.chua.common.support.collection.Table;
import com.chua.common.support.geo.Point;

import java.io.Serializable;

import static com.chua.common.support.utils.Preconditions.checkNotNull;


/**
 * 类型
 * -----------------------------------------------------------------------------------------
 * 坐标系    |解释                                                              |使用地图
 * -----------------------------------------------------------------------------------------
 * WGS84    |地球坐标系，国际上通用的坐标系。设备一般包含GPS芯片或者北斗芯片获取          |GPS/谷歌地图卫星
 * |的经纬度为WGS84地理坐标系,最基础的坐标，谷歌地图在非中国地区使用的坐标系     |
 * -----------------------------------------------------------------------------------------
 * GCJ02    |火星坐标系，是由中国国家测绘局制订的地理信息系统的坐标系统。                 |腾讯(搜搜)地图，
 * |并要求在中国使用的地图产品使用的都必须是加密后的坐标，                      |阿里云地图，高德地图，
 * |而这套WGS84加密后的坐标就是gcj02。                                    |谷歌国内地图
 * -----------------------------------------------------------------------------------------
 * BD09     |百度坐标系，百度在GCJ02的基础上进行了二次加密，                           |百度地图
 * |官方解释是为了进一步保护用户隐私（我差点就信了）                           |
 * -----------------------------------------------------------------------------------------
 * 小众坐标系 |类似于百度地图，在GCJ02基础上使用自己的加密算法进行二次加密的坐标系           |搜狗地图、图吧地图 等
 * -----------------------------------------------------------------------------------------
 * 墨卡托坐标  |墨卡托投影以整个世界范围，赤道作为标准纬线，本初子午线作为中央经线，
 * |两者交点为坐标原点，向东向北为正，向西向南为负。
 * |南北极在地图的正下、上方，而东西方向处于地图的正右、左。
 *
 * @author CH
 */
public enum Coordinate {


    /**
     * wgs84
     */
    WGS84,
    /**
     * bd09
     */
    BD09,
    /**
     * mercator
     */
    MERCATOR,
    /**
     * CGS2000
     */
    CGS2000,
    /**
     * gcj02
     */
    GCJ02;
    /**
     * 转化器
     */
    static final Table<Coordinate, Coordinate, CoordinateConverter> CONVERTER_TABLE =
            ImmutableBuilder.<Coordinate, Coordinate, CoordinateConverter>newTable()
                    .put(WGS84, GCJ02, new Wgs84ToGcj02CoordinateConverter())
                    .put(WGS84, BD09, new Wgs84ToBd09CoordinateConverter())
                    .put(WGS84, MERCATOR, new Wgs84ToMercatorCoordinateConverter())
                    .put(WGS84, CGS2000, new Wgs84ToCgS2000CoordinateConverter())

                    .put(GCJ02, WGS84, new Wgs84ToGcj02CoordinateConverter())
                    .put(GCJ02, BD09, new Gcj02ToBd09CoordinateConverter())
                    .put(GCJ02, MERCATOR, new Gcj02ToMercatorCoordinateConverter())
                    .put(GCJ02, CGS2000, new Gcj02ToCgS2000CoordinateConverter())

                    .put(BD09, WGS84, new Bd09ToWgs84CoordinateConverter())
                    .put(BD09, GCJ02, new Bd09ToGcj02CoordinateConverter())
                    .put(BD09, MERCATOR, new Bd09ToMercatorCoordinateConverter())
                    .put(BD09, CGS2000, new Bd09ToCgS2000CoordinateConverter())

                    .put(MERCATOR, WGS84, new MercatorToWgs84CoordinateConverter())
                    .put(MERCATOR, GCJ02, new MercatorToGcj02CoordinateConverter())
                    .put(MERCATOR, BD09, new MercatorToBd09CoordinateConverter())
                    .put(MERCATOR, CGS2000, new MercatorToCgS2000CoordinateConverter())

                    .put(CGS2000, WGS84, new CgS2000ToWgs84CoordinateConverter())
                    .put(CGS2000, GCJ02, new CgS2000ToGcj02CoordinateConverter())
                    .put(CGS2000, BD09, new CgS2000ToBd09CoordinateConverter())
                    .put(CGS2000, MERCATOR, new CgS2000ToMercatorCoordinateConverter())
                    .build();

    /**
     * 转化
     *
     * @param coordinate 目标类型
     * @param point      参数
     * @return 结果
     */
    public final Point to(Coordinate coordinate, Point point) {
        checkNotNull(coordinate);
        checkNotNull(point);
        return (coordinate == this) ? point : convert(coordinate, point);
    }

    /**
     * 转化
     *
     * @param coordinate 坐标转化器
     * @param point      坐标
     * @return 结果
     */
    private Point convert(Coordinate coordinate, Point point) {
        CoordinateConverter converter = CONVERTER_TABLE.get(this, coordinate);
        return null == converter ? null : converter.transform(point);
    }

    /**
     * 转化
     *
     * @param coordinate 类型
     * @return 结果
     */
    public CoordinateConverter converterTo(Coordinate coordinate) {
        return new StringConverter(this, coordinate);
    }

    private static final class StringConverter
            implements Serializable, CoordinateConverter {

        private final Coordinate sourceCoordinate;
        private final Coordinate targetCoordinate;

        public StringConverter(Coordinate sourceCoordinate, Coordinate targetCoordinate) {
            this.sourceCoordinate = sourceCoordinate;
            this.targetCoordinate = targetCoordinate;
        }

        @Override
        public Point transform(Point point) {
            return sourceCoordinate.to(targetCoordinate, point);
        }
    }
}
