package com.capol.amis.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.capol.amis.entity.FormFieldConfigDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zhangyaxi
 * @since 2022-07-01 10:34
 * desc: 针对表【cfg_form_field_config(表单字段配置表)】的数据库操作Mapper
 */
@Repository
@DS("qa_biz")
public interface FormFieldConfigMapper extends BaseMapper<FormFieldConfigDO> {
    // 依据subject_id查找表名
    @Select("select distinct table_name from cfg_form_field_config " +
            "where subject_id = #{subjectId}")
    List<String> getTblsFromSubjectId(Long subjectId);


}




