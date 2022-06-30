package com.capol.amis.entity.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据表通用基本信息
 */
@Data
public class BaseInfo implements Serializable {

    private static final long serialVersionUID = 455641123456667899L;

    /**
     * 主键ID
     * 指定主键⽣成策略使⽤雪花算法（默认策略）
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 创建人主机IP
     */
    private String createdHostIp;

    /**
     * 最近一次操作人
     */
    private String lastOperator;

    /**
     * 最近一次操作人ID
     */
    private Long lastOperatorId;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 更新人主机IP
     */
    private String updateHostIp;

    /**
     * 判断当前实体是否为新增
     *
     * @return
     */
    private boolean isNew() {
        return this.id == null;
    }

    /**
     * 系统信息
     *
     * @param systemInfo
     */
    public void setSystemInfo(SystemInfo systemInfo) {
        if (isNew()) {
            this.id = systemInfo.id;
            this.creator = systemInfo.userName;
            this.creatorId = systemInfo.userId;
            this.createTime = systemInfo.updTime;
            this.updateTime = systemInfo.updTime;
            this.updateHostIp = systemInfo.userIp;
            this.createdHostIp = systemInfo.userIp;
            this.lastOperator = systemInfo.userName;
            this.lastOperatorId = systemInfo.userId;
        } else {
            this.updateHostIp = systemInfo.userIp;
            this.updateTime = systemInfo.updTime;
            this.lastOperator = systemInfo.userName;
            this.lastOperatorId = systemInfo.userId;
        }
    }
}
