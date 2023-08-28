package com.chua.common.support.file;

import com.chua.common.support.geo.Point;

import java.io.OutputStream;
import java.util.Date;

/**
 * exif
 *
 * @author CH
 */
public interface ExifFile {
    /**
     * 删除exif
     *
     * @param outputStream 输出
     */
    void removeExif(OutputStream outputStream);

    /**
     * 媒体后缀
     *
     * @return 媒体后缀
     */
    String getExtension();

    /**
     * 获取媒体类型
     *
     * @return 媒体类型
     */
    String getMineType();

    /**
     * 获取文件类型
     *
     * @return 文件类型
     */
    String getLongFileType();

    /**
     * 获取文件类型
     *
     * @return 文件类型
     */
    String getFileType();

    /**
     * 获取原始日期
     *
     * @return 日期
     */
    Date getOriginalDate();

    /**
     * 获取数字化日期
     *
     * @return 日期
     */
    Date getDigitizedDate();

    /**
     * 光圈值
     *
     * @return 光圈值
     */
    String getNumber();

    /**
     * 曝光时间
     *
     * @return 曝光时间
     */
    String getExposureTime();

    /**
     * ISO速度
     *
     * @return ISO速度
     */
    String getIsoEquivalent();

    /**
     * 焦距
     *
     * @return 焦距
     */
    String getFocalLength();

    /**
     * 最大光圈
     *
     * @return 最大光圈
     */
    Double getMaxAperture();

    /**
     * 宽
     *
     * @return 宽
     */
    String getExifImageWidth();

    /**
     * 高
     *
     * @return 高
     */
    String getExifImageHeight();

    /**
     * 照相机制造商
     *
     * @return 照相机制造商
     */
    String getMake();

    /**
     * 照相机型号
     *
     * @return 照相机型号
     */
    String getModel();

    /**
     * x方向分辨路
     *
     * @return x方向分辨路
     */
    String getXzAxisResolution();

    /**
     * y方向分辨路
     *
     * @return y方向分辨路
     */
    String getYzAxisResolution();

    /**
     * 显示固件Firmware版本(图片详细信息的程序名称)
     *
     * @return 显示固件Firmware版本(图片详细信息的程序名称)
     */
    String getSoftware();

    /**
     * 35mm焦距
     *
     * @return 35mm焦距
     */
    String get35MmFilmEquivFocalLength();

    /**
     * 孔径(图片分辨率单位)
     *
     * @return 孔径(图片分辨率单位)
     */
    String getAperture();

    /**
     * 应用程序记录
     *
     * @return 应用程序记录
     */
    String getApplicationNotes();

    /**
     * 作者
     *
     * @return 作者
     */
    String getArtist();

    /**
     * TAG_BODY_SERIAL_NUMBER
     *
     * @return TAG_BODY_SERIAL_NUMBER
     */
    String getTagBodySerialNumber();

    /**
     * 分辨率单位
     *
     * @return 分辨率单位
     */
    String getResolutionUnit();

    /**
     * 曝光补偿
     *
     * @return 曝光补偿
     */
    String getExposureBias();

    /**
     * 色域、色彩空间
     *
     * @return 色域、色彩空间
     */
    String getColorSpace();

    /**
     * 色相系数
     *
     * @return 色相系数
     */
    String getYcbcrCoefficients();

    /**
     * 色相定位
     *
     * @return 色相定位
     */
    String getYcbcrPositioning();

    /**
     * 色相抽样
     *
     * @return 色相抽样
     */
    String getYcbcrSubsampling();

    /**
     * 版本号
     *
     * @return 版本号
     */
    String getExifVersion();

    /**
     * 定位
     *
     * @return 定位
     */
    Point getGeoLocation();
}
