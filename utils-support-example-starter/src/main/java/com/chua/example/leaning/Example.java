package com.chua.example.leaning;

import com.chua.common.support.utils.ByteUtils;
import com.chua.common.support.utils.Hex;

/**
 * @author CH
 */
public class Example {

    public static void main(String[] args) throws Exception {
        String hex = "55 A1 01 E4 03 01 32 00";
        String validate;
        int tpl = 0;
        for (String s : hex.split("\\s+")) {
            int i = Integer.valueOf(s, 16);
            tpl ^= i;
        }

        System.out.println(Hex.everySpace(Hex.encodeHexUpperString(ByteUtils.asBytes(tpl))));

//        MqServer mqServer = new MqServer();
//        mqServer.start();
//        Integer[] arr = {832,833,834,1040,1041,1042,1095,1096,1097,1098,1099,1100,1101,1102,1229,1230,1231,1232,1233,1234,1235};
//
//        Arrays.sort(arr);
//        Map<Integer, List<Integer>> tpl = new LinkedHashMap<>();
//
//        for (int i = 0; i < arr.length; i++) {
//            Integer integer = arr[i];
//            if(i == 0) {
//                List<Integer> item = tpl.computeIfAbsent(integer, it -> new LinkedList<>());
//                item.add(integer);
//                continue;
//            }
//            List<Integer> item = tpl.computeIfAbsent(integer - 1, it -> new LinkedList<>());
//            item.add(integer);
//            tpl.put(integer, item);
//        }
//
//        Set<List<Integer>> lists = new LinkedHashSet<>(tpl.values());
//        System.out.println();
    }
}
