package com.capol.amis.aspects;


import com.capol.amis.annotation.DataSourceAnno;
import com.capol.amis.helpers.DataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/6 11:22
 * desc: aop实现数据源切换
 *       https://blog.csdn.net/j1231230/article/details/108602206
 */
@Component
@Order(value = -100)
@Slf4j
@Aspect
public class DataSourceAspect {

    @Before("execution(* com.capol.amis.mapper..*.*(..)) || execution(* com.capol.amis.service..*.*(..)) || @annotation(com.capol.amis.annotation.DataSourceAnno)")
    public void before(JoinPoint joinPoint) {
        // execution 中配置的是服务实现类 & MyDataSource的包路径
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();

        DataSourceAnno dataSourceAnno = null;
        // 判断方法上的注解
        if (method.isAnnotationPresent(DataSourceAnno.class)) {
            dataSourceAnno = method.getAnnotation(DataSourceAnno.class);
            DataSourceContextHolder.setDbType(dataSourceAnno.value());
        } else if (method.getDeclaringClass().isAnnotationPresent(DataSourceAnno.class)) {
            // 其次判断类上的注解
            dataSourceAnno = method.getDeclaringClass().getAnnotation(DataSourceAnno.class);
            DataSourceContextHolder.setDbType(dataSourceAnno.value());
        } else {
            // 判断原始类上的注解
            // 防止使用到BaseMapper或者ServiceImpl中的方法
            Type[] types = AopUtils.getTargetClass(joinPoint.getTarget()).getGenericInterfaces();
            if (types.length > 0) {
                Class<?> targetClass = (Class<?>)types[0];
                if (targetClass.isAnnotationPresent(DataSourceAnno.class)) {
                    dataSourceAnno = targetClass.getAnnotation(DataSourceAnno.class);
                    DataSourceContextHolder.setDbType(dataSourceAnno.value());
                }
            }
        }
        if (dataSourceAnno != null) {
            log.info("注解方式选择数据源---" + dataSourceAnno.value().getValue());
        }
    }

    /**
     * 服务类的方法结束后，会清除数据源，此时会变更为默认的数据源
     */
    @After("execution(* com.capol.amis.mapper..*.*(..)) || execution(* com.capol.amis.service..*.*(..)) || @annotation(com.capol.amis.annotation.DataSourceAnno)")
    public void after(JoinPoint point){
        // execution 中配置的是服务实现类 & MyDataSource的包路径
        DataSourceContextHolder.clearDbType();
    }

}
