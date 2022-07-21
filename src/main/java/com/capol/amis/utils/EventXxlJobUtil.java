package com.capol.amis.utils;

import cn.hutool.core.map.MapUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.capol.amis.entity.bo.XxlJobInfoBO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/15 17:48
 * desc: xxl-job任务工具
 */
@Slf4j
@Component
public class EventXxlJobUtil {

    private static final String LOGIN_URL = "/login";
    private static final String ADD_URL = "/jobinfo/add";

    /**
     * 调度中心接口的添加任务接口，向xxl-job中添加任务信息
     */
    public HttpResponse addXxlJob(XxlJobInfoBO jobInfo) {
        String targetPath = jobInfo.getAddress() + ADD_URL;
        Map<String, Object> paramMap = getParamMap(jobInfo);
        HttpResponse response = doPost(targetPath, paramMap, jobInfo);
        log.info("新增任务{}的返回response:{}", jobInfo.getJobDesc(), response.toString());
        return response;
    }

    /**
     * 发起远程调用请求
     */
    private HttpResponse doPost(String url, Map<String, Object> paramMap, XxlJobInfoBO jobInfo) {
        String cookie = getCookie(jobInfo);
        if (StringUtils.isEmpty(cookie)) {
            log.warn("==========>>>>>>>>>> 获取调度中心的cookie为空 <<<<<<<<<<==========");
        }
        HttpRequest request = HttpRequest.post(url).cookie(cookie);
        return MapUtil.isEmpty(paramMap) ? request.execute() : request.form(paramMap).execute();
    }

    /**
     * 登录xxl-job获取cookie
     */
    private String getCookie(XxlJobInfoBO jobInfo) {
        String path = jobInfo.getAddress() + LOGIN_URL;
        Map<String, Object> map = new HashMap<>();
        map.put("userName", jobInfo.getLoginUser());
        map.put("password", jobInfo.getLoginPwd());
        HttpResponse response = HttpRequest.post(path).form(map).execute();
        StringBuilder sb = new StringBuilder();
        if (response.isOk()) {
            log.info("==========>>>>>>>>>> 获取调度中心的cookie成功 <<<<<<<<<<==========");
            List<HttpCookie> cookies = response.getCookies();
            for (HttpCookie cookie : cookies) {
                sb.append(cookie.toString());
            }
        } else {
            log.warn("==========>>>>>>>>>> 获取调度中心的cookie失败 <<<<<<<<<<==========");
        }
        return sb.toString();
    }

    /**
     * 封装xxl-job调度中心的任务信息
     */
    private Map<String, Object> getParamMap(XxlJobInfoBO xxlInfo) {
        Map<String, Object> paramMap = new HashMap<>();
        // ============================== 基础配置 ==============================
        // 执行器主键ID
        paramMap.put("jobGroup", xxlInfo.getJobGroup());
        // 执行器，任务Handler名称，需提前在项目中定义
        paramMap.put("executorHandler", xxlInfo.getExecutorHandler());
        // 任务描述
        paramMap.put("jobDesc", xxlInfo.getJobDesc());
        // 负责人
        paramMap.put("author", "admin");
        // 报警邮件
        // paramMap.put("alarmEmail", "xxx@satcloud.com.cn");
        // ============================== 调度配置 ==============================
        // 调度类型
        paramMap.put("scheduleType", "CRON");
        // CRON表达式
        paramMap.put("scheduleConf", xxlInfo.getScheduleConf());

        // ============================== 任务配置 ==============================
        // GLUE类型    #com.xxl.job.core.glue.GlueTypeEnum
        paramMap.put("glueType", "BEAN");
        // GLUE备注
        paramMap.put("glueRemark", "GLUE代码初始化");
        // 执行器，任务参数
        // paramMap.put("executorParam", "测试");
        // ============================== 高级配置 ==============================
        // 执行器路由策略
        paramMap.put("executorRouteStrategy", "ROUND");
        // 调度过期策略
        paramMap.put("misfireStrategy", "DO_NOTHING");
        // 阻塞处理策略
        paramMap.put("executorBlockStrategy", "DISCARD_LATER");
        // 任务执行超时时间，单位秒
        paramMap.put("executorTimeout", 0);
        // 失败重试次数
        paramMap.put("executorFailRetryCount", 0);
        // 调度状态：0-停止，1-运行
        paramMap.put("triggerStatus", 0);
        return paramMap;
    }


}
