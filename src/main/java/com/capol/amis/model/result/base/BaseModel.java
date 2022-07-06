package com.capol.amis.model.result.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseModel implements Serializable {

    private static final long serialVersionUID = 455641123456652158L;

    /**
     * 主键ID
     * 指定主键⽣成策略使⽤雪花算法（默认策略）
     */
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
     * 记录状态（0-已删除 1-正常）
     */
    private Integer status;
}
