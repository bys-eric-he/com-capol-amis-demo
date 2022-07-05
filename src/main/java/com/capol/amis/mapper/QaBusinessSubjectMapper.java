package com.capol.amis.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.capol.amis.entity.QaBusinessSubjectDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zhangyaxi
 * @since 2022-07-01 10:57
 * desc: 针对表【cfg_business_subject(业务主题表)】的数据库操作Mapper
 */
@Repository
@DS("qa_biz")
public interface QaBusinessSubjectMapper extends BaseMapper<QaBusinessSubjectDO> {
    // 依据表来源类型查询结果
    @Select("select id from cfg_business_subject where table_source_type = #{code}")
    List<Long> getIdsByTableSourceType(Integer code);


}




