package com.chua.common.support.collection;

/**
 * 位图类
 *
 * @author CH
 */
public class Bitmap {
    /*
     *使用long型数组保存点
     */
    private final long[] elementData;

    /**
    位图保存的点的状态种数
     */
    private final long stateNum;
    /**
    存储一个位图保存的点的状态值所需要的位数
     */
    private final int stateBitNum;
    /**
    位图可以储存的最大点数，初始化Bitmap类时指定
     */
    private final long MAX_SIZE;
    /**
    一个long型变量可以保存的点的数量
     */
    private final long numOfOneLong;
    /**
    该位图已经保存的点的数量
     */
    private long size;

    /**
     * @param stateNum 位图中点的状态数量,state>1
     * @param length   位图大小,0<length
     *                 如果 length > ⌈ log2(stateNum) ⌉（向上取整） / 64 * Integer.MAX_VALUE，则说明该 Bitmap 类无法储存 length 个 stateNum 种数的点，抛出异常
     */
    public Bitmap(long stateNum, long length) {
        if (stateNum > 1 && length > 0) {
            /*
            计算状态值需要多少位来表示
             */
            stateBitNum = 64 - Long.numberOfLeadingZeros(stateNum - 1);
            /*
            计算一个long型里面最多可以存放几个状态值，
            如果不能整除的话，就舍弃余下位数，
            因为在数组中跨元素进行位值的存取计算极其复杂，故舍弃部分位空间，方便位图的各项操作
             */
            numOfOneLong = 64 / stateBitNum;
            /*
            一个long型最多可以存储numOfOneLong个状态值，
            所以该位图最大能存取numOfOneLong*Integer.MAX_VALUE个点，
            如果需要存储的数量length大于位图的最大存储数（numOfOneLong*Integer.MAX_VALUE）就说明该类Bitmap无法满足要求，抛出异常
             */
            if (length > numOfOneLong * Integer.MAX_VALUE) {
                throw new RuntimeException("初始化的位图过大，无法存储！！！");
            }
            this.stateNum = stateNum;
            MAX_SIZE = length;

            if (length % numOfOneLong == 0) {
                elementData = new long[(int) (length / numOfOneLong)];
            }
            /*
            如果length个点不能被数组正好放下，就需要多添加一个数组元素来存储点
             */
            else {
                elementData = new long[((int) (length / numOfOneLong)) + 1];
            }
        } else {
            throw new RuntimeException("位图类的初始化传入参数值中没有负整数！！！");
        }
    }

    /**
     * 顺序添加点
     *
     * @param state 点的状态
     * @return true表示添加成功，产生false的情况有：位图满了；状态值越界
     */
    public boolean add(long state) {
        if (state > stateNum - 1 || state < 0 || size == MAX_SIZE) {
            return false;
        }
        int index = (int) (size / numOfOneLong);
        int left = (int) (size % numOfOneLong);
        elementData[index] |= state << (64 - stateBitNum * (1 + left));
        ++size;
        return true;
    }

    public long find(long index) {
        if (index < 0 || index > size - 1) {
            return -1;
        }
        /*
        计算数组中哪一个元素保存着index索引对应的点
         */
        int arrayIndex = (int) (index / numOfOneLong);
        /*
        计算元素中从哪一位开始的位保存着index索引对应的点
         */
        int elementIndex = (int) (index % numOfOneLong);
        /*
        通过左移，清空掉左边无用的位，再通过无符号右移清空掉右边无用的位，最终正好是需要查找的index位置对应的state值
         */
        return elementData[arrayIndex] << (stateBitNum * elementIndex) >>> (64 - stateBitNum);
    }

    public boolean update(long index, long state) {
        if (index < 0 || index > size - 1 || state > stateNum - 1 || state < 0) {
            return false;
        }
        int arrayIndex = (int) (index / numOfOneLong);
        int left = (int) (index % numOfOneLong);
        elementData[arrayIndex] |= state << (64 - stateBitNum * (1 + left));
        return true;
    }

    /**
     * 返回位图中点的状态数量
     */
    public long getStateNum() {
        return stateNum;
    }

    /**
     * 返回位图可以存储的点的最大数量
     */
    public long getMaxSize() {
        return MAX_SIZE;
    }

    /**
     * 返回位图中实际已使用的数量
     */
    public long getSize() {
        return size;
    }

    /**
     * 辅助toString()方法，主要用于打印位图的具体显示
     */
    private String elementDataToString() {
        StringBuilder result = new StringBuilder("[\n");
        for (long element : elementData) {
            String eleString = Long.toBinaryString(element);
            StringBuilder one = new StringBuilder();
            for (int i = 0; i < 64 - eleString.length(); i++) {
                one.append("0");
            }
            one.append(eleString);
            for (int i = 0; i < numOfOneLong + 1; i++) {
                one.insert((stateBitNum + 1) * i, ',');
            }
            result.append(one.substring(1, one.lastIndexOf(","))).append(",\n");
        }
        return result.append("]").toString();
    }

    @Override
    public String toString() {
        return "Bitmap{\n" +
                "elementData=" + elementDataToString() +
                ", \nstateNum=" + stateNum +
                ", \nstateBitNum=" + stateBitNum +
                ", \nMAX_SIZE=" + MAX_SIZE +
                ", \nsize=" + size +
                "\n}";
    }
}
