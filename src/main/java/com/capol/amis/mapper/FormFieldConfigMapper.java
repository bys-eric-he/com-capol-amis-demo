package com.capol.amis.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.capol.amis.annotation.DataSourceAnno;
import com.capol.amis.entity.FormFieldConfigDO;
import com.capol.amis.enums.DBTypeEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zhangyaxi
 * @since 2022-07-01 10:34
 * desc: 针对表【cfg_form_field_config(表单字段配置表)】的数据库操作Mapper
 */
@Mapper
@DataSourceAnno(DBTypeEnum.QA_BIZ)
@InterceptorIgnore(dynamicTableName = "true")  // 禁用动态表名
public interface FormFieldConfigMapper extends BaseMapper<FormFieldConfigDO> {
    // 依据subject_id查找表名
    @Select("select distinct table_name from cfg_form_field_config " +
            "where subject_id = #{subjectId}")
    List<String> getTblsFromSubjectId(Long subjectId);

}




