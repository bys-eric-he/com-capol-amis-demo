package com.capol.amis.entity.bo;

import lombok.Data;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/15 17:42
 * desc: xxljob元数据信息
 */
@Data
public class XxlJobInfoBO {
    /** 主键 */
    private int id;
    /** 任务描述 */
    private String jobDesc;
    /** Cron表达式 */
    private String scheduleConf;
    /** JobHandler */
    private String executorHandler;
    /** 调度中心用户名 */
    private String loginUser;
    /** 调度中心用户密码 */
    private String loginPwd;
    /** 执行器id */
    private int jobGroup;
    /** 调度中心地址 */
    private String address;
}
