package com.capol.amis.enums;

import lombok.Getter;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/7 15:40
 * desc: 两表关联关系
 */
@Getter
public enum UnionJoinTypeEnum {
    /**
     * 连接关系
     */
    LEFT_JOIN(0, "left join"),
    RIGHT_JOIN(1, "left join"),
    INNER_JOIN(2, "join"),
    OUTER_JOIN(3, "outer join");


    private final Integer joinCode;

    private final String joinDesc;

    UnionJoinTypeEnum(Integer joinCode, String joinDesc) {
        this.joinCode = joinCode;
        this.joinDesc = joinDesc;
    }

}
