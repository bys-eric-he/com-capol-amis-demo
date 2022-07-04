package com.capol.amis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.capol.amis.entity.TemplateFormConfDO;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 业务主题表单配置表 Mapper 接口
 * </p>
 *
 * @author He.Yong
 * @since 2022-06-28
 */
@Mapper
public interface TemplateFormConfMapper extends BaseMapper<TemplateFormConfDO> {

    /**
     * 获取业务主题字段配置信息
     *
     * @param subjectId
     * @return
     */
    List<TemplateFormConfDO> getFieldsBySubjectId(@Param("subjectId") Long subjectId);
}
