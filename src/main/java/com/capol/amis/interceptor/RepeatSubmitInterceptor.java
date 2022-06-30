package com.capol.amis.interceptor;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.capol.amis.annotation.RepeatSubmit;
import com.capol.amis.servlet.RepeatedlyRequestWrapper;
import com.capol.amis.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 防止重复提交拦截器
 */
@Slf4j
@Component
public class RepeatSubmitInterceptor implements HandlerInterceptor {
    public final String REPEAT_PARAMS = "repeatParams";

    public final String REPEAT_TIME = "repeatTime";
    /**
     * 防重提交 redis key
     */
    public final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    // 令牌自定义标识
    @Value("${token.header}")
    private String header;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 间隔时间，单位:秒
     * <p>
     * 两次相同参数的请求，如果间隔时间大于该参数，系统不会认定为重复提交的数据
     */
    @Value("${repeatSubmit.intervalTime}")
    private int intervalTime;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            RepeatSubmit annotation = method.getAnnotation(RepeatSubmit.class);
            if (annotation != null) {
                if (this.isRepeatSubmit(request)) {
                    //返回重复提交提示
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("code", "500");
                    resultMap.put("msg", request.getRequestURI() + "不允许重复提交，请稍后再试！");
                    try {
                        response.setStatus(200);
                        response.setContentType("application/json");
                        response.setCharacterEncoding("utf-8");
                        response.getWriter().print(JSONObject.toJSONString(resultMap));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            }
            return true;
        } else {
            return preHandle(request, response, handler);
        }
    }

    /**
     * 验证是否重复提交由子类实现具体的防重复提交的规则
     */
    public boolean isRepeatSubmit(HttpServletRequest request) {
        String nowParams = "";
        if (request instanceof RepeatedlyRequestWrapper) {
            RepeatedlyRequestWrapper repeatedlyRequest = (RepeatedlyRequestWrapper) request;
            nowParams = repeatedlyRequest.getBody();
        }
        // body参数为空，获取Parameter的数据
        if (StrUtil.isBlank(nowParams)) {
            nowParams = JSONObject.toJSONString(request.getParameterMap());
        }
        Map<String, Object> nowDataMap = new HashMap<String, Object>();
        nowDataMap.put(REPEAT_PARAMS, nowParams);
        nowDataMap.put(REPEAT_TIME, System.currentTimeMillis());

        // 请求地址（作为存放cache的key值）
        String url = request.getRequestURI();

        // 唯一值（没有消息头则使用请求地址）
        String submitKey = request.getHeader(header);
        if (StrUtil.isBlank(submitKey)) {
            log.info("-->没有消息头,使用请求地址作为Redis的Key!");
            submitKey = url;
        }

        // 唯一标识（指定key + 消息头）
        String cacheRepeatKey = REPEAT_SUBMIT_KEY + submitKey;

        Object sessionObj = redisUtil.getCacheObject(cacheRepeatKey);
        if (sessionObj != null) {
            Map<String, Object> sessionMap = (Map<String, Object>) sessionObj;
            if (sessionMap.containsKey(url)) {
                Map<String, Object> preDataMap = (Map<String, Object>) sessionMap.get(url);
                if (compareParams(nowDataMap, preDataMap) && compareTime(nowDataMap, preDataMap)) {
                    return true;
                }
            }
        }
        Map<String, Object> cacheMap = new HashMap<String, Object>();
        cacheMap.put(url, nowDataMap);
        //将间隔时间作为Key的过期时间
        redisUtil.setCacheObject(cacheRepeatKey, cacheMap, intervalTime, TimeUnit.SECONDS);
        return false;
    }

    /**
     * 判断参数是否相同
     */
    private boolean compareParams(Map<String, Object> nowMap, Map<String, Object> preMap) {
        String nowParams = (String) nowMap.get(REPEAT_PARAMS);
        String preParams = (String) preMap.get(REPEAT_PARAMS);
        return nowParams.equals(preParams);
    }

    /**
     * 判断两次间隔时间
     */
    private boolean compareTime(Map<String, Object> nowMap, Map<String, Object> preMap) {
        long time1 = (Long) nowMap.get(REPEAT_TIME);
        long time2 = (Long) preMap.get(REPEAT_TIME);
        log.info("-->请求提交间隔时间，单位:{} 秒!", this.intervalTime);
        if ((time1 - time2) < (this.intervalTime * 1000L)) {
            return true;
        }
        return false;
    }
}