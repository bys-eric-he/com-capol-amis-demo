package com.capol.amis.entity.base;

import lombok.Data;

import java.util.Date;

/**
 * 系统信息
 */
@Data
public class SystemInfo {
    public Long id;
    public String userName;
    public String userIp;
    public Long userId;
    public Date updTime;
}
