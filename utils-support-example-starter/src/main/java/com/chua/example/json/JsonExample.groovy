package com.chua.example.json


import com.alibaba.fastjson2.JSONPath

/**
 * @author CH
 */
public class JsonExample {

    public static void main(String[] args) {
        def jindu = JSONPath.extract(jsonValue(), '$._Info[?(@.Id in ("25323.521"))].Children[?(@.Id = "25323.521.310")].Val')
    }


    static String jsonValue (){
        return '''{
    "SN": 43,
    "SID": 2771,
    "_InstantTime": "2023-06-10 15:27:34",
    "_ItemSet": {
        "items": [
            {
                "Range": 0,
                "ReferenceLine": "",
                "enable": true,
                "sort": 0,
                "hide": false,
                "index": "2.2",
                "item": "时雨量",
                "olditem": "雨量5分钟",
                "decimal_digit": 2,
                "Modified": 0,
                "Coefficient": 1,
                "Chart": "column",
                "DisplayMethod": "差值",
                "Interval": 60,
                "Unit": "",
                "Quote": "",
                "Revise": 0,
                "tabledata": "",
                "formuladata": "",
                "SpareIndex": "",
                "val": "—",
                "XAxis0": 0,
                "b_code": "",
                "is_server_transfer": false
            },
            {
                "Range": 0,
                "ReferenceLine": "",
                "enable": true,
                "sort": 1,
                "hide": true,
                "index": "2.3",
                "item": "时雨量",
                "olditem": "时雨量",
                "decimal_digit": 2,
                "Modified": 0,
                "Coefficient": 1,
                "Chart": "column",
                "DisplayMethod": "差值",
                "Interval": 60,
                "Unit": "",
                "Quote": "",
                "Revise": 0,
                "tabledata": "",
                "formuladata": "",
                "SpareIndex": "",
                "val": "—",
                "XAxis0": 0,
                "b_code": "",
                "is_server_transfer": false
            },
            {
                "Range": 0,
                "ReferenceLine": "",
                "enable": true,
                "sort": 2,
                "hide": false,
                "index": "2.6",
                "item": "水位5",
                "olditem": "水位5分钟",
                "decimal_digit": 2,
                "Modified": 0,
                "Coefficient": 1,
                "Chart": "line",
                "DisplayMethod": "无",
                "Interval": 15,
                "Unit": "",
                "Quote": "",
                "Revise": 0,
                "tabledata": "",
                "formuladata": "",
                "SpareIndex": "",
                "val": "—",
                "XAxis0": 0,
                "b_code": "",
                "is_server_transfer": false
            },
            {
                "Range": 0,
                "ReferenceLine": "",
                "enable": true,
                "sort": 3,
                "hide": false,
                "index": "-1.1",
                "item": "库容",
                "olditem": "库容",
                "decimal_digit": 2,
                "Modified": 0,
                "Coefficient": 1,
                "Chart": "line",
                "DisplayMethod": "无",
                "Interval": 5,
                "Unit": "",
                "Quote": "2.6",
                "Revise": 1,
                "tabledata": "15.09,15.19,15.29,15.39,15.49,15.59,15.69,15.79,15.89,15.99,16.09,16.19,16.29,16.39,16.49,16.59,16.69,16.79,16.89,16.99,17.09,17.19,17.29,17.39,17.49,17.59,17.69,17.79,17.89,17.99,18.09,18.19,18.29,18.39,18.49,18.59,18.69,18.79,18.89,18.99,19.09,19.19,19.29,19.39,19.49,19.59,19.69,19.79,19.89,19.99,20.09,20.19,20.29,20.39,20.49,20.59,20.69,20.79,20.89,20.99,21.09,21.19,21.29,21.39,21.49,21.59,21.69,21.79,21.89,21.99,22.09,22.19,22.29,22.39,22.49,22.59,22.69,22.79,22.89,22.99,23.09,23.19,23.29,23.39,23.49,23.59,23.69,23.79,23.89,23.99,24.09,24.19,24.29,24.39,24.49,24.59,24.69,24.79,24.89,24.99,25.09,25.19,25.29,25.39,25.49,25.59,25.69,25.79,25.89,25.99,26.09,26.19,26.29,26.39,26.49,26.59,26.69,26.79,26.89,26.99,27.09,27.19,27.29,27.39,27.49,27.5,27.59,27.69,27.79,27.89,27.99,28.09,28.19,28.29,28.39,28.49,28.59,28.61,28.69,28.79,28.89,28.99,29.09,29.19,29.29,29.39,29.4@0.06,0.09,0.14,0.19,0.25,0.33,0.41,0.51,0.61,0.73,0.85,0.98,1.11,1.26,1.41,1.56,1.73,1.9,2.07,2.26,2.45,2.66,2.87,3.09,3.32,3.56,3.8,4.05,4.31,4.58,4.85,5.13,5.42,5.71,6.02,6.33,6.66,6.99,7.33,7.68,8.03,8.4,8.77,9.15,9.54,9.93,10.34,10.75,11.17,11.6,12.03,12.48,12.94,13.4,13.87,14.36,14.85,15.35,15.87,16.39,16.92,17.47,18.02,18.58,19.16,19.74,20.33,20.93,21.54,22.16,22.78,23.42,24.07,24.72,25.39,26.06,26.75,27.45,28.15,28.87,29.59,30.32,31.07,31.82,32.57,33.34,34.11,34.89,35.68,36.47,37.27,38.08,38.89,39.71,40.53,41.36,42.19,43.04,43.88,44.74,45.6,46.47,47.34,48.22,49.11,50,50.9,51.81,52.72,53.65,54.58,55.52,56.46,57.41,58.37,59.34,60.31,61.29,62.28,63.28,64.28,65.28,66.3,67.32,68.35,68.45,69.38,70.42,71.47,72.52,73.58,74.65,75.73,76.81,77.9,79,80.11,80.33,81.22,82.34,83.47,84.61,85.75,86.91,88.07,89.25,89.43",
                "formuladata": "",
                "SpareIndex": "",
                "val": "—",
                "XAxis0": 0,
                "b_code": "",
                "is_server_transfer": true
            },
            {
                "Range": 0,
                "ReferenceLine": "",
                "enable": true,
                "sort": 4,
                "hide": false,
                "index": "1.1",
                "item": "电压",
                "olditem": "电压",
                "decimal_digit": 2,
                "Modified": 0,
                "Coefficient": 1,
                "Chart": "line",
                "DisplayMethod": "无",
                "Interval": 15,
                "Unit": "",
                "Quote": "",
                "Revise": 0,
                "tabledata": "",
                "formuladata": "",
                "SpareIndex": "",
                "val": "—",
                "XAxis0": 0,
                "b_code": "",
                "is_server_transfer": false
            },
            {
                "Range": 0,
                "ReferenceLine": "",
                "enable": true,
                "sort": 5,
                "hide": true,
                "index": "2.8",
                "item": "日水位",
                "olditem": "日水位",
                "decimal_digit": 2,
                "Modified": 0,
                "Coefficient": 1,
                "Chart": "line",
                "DisplayMethod": "无",
                "Interval": 15,
                "Unit": "",
                "Quote": "",
                "Revise": 0,
                "tabledata": "",
                "formuladata": "",
                "SpareIndex": "",
                "val": "—",
                "XAxis0": 0,
                "b_code": "",
                "is_server_transfer": false
            },
            {
                "Range": 0,
                "ReferenceLine": "",
                "enable": true,
                "sort": 6,
                "hide": true,
                "index": "2.7",
                "item": "时水位",
                "olditem": "时水位",
                "decimal_digit": 2,
                "Modified": 0,
                "Coefficient": 1,
                "Chart": "line",
                "DisplayMethod": "无",
                "Interval": 15,
                "Unit": "",
                "Quote": "",
                "Revise": 0,
                "tabledata": "",
                "formuladata": "",
                "SpareIndex": "",
                "val": "—",
                "XAxis0": 0,
                "b_code": "",
                "is_server_transfer": false
            },
            {
                "Range": 0,
                "ReferenceLine": "",
                "enable": true,
                "sort": 7,
                "hide": true,
                "index": "2.5",
                "item": "当前水位",
                "olditem": "当前水位",
                "decimal_digit": 2,
                "Modified": 0,
                "Coefficient": 1,
                "Chart": "line",
                "DisplayMethod": "无",
                "Interval": 15,
                "Unit": "",
                "Quote": "",
                "Revise": 0,
                "tabledata": "",
                "formuladata": "",
                "SpareIndex": "",
                "val": "—",
                "XAxis0": 0,
                "b_code": "",
                "is_server_transfer": false
            },
            {
                "Range": 0,
                "ReferenceLine": "",
                "enable": true,
                "sort": 8,
                "hide": true,
                "index": "2.4",
                "item": "日雨量",
                "olditem": "日雨量",
                "decimal_digit": 2,
                "Modified": 0,
                "Coefficient": 1,
                "Chart": "line",
                "DisplayMethod": "无",
                "Interval": 5,
                "Unit": "",
                "Quote": "",
                "Revise": 0,
                "tabledata": "",
                "formuladata": "",
                "SpareIndex": "",
                "val": "—",
                "XAxis0": 0,
                "b_code": "",
                "is_server_transfer": false
            },
            {
                "Range": 0,
                "ReferenceLine": "",
                "enable": true,
                "sort": 9,
                "hide": true,
                "index": "2.1",
                "item": "雨量计数",
                "olditem": "雨量计数",
                "decimal_digit": 2,
                "Modified": 0,
                "Coefficient": 1,
                "Chart": "line",
                "DisplayMethod": "无",
                "Interval": 5,
                "Unit": "",
                "Quote": "",
                "Revise": 0,
                "tabledata": "",
                "formuladata": "",
                "SpareIndex": "",
                "val": "—",
                "XAxis0": 0,
                "b_code": "",
                "is_server_transfer": false
            },
            {
                "Range": 0,
                "ReferenceLine": "",
                "enable": true,
                "sort": 10,
                "hide": true,
                "index": "1.3",
                "item": "CSQ",
                "olditem": "CSQ",
                "decimal_digit": 2,
                "Modified": 0,
                "Coefficient": 1,
                "Chart": "line",
                "DisplayMethod": "无",
                "Interval": 15,
                "Unit": "",
                "Quote": "",
                "Revise": 0,
                "tabledata": "",
                "formuladata": "",
                "SpareIndex": "",
                "val": "—",
                "XAxis0": 0,
                "b_code": "",
                "is_server_transfer": false
            },
            {
                "Range": 0,
                "ReferenceLine": "",
                "enable": true,
                "sort": 11,
                "hide": true,
                "index": "1.2",
                "item": "环境温度",
                "olditem": "环境温度",
                "decimal_digit": 2,
                "Modified": 0,
                "Coefficient": 1,
                "Chart": "line",
                "DisplayMethod": "无",
                "Interval": 15,
                "Unit": "",
                "Quote": "",
                "Revise": 0,
                "tabledata": "",
                "formuladata": "",
                "SpareIndex": "",
                "val": "—",
                "XAxis0": 0,
                "b_code": "",
                "is_server_transfer": false
            }
        ]
    },
    "_Info": [
        {
            "Id": "25323.0",
            "GroupName": "",
            "Type": 1,
            "Children": [
                {
                    "Id": "25323.0.306",
                    "Name": "遥测终端SID编码",
                    "Val": "2771"
                },
                {
                    "Id": "25323.0.288",
                    "Name": "图片",
                    "Val": "https://www.swyaoce.com/images/2771"
                }
            ]
        },
        {
            "Id": "25323.521",
            "GroupName": "基础概貌",
            "Type": 1,
            "Children": [
                {
                    "Id": "25323.521.307",
                    "Name": "终端名称",
                    "Val": "南岙水库"
                },
                {
                    "Id": "25323.521.433",
                    "Name": "当前状态",
                    "Val": "启用"
                },
                {
                    "Id": "25323.521.308",
                    "Name": "行政区划",
                    "Val": "舟山市普陀区"
                },
                {
                    "Id": "25323.521.309",
                    "Name": "地址",
                    "Val": "东港街道南岙村"
                },
                {
                    "Id": "25323.521.310",
                    "Name": "经度",
                    "Val": "122.29607105255127"
                },
                {
                    "Id": "25323.521.311",
                    "Name": "纬度",
                    "Val": "29.98297691345215"
                },
                {
                    "Id": "25323.521.314",
                    "Name": "备注",
                    "Val": "水尺顶32米对应85高程29.41米"
                },
                {
                    "Id": "25323.521.917",
                    "Name": "业务测站编码",
                    "Val": ""
                }
            ]
        },
        {
            "Id": "25323.524",
            "GroupName": "监测要素",
            "Type": 1,
            "Children": [
                {
                    "Id": "25323.524.312",
                    "Name": "遥测项目",
                    "Val": "时雨量,时雨量,水位5,库容,电压,日水位,时水位,当前水位,日雨量,雨量计数,CSQ,环境温度"
                }
            ]
        },
        {
            "Id": "25323.526",
            "GroupName": "测站基础",
            "Type": 1,
            "Children": [
                {
                    "Id": "25323.526.283",
                    "Name": "流域名称",
                    "Val": "浙闽台河流"
                },
                {
                    "Id": "25323.526.285",
                    "Name": "水系名称",
                    "Val": "浙东北沿海诸河"
                },
                {
                    "Id": "25323.526.284",
                    "Name": "河流名称",
                    "Val": ""
                },
                {
                    "Id": "25323.526.281",
                    "Name": "站点类型",
                    "Val": "水库站"
                },
                {
                    "Id": "25323.526.919",
                    "Name": "集雨面积",
                    "Val": ""
                }
            ]
        },
        {
            "Id": "25323.525",
            "GroupName": "通信设置",
            "Type": 1,
            "Children": [
                {
                    "Id": "25323.525.1135",
                    "Name": "厂商联系人",
                    "Val": ""
                },
                {
                    "Id": "25323.525.1136",
                    "Name": "联系电话",
                    "Val": ""
                },
                {
                    "Id": "25323.525.1137",
                    "Name": "注册单位",
                    "Val": ""
                },
                {
                    "Id": "25323.525.1138",
                    "Name": "注册时间",
                    "Val": ""
                },
                {
                    "Id": "25323.525.1139",
                    "Name": "有效期",
                    "Val": ""
                }
            ]
        },
        {
            "Id": "25323.522",
            "GroupName": "管理信息",
            "Type": 1,
            "Children": [
                {
                    "Id": "25323.522.292",
                    "Name": "责任单位",
                    "Val": "普陀区水文站"
                },
                {
                    "Id": "25323.522.293",
                    "Name": "责任人",
                    "Val": "邵盼攀"
                },
                {
                    "Id": "25323.522.282",
                    "Name": "责任人手机",
                    "Val": "13454053520"
                },
                {
                    "Id": "25323.522.289",
                    "Name": "责任管理员",
                    "Val": "王俊杰"
                },
                {
                    "Id": "25323.522.287",
                    "Name": "责任管理员手机",
                    "Val": "13665817772"
                },
                {
                    "Id": "25323.522.918",
                    "Name": "站点所属建设项目",
                    "Val": ""
                }
            ]
        },
        {
            "Id": "25323.562",
            "GroupName": "测站信息",
            "Type": 1,
            "Children": [
                {
                    "Id": "25323.562.929",
                    "Name": "测站编码",
                    "Val": "70700510"
                },
                {
                    "Id": "25323.562.930",
                    "Name": "测站名称",
                    "Val": "南岙水库"
                },
                {
                    "Id": "25323.562.931",
                    "Name": "经度",
                    "Val": "122.297033"
                },
                {
                    "Id": "25323.562.932",
                    "Name": "纬度",
                    "Val": "29.985576"
                },
                {
                    "Id": "25323.562.1030",
                    "Name": "监测项目",
                    "Val": "雨量,水位"
                }
            ]
        },
        {
            "Id": "25323.62",
            "GroupName": "雨量",
            "Type": 2,
            "Children": [
                {
                    "Id": "25323.62.318",
                    "Name": "雨量传感器型号",
                    "Val": "JDZ05-1"
                },
                {
                    "Id": "25323.62.331",
                    "Name": "安装时间",
                    "Val": ""
                }
            ]
        },
        {
            "Id": "25323.64",
            "GroupName": "水位",
            "Type": 2,
            "Children": [
                {
                    "Id": "25323.64.322",
                    "Name": "水位传感器型号",
                    "Val": "WFH-2"
                },
                {
                    "Id": "25323.64.323",
                    "Name": "站点类型",
                    "Val": "小(二)水库"
                },
                {
                    "Id": "25323.64.324",
                    "Name": "是否斜井",
                    "Val": "是"
                },
                {
                    "Id": "25323.64.325",
                    "Name": "最高水位",
                    "Val": "29.61"
                },
                {
                    "Id": "25323.64.326",
                    "Name": "最低水位",
                    "Val": "0"
                },
                {
                    "Id": "25323.64.327",
                    "Name": "警戒水位",
                    "Val": "28.61"
                },
                {
                    "Id": "25323.64.333",
                    "Name": "安装时间",
                    "Val": ""
                },
                {
                    "Id": "25323.64.321",
                    "Name": "测站基面",
                    "Val": "国家85高程"
                }
            ]
        }
    ]
}'''
}
}
