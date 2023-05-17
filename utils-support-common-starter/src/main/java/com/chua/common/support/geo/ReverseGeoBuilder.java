package com.chua.common.support.geo;

import com.chua.common.support.lang.profile.ProfileProvider;
import com.chua.common.support.spi.ServiceProvider;
import lombok.NoArgsConstructor;

/**
 * 逆物理地址解析
 *
 * @author CH
 */
@NoArgsConstructor(staticName = "builder")
public class ReverseGeoBuilder extends ProfileProvider<ReverseGeoBuilder> {
    /**
     * 实现方式
     */
    public ReverseGeoBuilder type(String name) {
        addProfile("type", name);
        return this;
    }


    /**
     * build
     *
     * @return ReverseGeoPosition
     */
    public ReverseGeoPosition build() {
        ServiceProvider<ReverseGeoPosition> provider = ServiceProvider.of(ReverseGeoPosition.class);
        ReverseGeoPosition reverseGeoPosition = provider.getExtension(getString("type"));
        if(reverseGeoPosition instanceof ProfileProvider) {
            ((ProfileProvider<?>) reverseGeoPosition).addProfile(this);
        }

        return reverseGeoPosition;
    }
}
