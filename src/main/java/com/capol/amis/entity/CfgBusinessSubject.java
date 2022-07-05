package com.capol.amis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 业务主题表
 * </p>
 *
 * @author zyx
 * @since 2022-07-01
 */
@Data
public class CfgBusinessSubject implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 企业id 
     */
    private Long enterpriseId;

    /**
     * 主题名称
     */
    private String subjectName;

    /**
     * 主题编号
     */
    private String subjectCode;

    /**
     * 父级id
     */
    private Long parentId;

    /**
     * 排序
     */
    private Integer orderNo;

    /**
     * 层级（1：“一级主题”，2：“二级主题”）
     */
    private Boolean level;

    /**
     * 1：正常，0：已删除
     */
    private Boolean status;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建人id
     */
    private Long creatorId;

    /**
     * 创建人IP
     */
    private String createdHostIp;

    /**
     * 最后操作人 
     */
    private String lastOperator;

    /**
     * 最后操作人id 
     */
    private Long lastOperatorId;

    /**
     * 更新人IP 
     */
    private String updateHostIp;

    /**
     * 图标
     */
    private String menuIcon;

    private String configJson;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 表来源类型(1宽表;2用户自定义表)
     */
    private Boolean tableSourceType;

    /**
     * 任务分类或者任务的记录id
     */
    private Long jobId;

    /**
     * word模板文件id
     */
    private Long fileId;

}
