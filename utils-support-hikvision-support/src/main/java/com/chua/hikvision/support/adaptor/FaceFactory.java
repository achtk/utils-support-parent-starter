package com.chua.hikvision.support.adaptor;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.arcsoft.face.FaceInfo;
import com.boren.school.attendance.support.HikvisionClient;
import com.boren.school.attendance.support.query.FaceInfoForUpdate;
import com.boren.school.attendance.support.query.FaceSearch;
import com.boren.school.attendance.support.result.FaceGroupAdditionResult;
import com.boren.school.attendance.support.result.FaceGroupSearchResult;
import com.boren.school.attendance.support.result.FaceSearchResult;
import com.boren.school.attendance.support.result.HikResult;
import com.chua.common.support.bean.BeanMap;

import java.util.Collections;
import java.util.List;

import static com.boren.school.attendance.support.HikvisionEnum.*;

/**
 * 人臉适配器
 * @author CH
 */
public class FaceFactory {

    private final HikvisionClient hikvisionClient;

    public FaceFactory(HikvisionClient hikvisionClient) {
        this.hikvisionClient = hikvisionClient;
    }

    /**
     * 一次性添加一个人脸分组，返回结果为添加成功的人脸分组。
     * @param name 人脸分组的名称，1~32个字符；不能包含 ’ / \ : * ? " < >
     * @param description 分组的描述，1~128个字符
     * @return FaceAdditionResult
     */

    public FaceGroupAdditionResult additionGroup(String name, String description) {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("description", description);
        try {
            return hikvisionClient.executePost(FACE_GROUP_ADDITION, json, FaceGroupAdditionResult.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new FaceGroupAdditionResult();
        }
    }

    /**
     * 删除人脸分组
     * @param indexCodes 根据分组的唯一标识
     * @return FaceAdditionResult
     */

    public HikResult deleteGroup(String[] indexCodes) {
        JSONObject json = new JSONObject();
        json.put("indexCodes", indexCodes);
        try {
            return JSON.parseObject(hikvisionClient.executePost(FACE_GROUP_DELETION, json, String.class), HikResult.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new HikResult();
        }
    }
    /**
     * 更新人脸分组
     * @param indexCode 根据分组的唯一标识
     * @param name 人脸分组的名称，1~32个字符；不能包含 ’ / \ : * ? " < >
     * @param description 分组的描述，1~128个字符
     * @return FaceAdditionResult
     */

    public HikResult updateGroup(String indexCode, String name, String description) {
        JSONObject json = new JSONObject();
        json.put("indexCode", indexCode);
        json.put("name", name);
        json.put("description", description);
        try {
            return JSON.parseObject(hikvisionClient.executePost(FACE_GROUP_UPDATE, json, String.class), HikResult.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new HikResult();
        }
    }
    /**
     * a)根据查询条件，查询人脸分组集合。
     * @param indexCodes 根据分组的唯一标识
     * @param name 人脸分组名称模糊查询
     * @return FaceAdditionResult
     */

    public List<FaceGroupSearchResult> searchGroup(String[] indexCodes, String name) {
        JSONObject json = new JSONObject();
        json.put("indexCodes", indexCodes);
        json.put("name", name);
        try {
            return JSON.parseArray(hikvisionClient.executePost(FACE_GROUP_SEARCH, json, String.class), FaceGroupSearchResult.class);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    //人脸信息管理==================================================================================================
    /**
     * 单个添加人脸
     * a)一次性添加一张人脸，返回结果为添加成功的人脸。
     * b)添加的人脸图片，目前支持URL方式和二进制数据方式。URL方式时，人脸监控应用服务会通过HTTP协议的GET方式下载图片，校验图片，最后重新上传图片；二进制数据是指图片的字节流经过Base64编码后得到的字符串。
     * c)该URL能通过HTTP协议的GET方式能下载获取到即可，若包含认证，由调用方在URL中加上认证信息，保证URL能成功访问。
     * d)人脸监控应用服务会根据图片存储位置配置，将图片上传到图片存储服务器中，返回的URL为图片存储服务器上的相对地址。
     * e)图片大小和格式均有要求，上传大小在10KB到200KB间的图片，上传JPG格式的图片。
     * f)若添加的人脸对应的人脸分组已经配置有识别计划，则新添加的人脸会被一并下发到设备上。
     * g)该接口依赖于图片存储位置已配置完，请确保平台已经配置有人脸图片存储位置，否则添加必定失败。
     * @param faceInfo 人脸信息
     * @return FaceAdditionResult
     */

    public List<FaceGroupSearchResult> additionFace(FaceInfo faceInfo) {
        JSONObject json = new JSONObject();
        json.putAll(BeanMap.of(faceInfo));
        try {
            return JSON.parseArray(hikvisionClient.executePost(FACE_ADDITION, json, String.class), FaceGroupSearchResult.class);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    /**
     *a)根据人脸删除条件删除一批人脸，返回的data为布尔类型，true代表操作成功，false代表操作失败。
     * b)若删除的人脸对应的人脸分组已经配置有识别计划，则删除的人脸会被一并从设备上删除。
     * c)该接口是从指定分组内删除指定人脸。
     * d)人脸分组唯一标识和人脸的唯一标识集合均不能为空。
     * e)一次性最多从一个分组内删除1000个人脸。
     * @param indexCodes 人脸的唯一标识集合，可从按条件批量查询人脸接口返回报文中的indexCode字段
     * @param faceGroupIndexCode 人脸分组的唯一标识，可从按条件查询人脸分组接口返回报文中的indexCode字段
     * @return FaceAdditionResult
     */

    public HikResult deleteFace(String[] indexCodes, String faceGroupIndexCode) {
        JSONObject json = new JSONObject();
        json.put("faceGroupIndexCode", faceGroupIndexCode);
        json.put("indexCodes", indexCodes);
        try {
            return JSON.parseObject(hikvisionClient.executePost(FACE_DELETE, json, String.class), HikResult.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new HikResult();
        }
    }
    /**
     *a)修改单张人脸信息,返回的data为布尔类型，true代表操作成功，false代表操作失败。
     * b)若修改的人脸对应的人脸分组已经配置有识别计划，则修改后的人脸会被重新下发到设备上。
     * c)修改后的字段信息以修改时传入的字段为准，若不传字段或传入null字段，则该字段会被置为null。
     * d)如果要修改人脸信息，则需要传递faceInfo对象。
     * e)如果要修改人脸图片，则需要传递facePic对象。
     * @param faceInfoForUpdate 人脸信息
     * @return FaceAdditionResult
     */

    public HikResult updateFace(FaceInfoForUpdate faceInfoForUpdate) {
        JSONObject json = new JSONObject();
        json.putAll(BeanMap.create(faceInfoForUpdate));
        try {
            return JSON.parseObject(hikvisionClient.executePost(FACE_UPDATE, json, String.class), HikResult.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new HikResult();
        }
    }
    /**
     *a)修改单张人脸信息,返回的data为布尔类型，true代表操作成功，false代表操作失败。
     * b)若修改的人脸对应的人脸分组已经配置有识别计划，则修改后的人脸会被重新下发到设备上。
     * c)修改后的字段信息以修改时传入的字段为准，若不传字段或传入null字段，则该字段会被置为null。
     * d)如果要修改人脸信息，则需要传递faceInfo对象。
     * e)如果要修改人脸图片，则需要传递facePic对象。
     * @param faceSearch 人脸信息
     * @return FaceAdditionResult
     */

    public FaceSearchResult searchFace(FaceSearch faceSearch) {
        JSONObject json = new JSONObject();
        json.putAll(BeanMap.create(faceSearch));
        try {
            return JSON.parseObject(hikvisionClient.executePost(FACE_SEARCH, json, String.class), FaceSearchResult.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new FaceSearchResult();
        }
    }


}
