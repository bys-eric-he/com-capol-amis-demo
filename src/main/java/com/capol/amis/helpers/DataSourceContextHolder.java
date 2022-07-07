package com.capol.amis.helpers;

import com.capol.amis.enums.DBTypeEnum;

/**
 * @author Yaxi.Zhang
 * @since 2022/7/6 11:01
 * desc: 设置、获取数据源
 */
public class DataSourceContextHolder {
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>(); //开启多个线程，每个线程初始化一个数据源
    /**
     * 设置数据源
     */
    public static void setDbType(DBTypeEnum dbTypeEnum) {
        contextHolder.set(dbTypeEnum.getValue());
    }

    /**
     * 取得当前数据源
     */
    public static String getDbType() {
        return contextHolder.get();
    }

    /**
     * 清除上下文数据
     */
    public static void clearDbType() {
        contextHolder.remove();
    }

}
