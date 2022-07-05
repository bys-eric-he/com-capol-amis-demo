package com.capol.amis.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/1 18:38
 * desc:
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FormFieldConfigExtDO extends FormFieldConfigDO {
    private Long enterpriseId;
}
