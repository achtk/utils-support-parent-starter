package com.chua.example.crawler

import com.alibaba.fastjson2.JSON

/**
 *
 * @author CH
 */
class AreaExample {
    static def area = """
[
    {
      "id": 46,
      "name": "镇海区",
      "code": "330211000000",
      "pcode": "330200000000",
      "mark": null
    },
    {
      "id": 565,
      "name": "招宝山街道",
      "code": "330211001000",
      "pcode": "330211000000",
      "mark": null
    },
    {
      "id": 566,
      "name": "蛟川街道",
      "code": "330211002000",
      "pcode": "330211000000",
      "mark": null
    },
    {
      "id": 567,
      "name": "骆驼街道",
      "code": "330211003000",
      "pcode": "330211000000",
      "mark": null
    },
    {
      "id": 568,
      "name": "庄市街道",
      "code": "330211004000",
      "pcode": "330211000000",
      "mark": null
    },
    {
      "id": 569,
      "name": "贵驷街道",
      "code": "330211005000",
      "pcode": "330211000000",
      "mark": null
    },
    {
      "id": 570,
      "name": "澥浦镇",
      "code": "330211100000",
      "pcode": "330211000000",
      "mark": null
    },
    {
      "id": 571,
      "name": "九龙湖镇",
      "code": "330211101000",
      "pcode": "330211000000",
      "mark": null
    },
    {
      "id": 12279,
      "name": "朝阳村委会",
      "code": "330211003218",
      "pcode": "330211003000",
      "mark": null
    },
    {
      "id": 12280,
      "name": "中街社区",
      "code": "330211003001",
      "pcode": "330211003000",
      "mark": null
    },
    {
      "id": 12281,
      "name": "团桥村委会",
      "code": "330211003210",
      "pcode": "330211003000",
      "mark": null
    },
    {
      "id": 12282,
      "name": "南一社区",
      "code": "330211003002",
      "pcode": "330211003000",
      "mark": null
    },
    {
      "id": 12283,
      "name": "箭港湖社区",
      "code": "330211003007",
      "pcode": "330211003000",
      "mark": null
    },
    {
      "id": 12284,
      "name": "骆兴村委会",
      "code": "330211003212",
      "pcode": "330211003000",
      "mark": null
    },
    {
      "id": 12285,
      "name": "新晨社区",
      "code": "330211003012",
      "pcode": "330211003000",
      "mark": null
    },
    {
      "id": 12286,
      "name": "金东社区",
      "code": "330211003013",
      "pcode": "330211003000",
      "mark": null
    },
    {
      "id": 12287,
      "name": "敬德村委会",
      "code": "330211003214",
      "pcode": "330211003000",
      "mark": null
    },
    {
      "id": 12288,
      "name": "静远社区",
      "code": "330211003005",
      "pcode": "330211003000",
      "mark": null
    },
    {
      "id": 12289,
      "name": "莲晴社区",
      "code": "330211003006",
      "pcode": "330211003000",
      "mark": null
    },
    {
      "id": 12290,
      "name": "余三村委会",
      "code": "330211003206",
      "pcode": "330211003000",
      "mark": null
    },
    {
      "id": 12291,
      "name": "尚志村委会",
      "code": "330211003211",
      "pcode": "330211003000",
      "mark": null
    },
    {
      "id": 12292,
      "name": "董家畈村委会",
      "code": "330211003208",
      "pcode": "330211003000",
      "mark": null
    },
    {
      "id": 12293,
      "name": "金华社区",
      "code": "330211003011",
      "pcode": "330211003000",
      "mark": null
    },
    {
      "id": 12294,
      "name": "桕墅方社区",
      "code": "330211003010",
      "pcode": "330211003000",
      "mark": null
    },
    {
      "id": 12295,
      "name": "骆驼村委会",
      "code": "330211003203",
      "pcode": "330211003000",
      "mark": null
    },
    {
      "id": 12296,
      "name": "盛家社区",
      "code": "330211003003",
      "pcode": "330211003000",
      "mark": null
    },
    {
      "id": 12297,
      "name": "清水湖村委会",
      "code": "330211003213",
      "pcode": "330211003000",
      "mark": null
    },
    {
      "id": 12298,
      "name": "金邑社区",
      "code": "330211003008",
      "pcode": "330211003000",
      "mark": null
    },
    {
      "id": 12299,
      "name": "勤勇村委会",
      "code": "330211004202",
      "pcode": "330211004000",
      "mark": null
    },
    {
      "id": 12300,
      "name": "庄一社区",
      "code": "330211004002",
      "pcode": "330211004000",
      "mark": null
    },
    {
      "id": 12301,
      "name": "兴庄路社区",
      "code": "330211004003",
      "pcode": "330211004000",
      "mark": null
    },
    {
      "id": 12302,
      "name": "永旺村委会",
      "code": "330211004204",
      "pcode": "330211004000",
      "mark": null
    },
    {
      "id": 12303,
      "name": "汉郡社区",
      "code": "330211004009",
      "pcode": "330211004000",
      "mark": null
    },
    {
      "id": 12304,
      "name": "陈倪路社区",
      "code": "330211004004",
      "pcode": "330211004000",
      "mark": null
    },
    {
      "id": 12305,
      "name": "万市徐村委会",
      "code": "330211004209",
      "pcode": "330211004000",
      "mark": null
    },
    {
      "id": 12306,
      "name": "联兴社区",
      "code": "330211004011",
      "pcode": "330211004000",
      "mark": null
    },
    {
      "id": 12307,
      "name": "庄市社区",
      "code": "330211004001",
      "pcode": "330211004000",
      "mark": null
    },
    {
      "id": 12308,
      "name": "同心湖社区",
      "code": "330211004006",
      "pcode": "330211004000",
      "mark": null
    },
    {
      "id": 12309,
      "name": "汉塘村委会",
      "code": "330211004207",
      "pcode": "330211004000",
      "mark": null
    },
    {
      "id": 12310,
      "name": "中兴社区",
      "code": "330211004007",
      "pcode": "330211004000",
      "mark": null
    },
    {
      "id": 12311,
      "name": "锦绣社区",
      "code": "330211004008",
      "pcode": "330211004000",
      "mark": null
    },
    {
      "id": 12312,
      "name": "钟包村委会",
      "code": "330211004208",
      "pcode": "330211004000",
      "mark": null
    },
    {
      "id": 12313,
      "name": "光明村委会",
      "code": "330211004205",
      "pcode": "330211004000",
      "mark": null
    },
    {
      "id": 12314,
      "name": "高教社区",
      "code": "330211004005",
      "pcode": "330211004000",
      "mark": null
    },
    {
      "id": 12315,
      "name": "石塘下社区",
      "code": "330211002016",
      "pcode": "330211002000",
      "mark": null
    },
    {
      "id": 12316,
      "name": "镇电社区",
      "code": "330211002008",
      "pcode": "330211002000",
      "mark": null
    },
    {
      "id": 12317,
      "name": "后施社区",
      "code": "330211002009",
      "pcode": "330211002000",
      "mark": null
    },
    {
      "id": 12318,
      "name": "中一社区",
      "code": "330211002017",
      "pcode": "330211002000",
      "mark": null
    },
    {
      "id": 12319,
      "name": "镇海炼化围垦区社区",
      "code": "330211002400",
      "pcode": "330211002000",
      "mark": null
    },
    {
      "id": 12320,
      "name": "棉丰村委会",
      "code": "330211002209",
      "pcode": "330211002000",
      "mark": null
    },
    {
      "id": 12321,
      "name": "石化三建社区",
      "code": "330211002005",
      "pcode": "330211002000",
      "mark": null
    },
    {
      "id": 12322,
      "name": "青枫社区",
      "code": "330211002018",
      "pcode": "330211002000",
      "mark": null
    },
    {
      "id": 12323,
      "name": "清水浦村委会",
      "code": "330211002210",
      "pcode": "330211002000",
      "mark": null
    },
    {
      "id": 12324,
      "name": "炼化社区",
      "code": "330211002010",
      "pcode": "330211002000",
      "mark": null
    },
    {
      "id": 12325,
      "name": "东信路社区",
      "code": "330211002015",
      "pcode": "330211002000",
      "mark": null
    },
    {
      "id": 12326,
      "name": "俞范村委会",
      "code": "330211002212",
      "pcode": "330211002000",
      "mark": null
    },
    {
      "id": 12327,
      "name": "俞范社区",
      "code": "330211002012",
      "pcode": "330211002000",
      "mark": null
    },
    {
      "id": 12328,
      "name": "陈家村委会",
      "code": "330211002213",
      "pcode": "330211002000",
      "mark": null
    },
    {
      "id": 12329,
      "name": "五里牌村委会",
      "code": "330211002205",
      "pcode": "330211002000",
      "mark": null
    },
    {
      "id": 12330,
      "name": "沿江村委会",
      "code": "330211002204",
      "pcode": "330211002000",
      "mark": null
    },
    {
      "id": 12331,
      "name": "银凤社区",
      "code": "330211002013",
      "pcode": "330211002000",
      "mark": null
    },
    {
      "id": 12332,
      "name": "南洪村委会",
      "code": "330211002214",
      "pcode": "330211002000",
      "mark": null
    },
    {
      "id": 12333,
      "name": "渡驾桥村委会",
      "code": "330211002206",
      "pcode": "330211002000",
      "mark": null
    },
    {
      "id": 12334,
      "name": "五里牌社区",
      "code": "330211002001",
      "pcode": "330211002000",
      "mark": null
    },
    {
      "id": 12335,
      "name": "虹桥社区",
      "code": "330211002014",
      "pcode": "330211002000",
      "mark": null
    },
    {
      "id": 12336,
      "name": "迎周村委会",
      "code": "330211002211",
      "pcode": "330211002000",
      "mark": null
    },
    {
      "id": 12337,
      "name": "临江社区",
      "code": "330211002011",
      "pcode": "330211002000",
      "mark": null
    },
    {
      "id": 12338,
      "name": "民联村委会",
      "code": "330211005204",
      "pcode": "330211005000",
      "mark": null
    },
    {
      "id": 12339,
      "name": "沙河村委会",
      "code": "330211005205",
      "pcode": "330211005000",
      "mark": null
    },
    {
      "id": 12340,
      "name": "东钱村委会",
      "code": "330211005206",
      "pcode": "330211005000",
      "mark": null
    },
    {
      "id": 12341,
      "name": "妙胜寺村委会",
      "code": "330211005203",
      "pcode": "330211005000",
      "mark": null
    },
    {
      "id": 12342,
      "name": "贵驷村委会",
      "code": "330211005200",
      "pcode": "330211005000",
      "mark": null
    },
    {
      "id": 12343,
      "name": "里洞桥村委会",
      "code": "330211005201",
      "pcode": "330211005000",
      "mark": null
    },
    {
      "id": 12344,
      "name": "贵驷社区",
      "code": "330211005001",
      "pcode": "330211005000",
      "mark": null
    },
    {
      "id": 12345,
      "name": "兴丰村委会",
      "code": "330211005202",
      "pcode": "330211005000",
      "mark": null
    },
    {
      "id": 12346,
      "name": "东桂社区",
      "code": "330211005002",
      "pcode": "330211005000",
      "mark": null
    },
    {
      "id": 12347,
      "name": "车站路社区",
      "code": "330211001006",
      "pcode": "330211001000",
      "mark": null
    },
    {
      "id": 12348,
      "name": "西门社区",
      "code": "330211001007",
      "pcode": "330211001000",
      "mark": null
    },
    {
      "id": 12349,
      "name": "后海塘港口物流园区社区",
      "code": "330211001400",
      "pcode": "330211001000",
      "mark": null
    },
    {
      "id": 12350,
      "name": "白龙社区",
      "code": "330211001008",
      "pcode": "330211001000",
      "mark": null
    },
    {
      "id": 12351,
      "name": "海港社区",
      "code": "330211001013",
      "pcode": "330211001000",
      "mark": null
    },
    {
      "id": 12352,
      "name": "顺隆社区",
      "code": "330211001005",
      "pcode": "330211001000",
      "mark": null
    },
    {
      "id": 12353,
      "name": "城东社区",
      "code": "330211001001",
      "pcode": "330211001000",
      "mark": null
    },
    {
      "id": 12354,
      "name": "胜利路社区",
      "code": "330211001002",
      "pcode": "330211001000",
      "mark": null
    },
    {
      "id": 12355,
      "name": "总浦桥社区",
      "code": "330211001003",
      "pcode": "330211001000",
      "mark": null
    },
    {
      "id": 12356,
      "name": "张监碶社区",
      "code": "330211001012",
      "pcode": "330211001000",
      "mark": null
    },
    {
      "id": 12357,
      "name": "后大街社区",
      "code": "330211001004",
      "pcode": "330211001000",
      "mark": null
    },
    {
      "id": 12358,
      "name": "西河村委会",
      "code": "330211101205",
      "pcode": "330211101000",
      "mark": null
    },
    {
      "id": 12359,
      "name": "长石村委会",
      "code": "330211101206",
      "pcode": "330211101000",
      "mark": null
    },
    {
      "id": 12360,
      "name": "汶溪村委会",
      "code": "330211101208",
      "pcode": "330211101000",
      "mark": null
    },
    {
      "id": 12361,
      "name": "河源社区",
      "code": "330211101003",
      "pcode": "330211101000",
      "mark": null
    },
    {
      "id": 12362,
      "name": "西经堂村委会",
      "code": "330211101203",
      "pcode": "330211101000",
      "mark": null
    },
    {
      "id": 12363,
      "name": "九龙湖村委会",
      "code": "330211101200",
      "pcode": "330211101000",
      "mark": null
    },
    {
      "id": 12364,
      "name": "田杨陈村委会",
      "code": "330211101201",
      "pcode": "330211101000",
      "mark": null
    },
    {
      "id": 12365,
      "name": "思源社区",
      "code": "330211101001",
      "pcode": "330211101000",
      "mark": null
    },
    {
      "id": 12366,
      "name": "田顾村委会",
      "code": "330211101210",
      "pcode": "330211101000",
      "mark": null
    },
    {
      "id": 12367,
      "name": "龙源社区",
      "code": "330211101002",
      "pcode": "330211101000",
      "mark": null
    },
    {
      "id": 12368,
      "name": "杜夹岙村委会",
      "code": "330211101202",
      "pcode": "330211101000",
      "mark": null
    },
    {
      "id": 12369,
      "name": "长宏村委会",
      "code": "330211101207",
      "pcode": "330211101000",
      "mark": null
    },
    {
      "id": 12370,
      "name": "河头村委会",
      "code": "330211101204",
      "pcode": "330211101000",
      "mark": null
    },
    {
      "id": 12371,
      "name": "中心村委会",
      "code": "330211101209",
      "pcode": "330211101000",
      "mark": null
    },
    {
      "id": 12372,
      "name": "广源社区",
      "code": "330211100003",
      "pcode": "330211100000",
      "mark": null
    },
    {
      "id": 846351,
      "name": "广源社区",
      "code": "330211100003",
      "pcode": "330211100000",
      "mark": null
    },
    {
      "id": 846352,
      "name": "沿山村委会",
      "code": "330211100202",
      "pcode": "330211100000",
      "mark": null
    },
    {
      "id": 846353,
      "name": "觉渡村委会",
      "code": "330211100203",
      "pcode": "330211100000",
      "mark": null
    },
    {
      "id": 846354,
      "name": "庙戴村委会",
      "code": "330211100204",
      "pcode": "330211100000",
      "mark": null
    },
    {
      "id": 846355,
      "name": "十七房村委会",
      "code": "330211100205",
      "pcode": "330211100000",
      "mark": null
    },
    {
      "id": 846356,
      "name": "湾塘村委会",
      "code": "330211100206",
      "pcode": "330211100000",
      "mark": null
    },
    {
      "id": 846357,
      "name": "岚山村委会",
      "code": "330211100207",
      "pcode": "330211100000",
      "mark": null
    },
    {
      "id": 846358,
      "name": "宁波石化经济技术开发区社区",
      "code": "330211100401",
      "pcode": "330211100000",
      "mark": null
    }
  ]

""" as String

    static def rootCode = '330211000000'

    static void main(String[] args) {
        List<TAreaInfoVO> list = JSON.parseArray(area, TAreaInfoVO.class)
        def rootList = []
        def resultList = []

        for (TAreaInfoVO it : list) {
            if(it.getCode().equals(rootCode)) {
                rootList.add(it)
                break
            }
        }

        if(rootList.size() > 0) {
            resultList = handle(rootList, list)
        }

        println list
    }

    static List<TAreaInfoVO> handle(List<TAreaInfoVO> rList, List<TAreaInfoVO> list) {
        for (TAreaInfoVO areaInfo : rList) {
            def child = areaInfo.getChild()
            for (TAreaInfoVO areaInfo2 : list) {
                if(areaInfo2.getPcode().equals(areaInfo.getCode())) {
                    child.add(areaInfo2)
                }
            }

            println child.size()
        }
    }
}
