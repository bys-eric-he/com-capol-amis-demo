package com.capol.amis.service.impl;

import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.capol.amis.entity.bo.XxlJobInfoBO;
import com.capol.amis.service.ICrontabService;
import com.capol.amis.service.IDataSyncService;
import com.capol.amis.utils.EventXxlJobUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/18 09:22
 * desc: 定时任务实现类
 */
@Service
@Slf4j
public class CrontabServiceImpl implements ICrontabService {

    @Value("${xxl.job.admin.username}")
    private String loginUser;

    @Value("${xxl.job.admin.password}")
    private String loginPwd;

    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;

    @Value("${xxl.job.executor.jobGroup}")
    private int jobGroup;

    @Autowired
    private EventXxlJobUtil xxlJobUtil;

    @Autowired
    private IDataSyncService dataSyncService;

    @Override
    public long add(XxlJobInfoBO jobInfo) {
        long xxlJobId = 0L;
        initJobInfo(jobInfo);
        HttpResponse response = xxlJobUtil.addXxlJob(jobInfo);
        if (response.isOk()) {
            JSONObject jsonObject = JSON.parseObject(response.body());
            xxlJobId = jsonObject.getLongValue("content");
            log.info("==========>>>>>>>>>> 调度中心添加任务接口,内容为:{},id值为:{}", jsonObject, xxlJobId);
            dataSyncService.registerJobHandler(jobInfo.getExecutorHandler());
        }
        return xxlJobId;
    }

    private void initJobInfo(XxlJobInfoBO jobInfo) {
        if (StringUtils.isBlank(adminAddresses)) {
            throw new RuntimeException("调度中心访问地址获取失败");
        }
        String[] addrArray = adminAddresses.split(",");
        jobInfo.setAddress(addrArray[0].trim());
        jobInfo.setJobGroup(jobGroup);
        jobInfo.setLoginUser(loginUser);
        jobInfo.setLoginPwd(loginPwd);
    }

}
