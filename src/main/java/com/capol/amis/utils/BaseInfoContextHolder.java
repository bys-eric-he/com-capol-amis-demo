package com.capol.amis.utils;

import com.capol.amis.entity.base.BaseInfo;
import com.capol.amis.entity.base.EnterpriseProjectInfo;
import com.capol.amis.entity.base.SystemInfo;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.Date;

@Slf4j
public class BaseInfoContextHolder {

    private static SnowflakeUtil snowflakeUtil = SpringContextHolder.getBean("SnowflakeUtil");

    /**
     * 企业项目信息
     *
     * @return
     */
    public static EnterpriseProjectInfo getEnterpriseAndProjectInfo() {
        EnterpriseProjectInfo enterpriseProjectInfo = new EnterpriseProjectInfo();
        enterpriseProjectInfo.setEnterpriseId(324225698253233659L);
        enterpriseProjectInfo.setProjectId(snowflakeUtil.nextId());

        return enterpriseProjectInfo;
    }

    /**
     * 基本信息
     *
     * @return
     */
    public static BaseInfo getBaseInfo() {
        BaseInfo baseInfo = new BaseInfo();
        SystemInfo systemInfo = new SystemInfo();

        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            systemInfo.setId(snowflakeUtil.nextId());
            systemInfo.setUserId(snowflakeUtil.nextId());
            systemInfo.setUserName("He.Yong");
            systemInfo.setUserIp(ip);
            systemInfo.setUpdTime(new Date());

            baseInfo.setSystemInfo(systemInfo);
        } catch (Exception exception) {
            log.error("-->获取基本信息异常:" + exception.getMessage());
        }

        return baseInfo;
    }

    /**
     * 系统基本信息
     *
     * @return
     */
    public static SystemInfo getSystemInfo() {
        SystemInfo systemInfo = new SystemInfo();

        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            systemInfo.setId(snowflakeUtil.nextId());
            systemInfo.setUserId(snowflakeUtil.nextId());
            systemInfo.setUserName("He.Yong");
            systemInfo.setUserIp(ip);
            systemInfo.setUpdTime(new Date());
        } catch (Exception exception) {
            log.error("-->获取系统基本信息异常:" + exception.getMessage());
        }

        return systemInfo;
    }
}
