package com.capol.amis.annotation;

import com.capol.amis.enums.DBTypeEnum;

import java.lang.annotation.*;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/6 11:00
 * desc: 数据源注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSourceAnno {
    DBTypeEnum value() default DBTypeEnum.AMIS_DEMO;
}
