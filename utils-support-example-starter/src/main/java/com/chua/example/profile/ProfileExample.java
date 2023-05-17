package com.chua.example.profile;

import com.chua.common.support.lang.profile.Profile;
import com.chua.common.support.lang.profile.ProfileBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author CH
 */
@Slf4j
public class ProfileExample {

    public static void main(String[] args) {
        Profile profile = ProfileBuilder.newBuilder().build();
        profile.addProfile("actuator.properties")
                .addProfile("log4j2.xml")
                .addProfile("0x0404.ini")
                .addProfile("Cargo.toml")
                .addProfile("application.yml");

        log.info("server.port -> {}", profile.getObject("#server['port']"));
        log.info("0x0404.1100 -> {}", profile.getString("0x0404.1100"));
//        log.info("bind entity -> {}", profile.bind(ObjectFileObject.class));
        System.out.println();
    }
}
