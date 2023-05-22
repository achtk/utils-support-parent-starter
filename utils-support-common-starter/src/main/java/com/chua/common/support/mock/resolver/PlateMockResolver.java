package com.chua.common.support.mock.resolver;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.collection.ImmutableBuilder;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.mock.MockValue;
import com.chua.common.support.utils.RandomUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 车牌
 *
 * @author CH
 */
@Spi("plate")
public class PlateMockResolver implements MockResolver {
    /**
     * 车牌号码候选字母(无I/O)
     */
    private static final List<String> PLATE_NUMBERS_LIST = ImmutableBuilder.<String>builder().add(
            "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z").unmodifiableList();

    /**
     * 省份前缀
     */
    private static final List<String> PROVINCE_PREFIX_LIST = ImmutableBuilder.<String>builder().add(
            "京", "津", "冀", "晋", "蒙",
            "辽", "吉", "黑", "沪", "苏",
            "浙", "皖", "闽", "赣", "鲁",
            "豫", "鄂", "湘", "粤", "桂",
            "琼", "渝", "川", "贵", "云",
            "藏", "陕", "甘", "宁", "青", "新").unmodifiableList();

    @Override
    public String resolve(MockValue mock, ExpressionParser expressionParser) {
        return getRandom(RandomUtils.randomInt() < 0);
    }

    /**
     * 获取随机车牌
     * @param isNewEnergyVehicle 是否新能源
     * @return 车牌
     */
    public static String getRandom(boolean isNewEnergyVehicle) {
        int length = 5;
        List<String> plateNumbers = new ArrayList<>(length);
        String prefix = RandomUtils.getRandomElement(PROVINCE_PREFIX_LIST);
        //最多2个字母
        int alphaCnt = RandomUtils.randomInt(0, 3);
        if (alphaCnt > 0) {
            for (int i = 0; i < alphaCnt; i++) {
                plateNumbers.add(RandomUtils.getRandomElement(PLATE_NUMBERS_LIST));
            }
        }
        //剩余部分全是数字
        int numericCnt = length - alphaCnt;
        for (int i = 0; i < numericCnt; i++) {
            plateNumbers.add(String.valueOf(RandomUtils.randomInt(0, 10)));
        }
        //打乱顺序
        Collections.shuffle(plateNumbers);

        String newEnergyVehicleTag = "";
        if (isNewEnergyVehicle) {
            int j = RandomUtils.randomInt(0, 2);
            //新能源车牌前缀为D或F
            newEnergyVehicleTag = (j == 0 ? "D" : "F");
        }
        return prefix + RandomUtils.getRandomElement(PLATE_NUMBERS_LIST)
                + newEnergyVehicleTag + Joiner.on("").join(plateNumbers);
    }
}
