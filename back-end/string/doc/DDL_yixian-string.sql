/******************************************/
/*   DatabaseName = string   */
/*   TableName = diary   */
/******************************************/
CREATE TABLE `diary` (
  `id` varchar(50) NOT NULL COMMENT 'id',
  `answer` varchar(1000) DEFAULT '' COMMENT '问题对应的答案',
  `question` varchar(100) DEFAULT '' COMMENT '问题内容',
  `date` varchar(15) DEFAULT NULL COMMENT '日记日期',
  `user_id` varchar(50) DEFAULT NULL COMMENT '用户id',
  `content_order` int DEFAULT NULL COMMENT '回答顺序',
  `create_time` datetime(3) DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(3) DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3
;

/******************************************/
/*   DatabaseName = string   */
/*   TableName = diary_answer   */
/******************************************/
CREATE TABLE `diary_answer` (
  `id` varchar(50) NOT NULL,
  `content` varchar(500) DEFAULT NULL,
  `question_id` varchar(50) NOT NULL,
  `user_id` varchar(45) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `date` varchar(15) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `diary_question_id` varchar(255) DEFAULT NULL,
  `diary_question_only_description_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `qid_uid_date` (`user_id`,`question_id`,`date`),
  KEY `FKljesit63onx40by6kxpbyvhoe` (`question_id`),
  KEY `FK562yvgn6kf0jfrmq59bxfgpc2` (`diary_question_id`),
  KEY `FKliwww8poh4gqihlgtnb5fejhl` (`diary_question_only_description_id`),
  CONSTRAINT `FK562yvgn6kf0jfrmq59bxfgpc2` FOREIGN KEY (`diary_question_id`) REFERENCES `diary_question` (`id`),
  CONSTRAINT `FKliwww8poh4gqihlgtnb5fejhl` FOREIGN KEY (`diary_question_only_description_id`) REFERENCES `diary_question` (`id`),
  CONSTRAINT `FKljesit63onx40by6kxpbyvhoe` FOREIGN KEY (`question_id`) REFERENCES `diary_question` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
;

/******************************************/
/*   DatabaseName = string   */
/*   TableName = diary_dialog   */
/******************************************/
CREATE TABLE `diary_dialog` (
  `id` varchar(50) NOT NULL,
  `user_id` varchar(50) NOT NULL COMMENT '用户id',
  `date` char(2) NOT NULL COMMENT '日',
  `month` char(2) NOT NULL COMMENT '月',
  `year` char(4) NOT NULL COMMENT '年',
  `dialogs` varchar(5000) NOT NULL COMMENT '对话内容',
  `create_time` datetime(3) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3
;

/******************************************/
/*   DatabaseName = string   */
/*   TableName = diary_question   */
/******************************************/
CREATE TABLE `diary_question` (
  `id` varchar(50) NOT NULL,
  `description` varchar(200) DEFAULT NULL,
  `user_id` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `in_use` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
;

/******************************************/
/*   DatabaseName = string   */
/*   TableName = notification   */
/******************************************/
CREATE TABLE `notification` (
  `id` varchar(50) NOT NULL,
  `content` varchar(255) NOT NULL,
  `user_id` varchar(50) NOT NULL,
  `create_time` datetime NOT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '0',
  `type` varchar(45) NOT NULL,
  `title` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
;

/******************************************/
/*   DatabaseName = string   */
/*   TableName = project_category   */
/******************************************/
CREATE TABLE `project_category` (
  `id` varchar(255) NOT NULL COMMENT 'id',
  `color` varchar(255) DEFAULT NULL COMMENT '颜色',
  `name` varchar(255) DEFAULT NULL COMMENT '名称',
  `user_id` varchar(255) DEFAULT NULL COMMENT '用户id',
  `create_time` datetime(3) DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(3) DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
;

/******************************************/
/*   DatabaseName = string   */
/*   TableName = sms   */
/******************************************/
CREATE TABLE `sms` (
  `id` varchar(50) NOT NULL DEFAULT '' COMMENT 'id',
  `mobile` varchar(50) NOT NULL COMMENT '手机号',
  `code` char(6) NOT NULL COMMENT '验证码',
  `for_what` char(1) NOT NULL COMMENT '用途|枚举[SmsUseEnum]：REGISTER("R", "注册"), FORGET("F", "忘记密码")',
  `at` datetime(3) NOT NULL COMMENT '生成时间',
  `status` char(1) NOT NULL COMMENT '状态|枚举[SmsStatusEnum]：USED("U", "已使用"), NOT_USED("N", "未使用")',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='短信验证码'
;

/******************************************/
/*   DatabaseName = string   */
/*   TableName = time_entry   */
/******************************************/
CREATE TABLE `time_entry` (
  `id` varchar(45) NOT NULL,
  `description` varchar(200) NOT NULL DEFAULT '',
  `category_id` varchar(45) NOT NULL DEFAULT '0',
  `duration` bigint NOT NULL,
  `user_id` varchar(45) NOT NULL,
  `create_time` datetime(6) DEFAULT NULL,
  `update_time` datetime(6) DEFAULT NULL,
  `start` bigint DEFAULT NULL,
  `end` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
;

/******************************************/
/*   DatabaseName = string   */
/*   TableName = time_entry_category   */
/******************************************/
CREATE TABLE `time_entry_category` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `color` varchar(45) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
;

/******************************************/
/*   DatabaseName = string   */
/*   TableName = time_entry_tmp   */
/******************************************/
CREATE TABLE `time_entry_tmp` (
  `id` varchar(50) NOT NULL,
  `category_id` varchar(45) DEFAULT NULL,
  `user_id` varchar(45) DEFAULT NULL,
  `description` varchar(45) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `start` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
;

/******************************************/
/*   DatabaseName = string   */
/*   TableName = todo   */
/******************************************/
CREATE TABLE `todo` (
  `id` varchar(50) NOT NULL,
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `done` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否完成',
  `priority` varchar(2) NOT NULL COMMENT '优先区域',
  `user_id` varchar(50) NOT NULL COMMENT '用户id',
  `description` varchar(255) NOT NULL DEFAULT '' COMMENT '描述',
  `update_time` datetime(3) NOT NULL COMMENT '更新时间',
  `order_index` bigint DEFAULT NULL COMMENT '顺序',
  `date` varchar(45) DEFAULT NULL COMMENT '对应日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
;

/******************************************/
/*   DatabaseName = string   */
/*   TableName = todo_memo   */
/******************************************/
CREATE TABLE `todo_memo` (
  `id` varchar(50) NOT NULL,
  `user_id` varchar(45) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `all_done` tinyint(1) DEFAULT NULL,
  `year` char(4) DEFAULT NULL,
  `month` char(2) DEFAULT NULL,
  `day` char(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
;

/******************************************/
/*   DatabaseName = string   */
/*   TableName = token   */
/******************************************/
CREATE TABLE `token` (
  `content` varchar(500) DEFAULT '' COMMENT 'token数据内容',
  `user_id` varchar(50) DEFAULT '' COMMENT '用户id',
  `has_logout` tinyint(1) DEFAULT '0' COMMENT '是否已退出登录',
  `id` varchar(50) NOT NULL,
  `create_time` datetime(3) DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3
;

/******************************************/
/*   DatabaseName = string   */
/*   TableName = user   */
/******************************************/
CREATE TABLE `user` (
  `id` varchar(50) NOT NULL COMMENT '用户表id',
  `username` varchar(50) NOT NULL DEFAULT '未命名' COMMENT '用户名',
  `password` varchar(200) NOT NULL COMMENT '用户密码',
  `phone_number` varchar(20) NOT NULL COMMENT '手机号',
  `profile_img` varchar(200) NOT NULL DEFAULT 'avatar.png' COMMENT '头像url',
  `create_time` datetime(3) NOT NULL COMMENT '注册时间',
  `update_time` datetime(3) NOT NULL COMMENT '最后一次更新时间',
  `timezone` varchar(50) NOT NULL DEFAULT 'gggg',
  `time_format` varchar(10) NOT NULL DEFAULT 'HOUR24',
  `goals` varchar(50) DEFAULT '',
  `show_short_entry` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `email_unique` (`phone_number`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户'
;
