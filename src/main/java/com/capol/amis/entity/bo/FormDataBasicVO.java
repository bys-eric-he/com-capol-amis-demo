package com.capol.amis.entity.bo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/8 14:14
 * desc: 表单数据基本信息
 */
@Data
@Accessors(chain = true)
public class FormDataBasicVO {
    private Long rowId;
    private String fieldName;
    private Long fieldHash;
}
