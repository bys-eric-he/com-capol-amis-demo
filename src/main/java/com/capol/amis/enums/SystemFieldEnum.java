package com.capol.amis.enums;

import io.swagger.models.auth.In;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统字段枚举
 */
@Getter
public enum SystemFieldEnum {

    ID("主键ID", "id", "bigint", 20, 2),
    STATUS("状态", "status", "tinyint", 4, 2),
    CREATOR("创建人", "creator", "varchar", 20, 2),
    CREATOR_ID("创建人ID", "creator_id", "bigint", 20, 2),
    CREATE_TIME("创建时间", "create_time", "datetime", 0, 2),
    CREATED_HOST_IP("创建人IP", "created_host_ip", "varchar", 50, 2),
    LAST_OPERATOR("最后操作人", "last_operator", "varchar", 20, 1),
    LAST_OPERATOR_ID("最后操作人ID", "last_operator_id", "bigint", 20, 2),
    UPDATE_TIME("更新时间", "update_time", "datetime", 0, 2),
    UPDATE_HOST_IP("更新人IP", "update_host_ip", "varchar", 50, 1);
    /**
     * 字段别名
     */
    private String fieldAlias;

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 字段类型
     */
    private String fieldType;

    /**
     * 字段长度
     */
    private Integer fieldLength;

    /**
     * 是否为空
     * 1-可以为空 2-不能为空
     */
    private Integer fieldNull;


    SystemFieldEnum(String fieldAlias,
                    String fieldName,
                    String fieldType,
                    Integer fieldLength,
                    Integer fieldNull) {
        this.fieldAlias = fieldAlias;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.fieldLength = fieldLength;
        this.fieldNull = fieldNull;
    }

    /**
     * 返回所有系统字段信息
     *
     * @return
     */
    public static List<SystemFieldEnum> getMainTableEnum() {
        List<SystemFieldEnum> systemFieldEnums = Arrays.stream(SystemFieldEnum.values()).collect(Collectors.toList());
        return systemFieldEnums;
    }
}
