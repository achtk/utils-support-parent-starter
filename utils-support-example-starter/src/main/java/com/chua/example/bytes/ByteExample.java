package com.chua.example.bytes;

/**
 * @author CH
 */
public class ByteExample {

    public static void main(String[] args) {
        AisDate aisDate = new AisDate(483392);
        System.out.println("月: "+ aisDate.getMonth());
        System.out.println("天: "+ aisDate.getDay());
        System.out.println("时: "+ aisDate.getHour());
        System.out.println("分: "+ aisDate.getMin());
    }


}
