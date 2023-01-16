package com.star.string.entity;


import javax.persistence.Entity;
import lombok.Data;
import javax.persistence.Id;

@Data
@Entity
public class Diary {

	@Id
	/**
	 * id
	 */
	private String id;

	/**
	 * 问题对应的答案
	 */
	private String answer;

	/**
	 * 问题内容
	 */
	private String question;

	/**
	 * 日记日期
	 */
	private String date;

	/**
	 * 用户id
	 */
	private String userId;

	/**
	 * 回答顺序
	 */
	private Integer contentOrder;

	/**
	 * 创建时间
	 */
	private java.util.Date createTime;


	/**
	 * 更新时间
	 */
	private java.util.Date updateTime;


}
