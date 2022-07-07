package com.capol.amis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.capol.amis.entity.TemplateFormDataDO;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 业务主题表单数据表 Mapper 接口
 * </p>
 *
 * @author He.Yong
 * @since 2022-06-28
 */
@Mapper
public interface TemplateFormDataMapper extends BaseMapper<TemplateFormDataDO> {

    /**
     * 根据业务主题Id查询主表数据
     *
     * @param subjectId
     * @return
     */
    List<TemplateFormDataDO> queryFromDataBySubjectId(@Param("subjectId") Long subjectId);
}
