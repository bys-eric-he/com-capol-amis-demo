package com.capol.amis.service;

import com.capol.amis.entity.TemplateFormDataDO;

import java.util.List;

/**
 * @author Yaxi.Zhang
 * @since 2022/6/30 19:59
 * desc: 动态表数据转换
 */
public interface ICfgDataConvertService {

    void transformData();

    void transformData4Test(Integer limit);
}
