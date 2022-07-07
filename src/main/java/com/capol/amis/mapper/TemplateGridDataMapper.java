package com.capol.amis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.capol.amis.entity.TemplateGridDataDO;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 业务主题列表数据表 Mapper 接口
 * </p>
 *
 * @author He.Yong
 * @since 2022-06-28
 */
@Mapper
public interface TemplateGridDataMapper extends BaseMapper<TemplateGridDataDO> {

    /**
     * 根据业务主题ID获取业务主题列表数据
     *
     * @param subjectId
     * @return
     */
    List<TemplateGridDataDO> queryGridDataBySubjectId(@Param("subjectId") Long subjectId);
}

