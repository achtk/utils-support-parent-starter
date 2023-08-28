package com.chua.common.support.view.view;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Gauss
 *
 * @author CH
 */
public class GaussView implements View {

    /**
     * 统计的标记数目y轴（分组数）
     */
    private int ySize;
    /**
     * 生成的虚拟数据的个数
     */
    private final int dataNumber;
    /**
     * 刻度数目x轴
     */
    private final int xSize;

    /**
     * 保存生成的高斯数据
     */
    private final ArrayList<Double> list = new ArrayList<>();
    /**
     * 根据分组统计数据个数
     */
    private final Map<Integer, Integer> map = new HashMap<>();

    public GaussView(int xSize, int ySize, int dataNumber) {
        this.ySize = Math.max(ySize, 3);
        this.xSize = Math.max(xSize, 3);
        this.dataNumber = Math.max(dataNumber, 1000);
        init();
    }

    private void init() {
        //初始化高斯随机数
        Random ran = new Random();
        for (int i = 0; i < dataNumber; i++) {
            list.add(ran.nextGaussian());
        }
        //初始化统计容器
        for (int i = 1; i <= this.ySize; i++) {
            map.put(i, 0);
        }
    }

    /**
     * 分析并统计高斯随机数
     */
    public void analysis() {
        /*
         * 利用Stream进行统计，由于Stream终极方法会关闭，当重复使用Stream时
         * 我们需要用供应商不断的提供相同的stream。
         */
        //Lambda表达式给供应商
        Supplier<Stream<Double>> supp = () -> list.stream();
        //为Stream提供一个比较器
        Comparator<Double> comp = (e1, e2) -> e1 > e2 ? 1 : -1;
        //获取最大最小值
        double max = supp.get().max(comp).get();
        double min = supp.get().min(comp).get();
        //计算统计区间的单位范围
        double range = (max - min) / this.ySize;
        //将每一个标记区的数据统计后放入map中。
        for (int i = 1; i <= this.ySize; i++) {
            double start = min + (i - 1) * range;
            double end = min + i * range;
            Stream<Double> stream = supp.get()
                    .filter((e) -> e >= start).filter((e) -> e < end);
            map.put(i, (int) stream.count());
        }
    }

    /**
     * 绘制统计图
     */
    public StringBuilder grawValue() {
        StringBuilder sb = new StringBuilder();
        //x轴刻度长度
        int scaleSize = 14;
        int avgScale = this.dataNumber / xSize;
        int printSize = scaleSize - String.valueOf(avgScale).length();
        //打印X轴、刻度以及刻度值
        for (int i = 0; i <= xSize; i++) {
            printChar(sb, ' ', printSize);
            sb.append(i * avgScale);
        }
        sb.append("");
        for (int i = 0; i <= xSize; i++) {
            if (i == 0) {
                printChar(sb, ' ', printSize);
            } else {
                printChar(sb, '-', scaleSize);
            }
        }
        sb.append("\r\n");
        //绘制统计内容
        for (int i = 1; i <= ySize; i++) {
            printChar(sb, ' ', printSize - 1 - String.valueOf(i).length());
            System.out.print(i + ":");
            int scaleValue = map.get(i);
            double grawSize = scaleValue / (avgScale * 1.0 / scaleSize);
            grawSize = (grawSize > 0 && grawSize < 1) ? 1 : grawSize;
            printChar(sb, '█', (int) grawSize);
            sb.append(" " + scaleValue + "\n");
        }
        return sb;
    }

    public Map<Integer, Integer> getAnalysisMap() {
        return this.map;
    }

    private void printChar(StringBuilder sb, char c, int number) {
        for (int i = 0; i < number; i++) {
            sb.append(c);
        }
    }


    @Override
    public String draw() {
        analysis();
        return grawValue().toString();
    }


}

