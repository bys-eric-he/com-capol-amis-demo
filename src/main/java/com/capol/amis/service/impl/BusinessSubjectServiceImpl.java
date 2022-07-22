package com.capol.amis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.capol.amis.entity.BusinessSubjectDO;
import com.capol.amis.mapper.BusinessSubjectMapper;
import com.capol.amis.service.IBusinessSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务主题基本信息 服务实现类
 * </p>
 *
 * @author He.Yong
 * @since 2022-06-28
 */
@Service
public class BusinessSubjectServiceImpl
        extends ServiceImpl<BusinessSubjectMapper, BusinessSubjectDO>
        implements IBusinessSubjectService {

    @Autowired
    private BusinessSubjectMapper businessSubjectMapper;

    /**
     * 根据业务主题ID获取表单配置JSON
     *
     * @param subjectId
     * @return
     */
    @Override
    public String getConfigJson(Long subjectId) {
        BusinessSubjectDO businessSubjectDO = businessSubjectMapper.getSubjectConfigJson(subjectId);
        if (businessSubjectDO != null) {
            return businessSubjectDO.getConfigJson();
        }
        return null;
    }
}
