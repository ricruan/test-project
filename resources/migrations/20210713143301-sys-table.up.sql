CREATE TABLE `t_sys_user` (
  `id` varchar(40) NOT NULL COMMENT '主键',
  `username` varchar(100) NOT NULL COMMENT '用户名',
  `password` varchar(200) NOT NULL COMMENT '密码',
  `nickname` varchar(20) NOT NULL COMMENT '昵称',
  `create_user_id` varchar(40) NOT NULL DEFAULT '' COMMENT '创建用户',
  `update_user_id` varchar(40) NOT NULL COMMENT '更新用户',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统用户';

--;;
create table t_sys_role(
  `id` varchar(40) primary key COMMENT '主键',
  `name` varchar(40) not null comment '角色名称',
  `code` varchar(40) not null comment '角色编码',
  `delete_flag` tinyint(1) not null default 0 comment '是否删除',
  `create_time` timestamp not null comment '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci comment='用户角色表';

--;;
create table t_sys_user_role(
  `id` varchar(40) primary key COMMENT '主键',
  `user_id` varchar(40) not null COMMENT '系统用户id',
  `role_id` varchar(40) not null COMMENT '系统角色id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci comment='用户角色关联表';

--;;
create table t_sys_menu(
  `id` varchar(40) primary key COMMENT '主键',
  `parent_id` varchar(40) not null COMMENT '父级id',
  `name` varchar(40) not null comment '菜单名称',
  `code` varchar(40) not null comment '菜单编码',
  `path` varchar(500)  default '' comment '路由地址',
  `icon` varchar(200)  default '' comment '图标',
  `sort` int(11) comment '排序',
  `delete_flag` tinyint(1) not null default 0 comment '是否禁用',
  `create_time` timestamp not null comment '创建时间',
  `menu_type` int(20) DEFAULT 0 NOT NULL COMMENT '0:菜单;1:功能'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci comment='用户菜单表';

--;;
create table t_sys_role_menu(
  `id` varchar(40) primary key COMMENT '主键',
  `role_id` varchar(40) not null COMMENT '系统角色id',
  `menu_id` varchar(40) not null COMMENT '系统菜单id'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci comment='用户角色菜单关联表';

--;; 系统字典表
CREATE TABLE IF NOT EXISTS `t_sys_dict` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `code` varchar(200)  NOT NULL COMMENT '代码',
  `name` varchar(400)  DEFAULT NULL COMMENT '名称',
  `parent_id` int(11) DEFAULT NULL COMMENT '父节点id',
  `type` int(255) DEFAULT NULL COMMENT 'l类型：0：索引，1：字典值',
  `group_code` varchar(255) DEFAULT NULL COMMENT '分组：用在index索引上，将一组索引归为一个大组',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `deleted` tinyint(255) DEFAULT NULL COMMENT '删除标志（0：未删除，1：删除）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统字典表';
