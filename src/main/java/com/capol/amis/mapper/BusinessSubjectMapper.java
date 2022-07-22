package com.capol.amis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.capol.amis.entity.BusinessSubjectDO;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 业务主题基本信息 Mapper 接口
 * </p>
 *
 * @author He.Yong
 * @since 2022-06-28
 */
@Mapper
public interface BusinessSubjectMapper extends BaseMapper<BusinessSubjectDO> {
    /**
     * 根据业务主题ID获取该业务主题表单配置JSON
     *
     * @param subjectId
     * @return
     */
    BusinessSubjectDO getSubjectConfigJson(@Param("subjectId") Long subjectId);
}

