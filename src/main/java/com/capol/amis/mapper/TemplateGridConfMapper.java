package com.capol.amis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.capol.amis.entity.TemplateGridConfDO;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 业务主题列表配置表 Mapper 接口
 * </p>
 *
 * @author He.Yong
 * @since 2022-06-28
 */
@Repository
public interface TemplateGridConfMapper extends BaseMapper<TemplateGridConfDO> {
    /**
     * 获取业务主题字段配置信息
     *
     * @param subjectId
     * @return
     */
    List<TemplateGridConfDO> getFieldsBySubjectId(@Param("subjectId") Long subjectId);
}

