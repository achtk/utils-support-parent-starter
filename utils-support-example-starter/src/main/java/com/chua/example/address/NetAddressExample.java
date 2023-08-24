package com.chua.example.address;

import com.chua.common.support.net.NetAddress;

/**
 * @author CH
 */
public class NetAddressExample {

    public static void main(String[] args) {
        NetAddress netAddress = NetAddress.of("http://www.baidu.com/s?wd=1");
        System.out.println(netAddress);
    }
}
