package com.capol.amis.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.capol.amis.entity.FormFieldConfigDO;
import com.capol.amis.entity.FormFieldConfigExtDO;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author Yaxi.Zhang
 * @since 2022/6/30 11:27
 * desc: 查看动态表
 */
@Repository
@DS("qa_biz")
public interface CfgDynamicDataMapper {

    // 查询动态表数据
    @Select("select * from ${tbl}")
    List<Map<String, Object>> getListMapDatas(String tbl);

    // 动态表信息
    @Select("SELECT T0.enterprise_id, T1.*\n" +
            "FROM (SELECT id, enterprise_id FROM cfg_business_subject WHERE table_source_type = #{tableSourceType}) T0\n" +
            "         JOIN cfg_form_field_config T1 ON T0.id = subject_id")
    List<FormFieldConfigExtDO> getFieldList(Integer tableSourceType);


}
