package com.star.string.entity;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class User {

	@Id
	/**
	 * 用户表id
	 */
	private String id;

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 用户密码
	 */
	private String password;

	/**
	 * 手机号
	 */
	private String phoneNumber;

	/**
	 * 头像url
	 */
	private String profileImg;

	/**
	 * 注册时间
	 */
	private java.util.Date createTime;

	/**
	 * 最后一次更新时间
	 */
	private java.util.Date updateTime;

	private String timezone;

	private String timeFormat;

	private String goals;

	private Boolean showShortEntry;


}
