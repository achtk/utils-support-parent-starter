package com.chua.hikvision.support;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 海康
 * @author CH
 */
@AllArgsConstructor
@Getter
public enum HikvisionEnum {
    /**
     * 获取停车库列表
     * <p>
     *     根据停车场唯一表示集合获取停车列表信息
     * </p>
     */
    CAR_CARPORT("/api/resource/v1/park/parkList"),
    /**
     * 查询停车账单(根据停车库和车牌号)
     * <p>
     * </p>
     */
    CAR_CARPORT_BILL("/api/resource/v1/pay/quickPreBill"),
    /**
     * 获取监控点预览取流URLv2
     * <p>
     * 1.平台正常运行；平台已经添加过设备和监控点信息。
     * 2.平台需要安装mgc取流服务。
     * 3.通过openAPI获取到监控点数据，依据自身业务开发监控点导航界面。
     * 4.根据监控点编号调用本接口获取预览取流URL，协议类型包括：hik、rtsp、rtmp、hls、ws。
     * 5.通过开放平台的开发包进行实时预览或者使用标准的GUI播放工具进行实时预览。
     * 6.为保证数据的安全性，取流URL设有有效时间，有效时间为5分钟。
     * </p>
     */
    CAMERAS_PREVIEW_URLS("/api/video/v2/cameras/previewURLs"),
    /**
     * 分页获取监控点资源
     * <p>
     * 获取监控点列表接口可用来全量同步监控点信息，返回结果分页展示。
     * </p>
     */
    CAMERAS_PAGE_SEARCH("/api/resource/v1/cameras"),

    /**
     * 获取监控点在线状态
     * <p>
     * 获取监控点在线状态，返回结果分页展示。
     * </p>
     */
    CAMERAS_STATUS("/api/nms/v1/online/camera/get"),
    /**
     * 根据编号获取监控点详细信息
     * <p>
     * 获取单个监控点信息是指根据监控点唯一标识来获取指定的监控点信息。
     * </p>
     */
    CAMERAS_INDEX_CODE("/api/resource/v1/cameras/indexCode"),
    /**
     * 查询监控点列表v2
     * <p>
     * 根据条件查询目录下有权限的监控点列表。
     * 当返回字段对应的值为空时，该字段不返回。
     * </p>
     */
    CAMERAS_SEARCH("/api/resource/v2/camera/search"),
    /*********************************************************/
    /**
     * 分页获取监控点资源
     * <p>
     *     获取监控点列表接口可用来全量同步监控点信息，返回结果分页展示。
     * </p>
     */
    REGION_NODES("/api/irds/v2/region/nodesByParams"),
    /**
     * 获取根区域信息
     */
    REGION_ROOT("/api/resource/v1/regions/root"),
    /**
     * 分页获取区域列表
     * <p>
     *     获取区域列表接口可用来全量同步区域信息，返回结果分页展示。
     * </p>
     */
    REGIONS("/api/resource/v1/regions"),
    /**
     * 根据区域编号获取下一级区域列表v2
     * <p>
     *     根据用户请求的资源类型和资源权限获取父区域的下级区域列表，主要用于逐层获取父区域的下级区域信息，例如监控点预览业务的区域树的逐层获取。下级区域只包括直接下级子区域。
     * 注：
     * 查询区域管理权限（resourceType为region），若父区域的子区域无权限、但是其孙区域有权限时，会返回该无权限的子区域，但是该区域的available标记为false（表示无权限）
     * </p>
     */
    REGIONS_SUB("/api/resource/v2/regions/subRegions"),
    /**
     * 根据编号获取区域详细信息
     * <p>
     *     根据区域编号查询区域详细信息及总条数，主要用于区域详细信息展示。
     * </p>
     */
    REGIONS_INFO("/api/resource/v1/region/regionCatalog/regionInfo"),
    ;

    private String url;

}
