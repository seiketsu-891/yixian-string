package com.star.string.entity;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
public class Token {

	/**
	 * token数据内容
	 */
	private String content;

	/**
	 * 用户id
	 */
	private String userId;

	/**
	 * 是否已退出登录
	 */
	private Boolean hasLogout;

	/**
	 * 主键id
	 */
	@Id
	private String id;


	/**
	 * 创建时间
	 */
	private Date createTime;


}
