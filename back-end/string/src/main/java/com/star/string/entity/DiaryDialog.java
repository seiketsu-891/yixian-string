package com.star.string.entity;


import javax.persistence.Entity;
import lombok.Data;
import javax.persistence.Id;

@Data
@Entity
public class DiaryDialog {

	/**
	 * 主键id
	 */
	@Id
	private String id;

	/**
	 * 用户id
	 */
	private String userId;

	/**
	 * 日
	 */
	private String date;

	/**
	 * 月
	 */
	private String month;

	/**
	 * 年
	 */
	private String year;

	/**
	 * 对话内容
	 */
	private String dialogs;

	/**
	 * 创建时间
	 */
	private java.util.Date createTime;


}
