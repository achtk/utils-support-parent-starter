package com.chua.hikvision.support.query;

import lombok.Builder;
import lombok.Data;

/**
 * 门禁
 * @author CH
 */
@Data
@Builder
public class DoorQuery {
    /**
     * 当前页码（pageNo>0）
     */
    int pageNo;
    /**
     * 每页展示数目（0<pageSize<=1000）
     */
    int pageSize;
    /**
     * 开始时间（事件开始时间，采用 ISO8601
     * 时间格式，与 endTime 配对使用，不能
     * 单独使用
     */
    String startTime;
    /**
     * 结束时间（事件结束时间，采用 ISO8601
     * 时间格式，最大长度 32 个字符，与
     * startTime 配对使用，不能单独使用，时
     * 间范围最大不能超过 3 个月）
     */
    String endTime;
    /**
     * 入库开始时间，采用 ISO8601 时间格式，
     * 与 receiveEndTime 配对使用，不能单独
     * 使用，时间范围最大不能超过 3 个月
     */
    String receiveStartTime;
    /**
     * 入库结束时间，采用 ISO8601 时间格式，
     * 最 大 长 度 32 个 字 符 ， 与
     * receiveStartTime 配对使用，不能单独
     * 使用，时间范围最大不能超过 3 个月
     */
    String receiveEndTime;

    /**
     * 门禁点唯一标识数组，最大支持 10 个门
     * 禁点，[查询门禁点列表 v2]@[软件产品
     * -综合安防管理平台-API 列表-一卡通应
     * 用服务-门禁管理#查询门禁点列表 v2]
     * 接口获取返回报文中的 doorIndexCode
     * 字段(非必填)
     */
    String[] doorIndexCodes;
    /**
     * 门禁点名称，支持模糊查询，从[查询门
     * 禁点列表 v2]@[软件产品-综合安防管理
     * 平台-API 列表-一卡通应用服务-门禁管
     * 理#查询门禁点列表 v2]接口获取返回报
     * 文中的 name 字段 (非必填)
     */
    String doorName;
    /**
     * 事件类型，参考[附录 D2.1 门禁事
     * 件]@[软件产品-综合安防管理平台-附
     * 录-附录 D 事件列表-附录 D2 一卡通事
     * 件-附录 D2.1 门禁事件](非必填)
     */
    String[] eventTypes;
    /**
     * 人员姓名(支持中英文字符，不能包含 '
     * / \ : * ? " < > |，支持模糊搜索,长
     * 度不超过 32 位)(非必填)
     */

    String personName;
    /**
     * 排序字段（支持 personName、doorName、
     * eventTime 填写排序的字段名称）(非必填)
     */
    String sort;
    /**
     * 升/降序（指定排序字段是使用升序
     * （asc）还是降序（desc）(非必填)
     */
    String order;
}
