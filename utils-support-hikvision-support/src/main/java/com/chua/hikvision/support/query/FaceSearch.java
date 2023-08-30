package com.chua.hikvision.support.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author CH
 */
@NoArgsConstructor
@Data
public class FaceSearch {

    /**
     * 人脸的证件号码模糊查询
     */

    @JsonProperty("certificateNum")
    private String certificateNum;
    /**
     * 人脸的证件类型搜索
     */
    @JsonProperty("certificateType")
    private String certificateType;
    /**
     * 根据人脸所属的分组搜索该分组下符合条件的人脸，可从按条件查询人脸分组接口返回报文中的indexCode字段
     */
    @JsonProperty("faceGroupIndexCode")
    private String faceGroupIndexCode;
    /**
     * 通过人脸的唯一标识集合查询指定的人脸集合
     */
    @JsonProperty("indexCodes")
    private List<String> indexCodes;
    /**
     * 人脸名称模糊查询
     */
    @JsonProperty("name")
    private String name;
    /**
     * 分页查询条件，页码，为空时，等价于1，页码不能小于1或大于1000
     */
    @JsonProperty("pageNo")
    private Integer pageNo = 1;
    /**
     * 分页查询条件，页尺，为空时，等价于1000，页尺不能小于1或大于1000
     */
    @JsonProperty("pageSize")
    private Integer pageSize;
    /**
     *性别搜索,1代表男性、2代表女性、UNKNOWN代表未知
     */
    @JsonProperty("sex")
    private String sex;
}
