CREATE TABLE `t_app` (
  `app_id` VARCHAR(40) NOT NULL COMMENT '主键',
  `app_name` VARCHAR(40) NOT NULL COMMENT 'app名称',
  `wx_app_id` VARCHAR(40) NOT NULL COMMENT '小程序id',
  `wx_app_secret` VARCHAR(40) NOT NULL COMMENT '小程序密钥',
  `wx_mch_id` VARCHAR(40) DEFAULT NULL COMMENT '小程序商户id',
  `wx_api_secret` VARCHAR(40) DEFAULT NULL COMMENT '小程序支付API密钥',
  `wx_cert_path` VARCHAR(100) DEFAULT NULL COMMENT '小程序支付证书路径',
  `wx_cert_pwd` VARCHAR(20) DEFAULT NULL COMMENT '小程序支付证书密码',
  `wx_pay_body` VARCHAR(100) DEFAULT NULL COMMENT '微信支付描述',
  `wx_notify_url` VARCHAR(100) DEFAULT NULL COMMENT '微信支付回调url',
  `ai_app_id` VARCHAR(40) DEFAULT NULL COMMENT '智量id',
  `ai_app_secret` VARCHAR(40) DEFAULT NULL COMMENT '智量secret',
  `qiniu_ak` VARCHAR(40) DEFAULT NULL COMMENT '七牛accessToken',
  `qiniu_sk` VARCHAR(40) DEFAULT NULL COMMENT '七牛secretToken',
  `qiniu_base_url` VARCHAR(100) DEFAULT NULL COMMENT '七牛基础url地址',
  `qiniu_bucket` VARCHAR(80) DEFAULT NULL COMMENT '七牛BUCKET',
  `remark` VARCHAR(400) DEFAULT NULL COMMENT '备注',
  `delete_flag` VARCHAR(4) DEFAULT '0' COMMENT '删除标记 0：未删除 1：已删除',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user_id` VARCHAR(40) DEFAULT NULL COMMENT '创建人id',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `update_user_id` VARCHAR(40) DEFAULT NULL COMMENT '最后修改人id',
  `wx_certificate_path` VARCHAR(600) DEFAULT NULL COMMENT '微信证书存放路径',
  PRIMARY KEY (`app_id`) USING BTREE
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COMMENT='小程序应用表';

--;;
INSERT  INTO `t_app`(`app_id`,`app_name`,`wx_app_id`,`wx_app_secret`,`wx_mch_id`,`wx_api_secret`,`wx_cert_path`,`wx_cert_pwd`,`wx_pay_body`,`wx_notify_url`,`ai_app_id`,`ai_app_secret`,`qiniu_ak`,`qiniu_sk`,`qiniu_base_url`,`qiniu_bucket`,`remark`,`delete_flag`,`create_time`,`create_user_id`,`update_time`,`update_user_id`,`wx_certificate_path`) VALUES
('1','裁圣私服定制','wx069c4d7574501e36','dd83fee0e82ebf4d7218f115d49ce5ed','1549627571','27B937B5FB4541EA026826EDF0A5641D',NULL,'Hczt@001','裁圣-服饰','https://wxqrcode.3vyd.com/api/callback','cshc8539693774548274','KIiWvPtV6Tpup9J6NGuXOcnvG4F8zpGX','USVsxU0jt3REMPZFg5BwYO_lHWfI-SWt5UEfaaQt','U6UomjXwBerfBGadBuArCoY8jkVe7woC8GvBQ6Ig','https://hcxcx.shcaijiang.com','applet','裁圣小程序','0','2019-10-15 11:05:30',NULL,'2019-10-15 11:05:30',NULL,'/opt/mall/pkg/wxcert/hc_apiclient_cert.p12');


--;;
CREATE TABLE `t_user` (
  `user_id` VARCHAR(40) NOT NULL COMMENT '主键',
  `app_id` VARCHAR(40) DEFAULT NULL COMMENT '所属应用',
  `wx_open_id` VARCHAR(40) DEFAULT NULL COMMENT '微信open_id',
  `wx_union_id` VARCHAR(40) DEFAULT NULL COMMENT '微信union_id',
  `wx_session_key` VARCHAR(40) DEFAULT NULL COMMENT '微信会话密钥',
  `wx_nick_name` VARCHAR(40) DEFAULT NULL COMMENT '微信昵称',
  `wx_gender` VARCHAR(4) DEFAULT NULL COMMENT '微信性别 m：男 f：女',
  `wx_country` VARCHAR(40) DEFAULT NULL COMMENT '国家',
  `wx_province` VARCHAR(40) DEFAULT NULL COMMENT '省份',
  `wx_city` VARCHAR(40) DEFAULT NULL COMMENT '城市',
  `wx_head_image_url` VARCHAR(400) DEFAULT NULL COMMENT '微信头像',
  `wx_mobile` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `wx_language` VARCHAR(40) DEFAULT NULL COMMENT '语言',
  `remark` VARCHAR(400) DEFAULT NULL COMMENT '备注',
  `delete_flag` VARCHAR(4) DEFAULT '0' COMMENT '删除标记 0：未删除 1：已删除',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user_id` VARCHAR(40) DEFAULT NULL COMMENT '创建人id',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `update_user_id` VARCHAR(40) DEFAULT NULL COMMENT '最后修改人id',
  `member_level_id` VARCHAR(40) DEFAULT '1' COMMENT '会员等级id',
  `use_amount` DECIMAL(12,2) DEFAULT NULL COMMENT '可用金额',
  `member_end_date` TIMESTAMP NULL DEFAULT NULL COMMENT '会员等级结束时间',
  `lock_use_amount` DECIMAL(12,2) DEFAULT NULL COMMENT '冻结金额',
  `member_start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '会员等级开始时间',
  `user_name` VARCHAR(100) DEFAULT NULL COMMENT '用户姓名',
  `crm_customer_no` VARCHAR(100) DEFAULT '' COMMENT 'crm用户编码',
  `birthday` VARCHAR(40) DEFAULT NULL COMMENT '用户生日',
  `recommender_employee_no` VARCHAR(50) DEFAULT NULL COMMENT '推荐员工编码',
  `store_id` VARCHAR(40) DEFAULT NULL COMMENT '所属门店ID',
  `store_code` VARCHAR(40) DEFAULT NULL COMMENT '所属门店编码',
  `recommend_user_id` VARCHAR(40) DEFAULT NULL COMMENT '推荐用户ID',
  `qrcode_url` VARCHAR(100) DEFAULT NULL COMMENT '推广二维码地址',
  `role_code` TINYINT(4) DEFAULT NULL COMMENT '所属角色 0: 用户 1: 店员',
  `recommender_no` VARCHAR(40) DEFAULT NULL COMMENT '推荐人crm号',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

--;;
INSERT  INTO `t_user`(`user_id`,`app_id`,`wx_open_id`,`wx_union_id`,`wx_session_key`,`wx_nick_name`,`wx_gender`,`wx_country`,`wx_province`,`wx_city`,`wx_head_image_url`,`wx_mobile`,`wx_language`,`remark`,`delete_flag`,`create_time`,`create_user_id`,`update_time`,`update_user_id`,`member_level_id`,`use_amount`,`member_end_date`,`lock_use_amount`,`member_start_date`,`user_name`,`crm_customer_no`,`birthday`,`recommender_employee_no`,`store_id`,`store_code`,`recommend_user_id`,`qrcode_url`,`role_code`,`recommender_no`) VALUES
('269862775243751424','1','omoIR5aFIw6gL2YOnGjBmNk_9yrQ',NULL,'ZEmbLa2I65+3Qf1p4GMh+Q==',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'0','2020-01-15 16:18:19',NULL,'2020-01-15 16:18:19',NULL,'3',0.05,'2021-01-14 17:12:49',NULL,'2020-01-15 17:12:49',NULL,'',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
