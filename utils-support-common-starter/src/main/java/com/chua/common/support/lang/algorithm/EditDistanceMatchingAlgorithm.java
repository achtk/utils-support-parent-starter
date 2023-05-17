package com.chua.common.support.lang.algorithm;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.collection.TypeHashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * 编辑距离匹配算法
 *
 * @author CH
 */
@Slf4j
@Spi("edit-distance")
public final class EditDistanceMatchingAlgorithm extends TypeHashMap implements MatchingAlgorithm {

    @Override
    public double match(String source, String target) {
        int sourceLen = source.length();
        int targetLen = target.length();
        double minDistance = minDistance(targetLen > sourceLen ? target : source, targetLen < sourceLen ? target : source);
        if (targetLen > sourceLen) {
            return 1 - minDistance;
        }

        return minDistance;
    }

    /**
     * 编辑距离算法
     *
     * @param sourceStr 原字符串
     * @param targetStr 目标字符串
     * @return 返回最小距离: 原字符串需要变更多少次才能与目标字符串一致（变更动作：增加/删除/替换,每次都是以字节为单位）
     */
    public double minDistance(String sourceStr, String targetStr) {
        if (sourceStr == null && targetStr == null) {
            return 1;
        }

        if (null != sourceStr && sourceStr.equals(targetStr)) {
            return 1;
        }

        int sourceLen = sourceStr.length();
        int targetLen = targetStr.length();

        if (sourceLen == 0) {
            return 0;
        }
        if (targetLen == 0) {
            return 0;
        }

        //定义矩阵(二维数组)
        int[][] arr = new int[sourceLen + 1][targetLen + 1];

        for (int i = 0; i < sourceLen + 1; i++) {
            arr[i][0] = i;
        }
        for (int j = 0; j < targetLen + 1; j++) {
            arr[0][j] = j;
        }

        Character sourceChar;
        Character targetChar;

        for (int i = 1; i < sourceLen + 1; i++) {
            sourceChar = sourceStr.charAt(i - 1);

            for (int j = 1; j < targetLen + 1; j++) {
                targetChar = targetStr.charAt(j - 1);

                if (sourceChar.equals(targetChar)) {
                    /*
                     *  如果source[i] 等于target[j]，则：d[i, j] = d[i-1, j-1] + 0          （递推式 1）
                     */
                    arr[i][j] = arr[i - 1][j - 1];
                } else {
                    /*  如果source[i] 不等于target[j]，则根据插入、删除和替换三个策略，分别计算出使用三种策略得到的编辑距离，然后取最小的一个：
                        d[i, j] = min(d[i, j - 1] + 1, d[i - 1, j] + 1, d[i - 1, j - 1] + 1 )    （递推式 2）
                        >> d[i, j - 1] + 1 表示对source[i]执行插入操作后计算最小编辑距离
                        >> d[i - 1, j] + 1 表示对source[i]执行删除操作后计算最小编辑距离
                        >> d[i - 1, j - 1] + 1表示对source[i]替换成target[i]操作后计算最小编辑距离
                    */
                    arr[i][j] = (Math.min(Math.min(arr[i - 1][j], arr[i][j - 1]), arr[i - 1][j - 1])) + 1;
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("----------矩阵打印---------------");
            //矩阵打印
            for (int i = 0; i < sourceLen + 1; i++) {
                for (int j = 0; j < targetLen + 1; j++) {
                    log.debug("{}", arr[i][j]);
                }
            }
            log.debug("----------矩阵打印---------------");
        }

        return arr[sourceLen][targetLen] / (double) arr.length;
    }

    /**
     * 计算字符串相似度
     * similarity = (maxlen - distance) / maxlen
     * ps: 数据定义为double类型,如果为int类型 相除后结果为0(只保留整数位)
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return 概率
     */
    public Double getSimilarity(String str1, String str2) {
        double maxLen = Math.max(str1.length(), str2.length());
        return (maxLen - minDistance(str1, str2)) / maxLen;
    }
}
