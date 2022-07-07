
/**
 * 创建数据库db_amis_demo
 */
CREATE DATABASE IF NOT EXISTS `db_amis_demo` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;


SELECT *FROM cfg_business_subject_324225698253233659;

DELETE FROM cfg_business_subject_324225698253233659;

SELECT *FROM t_template_form_conf_324225698253233659;

DELETE FROM t_template_form_conf_324225698253233659;

SELECT *FROM t_template_form_data_324225698253233659;

SELECT *FROM `t_template_grid_conf_324225698253233659`;

DELETE FROM `t_template_grid_conf_324225698253233659`;

SELECT *FROM t_template_grid_data_324225698253233659;

/*
 * 业务主题基本信息
 * */
DROP TABLE IF EXISTS `cfg_business_subject_324225698253233659`;

CREATE TABLE `cfg_business_subject_324225698253233659` (
	`id` bigint(20) NOT NULL COMMENT '主键ID',
	`enterprise_id` bigint(20) NOT NULL COMMENT '企业ID',
	`project_id` bigint(20) NOT NULL COMMENT '项目ID',
	`subject_id` bigint(20) NOT NULL COMMENT '业务主题ID',
	`subject_name` varchar(50) DEFAULT NULL COMMENT '业务主题名称',
	`config_json` mediumtext COMMENT 'JSON配置信息',
	`status` tinyint(1) DEFAULT '1' COMMENT '1：正常，0：已删除',
	`creator` varchar(20) DEFAULT NULL COMMENT '创建人',
	`creator_id` bigint(20) DEFAULT NULL COMMENT '创建人id',
	`created_host_ip` varchar(50) DEFAULT NULL COMMENT '创建人IP',
	`last_operator` varchar(20) DEFAULT NULL COMMENT '最后操作人 ',
	`last_operator_id` bigint(20) DEFAULT NULL COMMENT '最后操作人id ',
	`update_host_ip` varchar(50) DEFAULT NULL COMMENT '更新人IP ',
	`create_time` datetime DEFAULT NOW() COMMENT '创建时间',
	`update_time` datetime DEFAULT NOW() COMMENT '更新时间',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="业务主题基本信息";


/**
 * 业务主题表单配置表
 */
DROP TABLE IF EXISTS `t_template_form_conf_324225698253233659`;

CREATE TABLE `t_template_form_conf_324225698253233659` (
	`id` bigint(20) NOT NULL COMMENT '主键ID',
	`enterprise_id` bigint(20) NOT NULL COMMENT '企业ID',
	`project_id` bigint(20) NOT NULL COMMENT '项目ID',
	`subject_id` bigint(20) DEFAULT NULL COMMENT '业务主题ID',
	`field_key` varchar(200) DEFAULT NULL COMMENT '字段标识',
	`field_alias` varchar(200) DEFAULT NULL COMMENT '字段别名',
	`field_name` varchar(200) DEFAULT NULL COMMENT '字段名称',
	`field_type` varchar(20) DEFAULT NULL COMMENT '字段类型',
	`field_order` int(10) DEFAULT 0 COMMENT '字段顺序',
	`status` tinyint(1) DEFAULT '1' COMMENT '1：正常，0：已删除',
	`creator` varchar(20) DEFAULT NULL COMMENT '创建人',
	`creator_id` bigint(20) DEFAULT NULL COMMENT '创建人id',
	`created_host_ip` varchar(50) DEFAULT NULL COMMENT '创建人IP',
	`last_operator` varchar(20) DEFAULT NULL COMMENT '最后操作人 ',
	`last_operator_id` bigint(20) DEFAULT NULL COMMENT '最后操作人id ',
	`update_host_ip` varchar(50) DEFAULT NULL COMMENT '更新人IP ',
	`create_time` datetime DEFAULT NOW() COMMENT '创建时间',
	`update_time` datetime DEFAULT NOW() COMMENT '更新时间',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="业务主题表单配置表";



/**
 * 业务主题列表配置表
 */
DROP TABLE IF EXISTS `t_template_grid_conf_324436777253208064`;

CREATE TABLE `t_template_grid_conf_324225698253233659` (
	`id` bigint(20) NOT NULL COMMENT '主键ID',
	`enterprise_id` bigint(20) NOT NULL COMMENT '企业ID',
	`project_id` bigint(20) NOT NULL COMMENT '项目ID',
	`subject_id` bigint(20) DEFAULT NULL COMMENT '业务主题ID',
	`table_name` varchar(200) DEFAULT NULL COMMENT '列表名称',
	`field_key` varchar(200) DEFAULT NULL COMMENT '字段标识',
	`field_alias` varchar(200) DEFAULT NULL COMMENT '字段别名',
	`field_name` varchar(200) DEFAULT NULL COMMENT '字段名称',
	`field_type` varchar(20) DEFAULT NULL COMMENT '字段类型',
	`field_order` int(10) DEFAULT 0 COMMENT '字段顺序',
	`status` tinyint(1) DEFAULT '1' COMMENT '1：正常，0：已删除',
	`creator` varchar(20) DEFAULT NULL COMMENT '创建人',
	`creator_id` bigint(20) DEFAULT NULL COMMENT '创建人id',
	`created_host_ip` varchar(50) DEFAULT NULL COMMENT '创建人IP',
	`last_operator` varchar(20) DEFAULT NULL COMMENT '最后操作人 ',
	`last_operator_id` bigint(20) DEFAULT NULL COMMENT '最后操作人id ',
	`update_host_ip` varchar(50) DEFAULT NULL COMMENT '更新人IP ',
	`create_time` datetime DEFAULT NOW() COMMENT '创建时间',
	`update_time` datetime DEFAULT NOW() COMMENT '更新时间',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="业务主题列表配置表";



/**
 * 业务主题表单数据表
 */
DROP TABLE IF EXISTS `t_template_form_data_324225698253233659`;

CREATE TABLE `t_template_form_data_324225698253233659` (
	`id` bigint(20) NOT NULL COMMENT '主键ID',
	`template_id` bigint(20) NOT NULL COMMENT '表单配置ID',
	`row_id` bigint(20) NOT NULL COMMENT '数据批次ID',
	`enterprise_id` bigint(20) NOT NULL COMMENT '企业ID',
	`project_id` bigint(20) NOT NULL COMMENT '项目ID',
	`subject_id` bigint(20) DEFAULT NULL COMMENT '业务主题ID',
	`field_key` varchar(200) DEFAULT NULL COMMENT '字段标识',
	`field_alias` varchar(200) DEFAULT NULL COMMENT '字段别名',
	`field_name` varchar(200) DEFAULT NULL COMMENT '字段名称',
	`field_type` varchar(20) DEFAULT NULL COMMENT '字段类型',
	`field_string_value` varchar(500) DEFAULT NULL COMMENT '字符串值',
	`field_number_value` bigint(50) DEFAULT NULL COMMENT '数字值',
	`field_text_value` text DEFAULT NULL COMMENT '描述性值',
	`field_hash_value` bigint(100) DEFAULT '1' COMMENT '值hash',
	`status` tinyint(1) DEFAULT '1' COMMENT '1：正常，0：已删除',
	`creator` varchar(20) DEFAULT NULL COMMENT '创建人',
	`creator_id` bigint(20) DEFAULT NULL COMMENT '创建人id',
	`created_host_ip` varchar(50) DEFAULT NULL COMMENT '创建人IP',
	`last_operator` varchar(20) DEFAULT NULL COMMENT '最后操作人 ',
	`last_operator_id` bigint(20) DEFAULT NULL COMMENT '最后操作人id ',
	`update_host_ip` varchar(50) DEFAULT NULL COMMENT '更新人IP ',
	`create_time` datetime DEFAULT NOW() COMMENT '创建时间',
	`update_time` datetime DEFAULT NOW() COMMENT '更新时间',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="业务主题表单数据表";



/**
 * 业务主题列表数据表
 */
DROP TABLE IF EXISTS `t_template_grid_data_324225698253233659`;

CREATE TABLE `t_template_grid_data_324225698253233659` (
	`id` bigint(20) NOT NULL COMMENT '主键ID',
	`template_id` bigint(20) NOT NULL COMMENT '表单配置ID',
	`form_row_id` bigint(20) NOT NULL COMMENT '表单数据行ID',
	`row_id` bigint(20) NOT NULL COMMENT '数据批次ID',
	`enterprise_id` bigint(20) NOT NULL COMMENT '企业ID',
	`project_id` bigint(20) NOT NULL COMMENT '项目ID',
	`subject_id` bigint(20) DEFAULT NULL COMMENT '业务主题ID',
	`table_name` varchar(200) DEFAULT NULL COMMENT '列表名称',
	`field_key` varchar(200) DEFAULT NULL COMMENT '字段标识',
	`field_alias` varchar(200) DEFAULT NULL COMMENT '字段别名',
	`field_name` varchar(200) DEFAULT NULL COMMENT '字段名称',
	`field_type` varchar(20) DEFAULT NULL COMMENT '字段类型',
	`field_string_value` varchar(500) DEFAULT NULL COMMENT '字符串值',
	`field_number_value` bigint(50) DEFAULT NULL COMMENT '数字值',
	`field_text_value` text DEFAULT NULL COMMENT '描述性值',
	`field_hash_value` bigint(100) DEFAULT '1' COMMENT '值hash',
	`status` tinyint(1) DEFAULT '1' COMMENT '1：正常，0：已删除',
	`creator` varchar(20) DEFAULT NULL COMMENT '创建人',
	`creator_id` bigint(20) DEFAULT NULL COMMENT '创建人id',
	`created_host_ip` varchar(50) DEFAULT NULL COMMENT '创建人IP',
	`last_operator` varchar(20) DEFAULT NULL COMMENT '最后操作人 ',
	`last_operator_id` bigint(20) DEFAULT NULL COMMENT '最后操作人id ',
	`update_host_ip` varchar(50) DEFAULT NULL COMMENT '更新人IP ',
	`create_time` datetime DEFAULT NOW() COMMENT '创建时间',
	`update_time` datetime DEFAULT NOW() COMMENT '更新时间',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT="业务主题列表数据表";


/**
 * 数据集表
 */
DROP TABLE IF EXISTS `t_dataset_324225698253233659`;

CREATE TABLE `t_dataset_324225698253233659` (
  `id` bigint(20) NOT NULL COMMENT '主键Id',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父主键Id',
  `enterprise_id` bigint(20) NOT NULL COMMENT '企业Id',
  `project_id` bigint(20) NOT NULL COMMENT '项目ID',
  `subject_key` varchar(120) DEFAULT NULL COMMENT '业务主题key',
  `subject_id` bigint(20) DEFAULT NULL COMMENT '业务主题Id',
  `table_name` varchar(100) DEFAULT NULL COMMENT '主表名',
  `table_source_type` tinyint(1) DEFAULT NULL COMMENT '表来源类型(1宽表;2用户自定义表)',
  `dataset_name` varchar(100) NOT NULL COMMENT '数据集名称',
  `level` tinyint(1) NOT NULL COMMENT '层级(1分类;2数据集)',
  `dataset_sql` text COMMENT '数据结果集SQL',
  `advance_setting` tinyint(1) DEFAULT NULL COMMENT '是否设置高级配置',
  `order_no` int(10) DEFAULT NULL COMMENT '序号',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态 1:正常，0:删除',
  `creator` varchar(20) NOT NULL COMMENT '创建人',
  `creator_id` bigint(20) NOT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `created_host_ip` varchar(50) NOT NULL COMMENT '创建人IP',
  `last_operator` varchar(100) DEFAULT NULL COMMENT '最后操作人',
  `last_operator_id` bigint(20) DEFAULT NULL COMMENT '最后操作人id',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_host_ip` varchar(50) DEFAULT NULL COMMENT '更新人IP',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据集表';

/**
 * 数据集字段表
 */
DROP TABLE IF EXISTS `t_dataset_324225698253233659`;

CREATE TABLE `t_dataset_field_324225698253233659` (
  `id` bigint(20) NOT NULL COMMENT '主键Id',
  `dataset_id` bigint(20) NOT NULL COMMENT '数据集主键id',
  `subject_key` varchar(120) NOT NULL COMMENT '业务主题key',
  `subject_id` bigint(20) NOT NULL COMMENT '业务主题Id',
  `table_name` varchar(100) NOT NULL COMMENT '主表名',
  `table_name_alias` varchar(100) NOT NULL COMMENT '表别称(给报表制作使用),规则是T拼最大下标',
  `table_source_type` tinyint(1) NOT NULL COMMENT '表来源类型(1宽表;2用户自定义表)',
  `field_id` bigint(20) NOT NULL COMMENT '字段主键id',
  `field_name` varchar(100) NOT NULL COMMENT '字段名',
  `field_alias` varchar(100) NOT NULL COMMENT '字段别名(用户定义)',
  `field_name_alias` varchar(100) NOT NULL COMMENT '字段别称(给报表制作使用),规则是F拼最大下标',
  `field_type_num` tinyint(1) NOT NULL COMMENT '字段类型(1文本;2数值;3日期)(用户定义)',
  `show_type` tinyint(1) NOT NULL COMMENT '显示类型(1显示;0不显示)',
  `ori_field_type` varchar(100) NOT NULL COMMENT '原始字段类型',
  `order_no` int(10) DEFAULT NULL COMMENT '序号',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态 1:正常，0:删除',
  `creator` varchar(20) NOT NULL COMMENT '创建人',
  `creator_id` bigint(20) NOT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `created_host_ip` varchar(50) NOT NULL COMMENT '创建人IP',
  `last_operator` varchar(100) DEFAULT NULL COMMENT '最后操作人',
  `last_operator_id` bigint(20) DEFAULT NULL COMMENT '最后操作人id',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_host_ip` varchar(50) DEFAULT NULL COMMENT '更新人IP',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据集字段表';


/**
 * 数据集关联表
 */
DROP TABLE IF EXISTS `t_dataset_union_324225698253233659`;

CREATE TABLE `t_dataset_union_324225698253233659` (
  `id` bigint(20) NOT NULL COMMENT '主键Id',
  `dataset_id` bigint(20) NOT NULL COMMENT '数据集主键id',
  `source_subject_key` varchar(120) NOT NULL COMMENT '来源业务主题key',
  `source_subject_id` bigint(20) NOT NULL COMMENT '来源业务主题Id',
  `source_table_name` varchar(100) NOT NULL COMMENT '来源表名',
  `source_field_id` bigint(20) NOT NULL COMMENT '来源字段主键id',
  `target_subject_key` varchar(120) NOT NULL COMMENT '目标业务主题key',
  `target_subject_id` bigint(20) NOT NULL COMMENT '目标业务主题Id',
  `target_table_name` varchar(100) NOT NULL COMMENT '目标表名',
  `target_field_id` bigint(20) NOT NULL COMMENT '目标字段主键id',
  `union_type` tinyint(1) NOT NULL COMMENT '关联类型(1左关联)',
  `target_table_name_alias` varchar(100) NOT NULL COMMENT '目标表别称(给报表制作使用),规则是T拼最大下标',
  `order_no` int(10) DEFAULT NULL COMMENT '序号',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态 1:正常，0:删除',
  `creator` varchar(20) NOT NULL COMMENT '创建人',
  `creator_id` bigint(20) NOT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `created_host_ip` varchar(50) NOT NULL COMMENT '创建人IP',
  `last_operator` varchar(100) DEFAULT NULL COMMENT '最后操作人',
  `last_operator_id` bigint(20) DEFAULT NULL COMMENT '最后操作人id',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_host_ip` varchar(50) DEFAULT NULL COMMENT '更新人IP',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据集关联表';



/**
 * 报表配置表
 */
DROP TABLE IF EXISTS `t_report_config_324225698253233659`;

CREATE TABLE `t_report_config_324225698253233659` (
  `id` bigint(20) NOT NULL COMMENT '主键Id',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父主键Id',
  `report_name` varchar(50) DEFAULT NULL,
  `report_type` tinyint(2) NOT NULL COMMENT '报表类型(1平台报表;2个人报表;3企业报表)',
  `config_type` tinyint(2) NOT NULL COMMENT '配置类型(1表单)',
  `report_level` tinyint(2) NOT NULL COMMENT '报表层级',
  `report_path` varchar(200) DEFAULT NULL COMMENT '报表路径',
  `menu_icon` varchar(50) DEFAULT NULL COMMENT '图标',
  `order_no` int(11) DEFAULT NULL COMMENT '序号',
  `config_json` text COMMENT '组件配置数据JSON字符串',
  `enterprise_id` bigint(20) NOT NULL COMMENT '企业Id',
  `status` tinyint(2) DEFAULT NULL COMMENT '数据状态（0-已删除，1-正常，10-草稿，20-已提交，30-已发布）',
  `creator` varchar(100) NOT NULL COMMENT '创建人',
  `created_host_ip` varchar(50) NOT NULL COMMENT '创建主机ID',
  `creator_id` bigint(20) NOT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `last_operator` varchar(100) DEFAULT NULL COMMENT '最后创建人',
  `update_host_ip` varchar(50) DEFAULT NULL COMMENT '最后修改主机ID',
  `last_operator_id` bigint(20) DEFAULT NULL COMMENT '最后创建人id',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报表配置表';


/**
 * 报表图表配置表
 */
DROP TABLE IF EXISTS `t_report_chart_config_324225698253233659`;

CREATE TABLE `t_report_chart_config_324225698253233659` (
  `id` bigint(20) NOT NULL COMMENT '主键Id',
  `report_id` bigint(20) NOT NULL COMMENT '报表id',
  `dataset_id` bigint(20) NOT NULL COMMENT '数据集id，关联 t_dataset 表的id',
  `enterprise_id` bigint(20) DEFAULT NULL COMMENT '企业id，保留字段',
  `chart_key` varchar(50) NOT NULL COMMENT '组件唯一标识，前端生成',
  `chart_type` tinyint(4) NOT NULL COMMENT '组件类型：1-柱状图，2-折线图',
  `field_name` varchar(50) NOT NULL COMMENT '字段名称，规则是F_xx',
  `field_type` varchar(50) DEFAULT NULL COMMENT '字段类型，保留字段',
  `order_no` tinyint(4) NOT NULL COMMENT '序号',
  `sort_type` tinyint(1) DEFAULT NULL COMMENT '排序类型：1-升序asc，2-降序desc',
  `select_type` tinyint(1) NOT NULL COMMENT '查询条件类型：1-x轴字段，2-y轴字段，3-为排序字段',
  `echarts_json` mediumtext  COMMENT '图表配置JSON数据结构',
  `top_n` bigint(10) DEFAULT '0' COMMENT '图表返回指定数据条数',
  `theme` varchar(50) DEFAULT NULL COMMENT '图表主题（用于图表配置的回显）',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态 1:正常，0:删除',
  `creator` varchar(20) NOT NULL COMMENT '创建人',
  `creator_id` bigint(20) NOT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `created_host_ip` varchar(50) NOT NULL COMMENT '创建人IP',
  `last_operator` varchar(100) DEFAULT NULL COMMENT '最后操作人',
  `last_operator_id` bigint(20) DEFAULT NULL COMMENT '最后操作人id',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_host_ip` varchar(50) DEFAULT NULL COMMENT '更新人IP',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报表图表配置表';


/**
 * 报表配置表
 */
DROP TABLE IF EXISTS `t_report_field_config_324225698253233659`;

CREATE TABLE `t_report_field_config_324225698253233659` (
  `id` bigint(20) NOT NULL COMMENT '主键Id',
  `field_id` bigint(20) DEFAULT NULL COMMENT '绑定字段id,关联t_ware_table_field 表的id',
  `statistics_field_id` bigint(20) DEFAULT NULL COMMENT '统计范围字段id,关联t_ware_table_field 表的id',
  `condition_field_id` bigint(20) DEFAULT NULL COMMENT '条件范围字段id,关联t_ware_table_field 表的id',
  `object_field_name` varchar(100) DEFAULT NULL COMMENT '对象绑定字段名称',
  `field_alias` varchar(100) DEFAULT NULL COMMENT '表头展示，字段别名',
  `form_field_binding_type` varchar(20) DEFAULT NULL COMMENT '表单字段绑定类型（text-纯文本  tpl-模板  image-图片  date-日期  progress-进度  status-状态   mapping-映射  operation-操作栏）',
  `field_type` tinyint(2) NOT NULL COMMENT '字段类型 1-show(展示字段)  2-group by(分组字段)  3-sum（求和） 4-count（计数） 5-AVG(平均数) 6-MAX(最大值)  7-MIN（最小值） 8-query(查询字段)',
  `data_type` varchar(20) DEFAULT NULL COMMENT '数据类型',
  `condition_value` text COMMENT '条件值',
  `mutil_param_id` bigint(20) DEFAULT NULL COMMENT '分组取值中取值范围字段的id',
  `order_no` tinyint(4) NOT NULL COMMENT '序号',
  `status` tinyint(1) NOT NULL COMMENT '状态 1:正常，0:删除',
  `creator` varchar(100) NOT NULL COMMENT '创建人',
  `created_host_ip` varchar(50) NOT NULL COMMENT '创建主机ID',
  `creator_id` bigint(20) NOT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `last_operator` varchar(100) DEFAULT NULL COMMENT '最后操作人',
  `update_host_ip` varchar(50) DEFAULT NULL COMMENT '更新人IP',
  `last_operator_id` bigint(20) DEFAULT NULL COMMENT '最后创建人id',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `dataset_id` bigint(20) NOT NULL COMMENT '数据集主键id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报表配置表';