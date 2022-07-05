package com.capol.amis.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.capol.amis.entity.QaBusinessSubjectDO;
import com.capol.amis.service.IQaBusinessSubjectService;
import com.capol.amis.mapper.QaBusinessSubjectMapper;
import org.springframework.stereotype.Service;

/**
 * @author zhangyaxi
 * @since 2022-07-01 10:57
 * desc: 针对表【cfg_business_subject(业务主题表)】的数据库操作Service实现
 */
@Service
@DS("qa_biz")
public class QaBusinessSubjectServiceImpl extends ServiceImpl<QaBusinessSubjectMapper, QaBusinessSubjectDO>
    implements IQaBusinessSubjectService{

}
