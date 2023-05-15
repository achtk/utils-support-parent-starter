package com.chua.common.support.pojo;

import lombok.Data;

/**
 * 计算机唯一标识
 *
 * @author CH
 */
@Data
public class ComputerUniqueIdentification {

    private String namePrefix;
    private String mainBoardSerialNumber;
    private String macAddress;
    private String cpuIdentification;

    public ComputerUniqueIdentification(String namePrefix, String mainBoardSerialNumber, String macAddress, String cpuIdentification) {
        this.namePrefix = namePrefix;
        this.mainBoardSerialNumber = mainBoardSerialNumber;
        this.macAddress = macAddress;
        this.cpuIdentification = cpuIdentification;
    }

    @Override
    public String toString() {
        return new StringBuilder().append('{')
                .append("\"namePrefix=\":\"").append(namePrefix).append("\",")
                .append("\"mainBoardSerialNumber=\":\"").append(mainBoardSerialNumber).append("\",")
                .append("\"MACAddress=\":\"").append(macAddress).append("\",")
                .append("\"CPUIdentification=\":\"").append(cpuIdentification)
                .append("\"}").toString();
    }
}
