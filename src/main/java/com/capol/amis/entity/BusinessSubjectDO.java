package com.capol.amis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.capol.amis.entity.base.BaseInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 业务主题基本信息
 * </p>
 *
 * @author He.Yong
 * @since 2022-06-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cfg_business_subject")
public class BusinessSubjectDO extends BaseInfo {
    /**
     * 企业ID
     */
    private Long enterpriseId;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 业务主题ID
     */
    private Long subjectId;

    /**
     * 业务主题名称
     */
    private String subjectName;

    /**
     * 业务主题表单配置JSON信息
     */
    private String configJson;

    /**
     * 记录状态（0-已删除 1-正常）
     */
    private Integer status;
}
