package com.capol.amis.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.capol.amis.annotation.DataSourceAnno;
import com.capol.amis.entity.QaBusinessSubjectDO;
import com.capol.amis.enums.DBTypeEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zhangyaxi
 * @since 2022-07-01 10:57
 * desc: 针对表【cfg_business_subject(业务主题表)】的数据库操作Mapper
 */
@Mapper
@DataSourceAnno(DBTypeEnum.QA_BIZ)
@InterceptorIgnore(dynamicTableName = "true")  // 禁用动态表名
public interface QaBusinessSubjectMapper extends BaseMapper<QaBusinessSubjectDO> {
    // 依据表来源类型查询结果
    @Select("select id from cfg_business_subject where table_source_type = #{code}")
    List<Long> getIdsByTableSourceType(Integer code);


}




