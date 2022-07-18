package com.capol.amis.service;

import com.capol.amis.entity.bo.XxlJobInfoBO;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/18 09:21
 * desc: 定时任务接口
 */
public interface ICrontabService {
    long add(XxlJobInfoBO jobInfo);
}
