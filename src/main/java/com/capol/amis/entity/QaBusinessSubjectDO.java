package com.capol.amis.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 业务主题表
 * cfg_business_subject
 */
@TableName(value ="cfg_business_subject")
@Data
public class QaBusinessSubjectDO implements Serializable {
    /**
     * 主键id
     */
    @TableId
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
    private Integer level;

    /**
     * 1：正常，0：已删除
     */
    private Integer status;

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

    /**
     * 
     */
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
    private Integer tableSourceType;

    /**
     * 任务分类或者任务的记录id
     */
    private Long jobId;

    /**
     * word模板文件id
     */
    private Long fileId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        QaBusinessSubjectDO other = (QaBusinessSubjectDO) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getEnterpriseId() == null ? other.getEnterpriseId() == null : this.getEnterpriseId().equals(other.getEnterpriseId()))
            && (this.getSubjectName() == null ? other.getSubjectName() == null : this.getSubjectName().equals(other.getSubjectName()))
            && (this.getSubjectCode() == null ? other.getSubjectCode() == null : this.getSubjectCode().equals(other.getSubjectCode()))
            && (this.getParentId() == null ? other.getParentId() == null : this.getParentId().equals(other.getParentId()))
            && (this.getOrderNo() == null ? other.getOrderNo() == null : this.getOrderNo().equals(other.getOrderNo()))
            && (this.getLevel() == null ? other.getLevel() == null : this.getLevel().equals(other.getLevel()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getCreator() == null ? other.getCreator() == null : this.getCreator().equals(other.getCreator()))
            && (this.getCreatorId() == null ? other.getCreatorId() == null : this.getCreatorId().equals(other.getCreatorId()))
            && (this.getCreatedHostIp() == null ? other.getCreatedHostIp() == null : this.getCreatedHostIp().equals(other.getCreatedHostIp()))
            && (this.getLastOperator() == null ? other.getLastOperator() == null : this.getLastOperator().equals(other.getLastOperator()))
            && (this.getLastOperatorId() == null ? other.getLastOperatorId() == null : this.getLastOperatorId().equals(other.getLastOperatorId()))
            && (this.getUpdateHostIp() == null ? other.getUpdateHostIp() == null : this.getUpdateHostIp().equals(other.getUpdateHostIp()))
            && (this.getMenuIcon() == null ? other.getMenuIcon() == null : this.getMenuIcon().equals(other.getMenuIcon()))
            && (this.getConfigJson() == null ? other.getConfigJson() == null : this.getConfigJson().equals(other.getConfigJson()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getTableSourceType() == null ? other.getTableSourceType() == null : this.getTableSourceType().equals(other.getTableSourceType()))
            && (this.getJobId() == null ? other.getJobId() == null : this.getJobId().equals(other.getJobId()))
            && (this.getFileId() == null ? other.getFileId() == null : this.getFileId().equals(other.getFileId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getEnterpriseId() == null) ? 0 : getEnterpriseId().hashCode());
        result = prime * result + ((getSubjectName() == null) ? 0 : getSubjectName().hashCode());
        result = prime * result + ((getSubjectCode() == null) ? 0 : getSubjectCode().hashCode());
        result = prime * result + ((getParentId() == null) ? 0 : getParentId().hashCode());
        result = prime * result + ((getOrderNo() == null) ? 0 : getOrderNo().hashCode());
        result = prime * result + ((getLevel() == null) ? 0 : getLevel().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getCreator() == null) ? 0 : getCreator().hashCode());
        result = prime * result + ((getCreatorId() == null) ? 0 : getCreatorId().hashCode());
        result = prime * result + ((getCreatedHostIp() == null) ? 0 : getCreatedHostIp().hashCode());
        result = prime * result + ((getLastOperator() == null) ? 0 : getLastOperator().hashCode());
        result = prime * result + ((getLastOperatorId() == null) ? 0 : getLastOperatorId().hashCode());
        result = prime * result + ((getUpdateHostIp() == null) ? 0 : getUpdateHostIp().hashCode());
        result = prime * result + ((getMenuIcon() == null) ? 0 : getMenuIcon().hashCode());
        result = prime * result + ((getConfigJson() == null) ? 0 : getConfigJson().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getTableSourceType() == null) ? 0 : getTableSourceType().hashCode());
        result = prime * result + ((getJobId() == null) ? 0 : getJobId().hashCode());
        result = prime * result + ((getFileId() == null) ? 0 : getFileId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", enterpriseId=").append(enterpriseId);
        sb.append(", subjectName=").append(subjectName);
        sb.append(", subjectCode=").append(subjectCode);
        sb.append(", parentId=").append(parentId);
        sb.append(", orderNo=").append(orderNo);
        sb.append(", level=").append(level);
        sb.append(", status=").append(status);
        sb.append(", creator=").append(creator);
        sb.append(", creatorId=").append(creatorId);
        sb.append(", createdHostIp=").append(createdHostIp);
        sb.append(", lastOperator=").append(lastOperator);
        sb.append(", lastOperatorId=").append(lastOperatorId);
        sb.append(", updateHostIp=").append(updateHostIp);
        sb.append(", menuIcon=").append(menuIcon);
        sb.append(", configJson=").append(configJson);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", tableSourceType=").append(tableSourceType);
        sb.append(", jobId=").append(jobId);
        sb.append(", fileId=").append(fileId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}