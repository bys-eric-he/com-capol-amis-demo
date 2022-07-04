package com.capol.amis.entity.base;

import lombok.Data;

/**
 * 企业及项目信息
 */
@Data
public class EnterpriseProjectInfo {
    /**
     * 项目ID
     */
    private Long projectId;
    /**
     * 企业ID
     */
    private Long enterpriseId;
}
