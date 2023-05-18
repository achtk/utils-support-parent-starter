package com.chua.example.mapping.guangdian;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 最热的活动类别
 *
 * @author CH
 */
@NoArgsConstructor
@Data
public class AssessHotCats {

    private String id;
    private String name;
    private Integer totalCount;
    private Integer totalScore;
}
