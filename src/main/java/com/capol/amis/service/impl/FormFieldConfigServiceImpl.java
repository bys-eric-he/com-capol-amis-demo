package com.capol.amis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.capol.amis.annotation.DataSourceAnno;
import com.capol.amis.entity.FormFieldConfigDO;
import com.capol.amis.enums.DBTypeEnum;
import com.capol.amis.service.IFormFieldConfigService;
import com.capol.amis.mapper.FormFieldConfigMapper;
import org.springframework.stereotype.Service;

/**
 * @author zhangyaxi
 * @since 2022-07-01 10:34
 * desc: 针对表【cfg_form_field_config(表单字段配置表)】的数据库操作Service实现
 */
@Service
@DataSourceAnno(DBTypeEnum.QA_BIZ)
public class FormFieldConfigServiceImpl extends ServiceImpl<FormFieldConfigMapper, FormFieldConfigDO>
    implements IFormFieldConfigService{

}
