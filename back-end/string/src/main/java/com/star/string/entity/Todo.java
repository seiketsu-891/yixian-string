package com.star.string.entity;


import javax.persistence.Entity;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Id;

@Data
@Entity
public class Todo {

	/**
	 * 主键id
	 */
	@Id
	private String id;

	/**
	 * 创建时间
	 */
	private java.util.Date createTime;

	/**
	 * 是否完成
	 */
	private Boolean done;

	/**
	 * 优先区域
	 */
	private String priority;

	/**
	 * 用户id
	 */
	private String userId;

	/**
	 * 描述
	 */
	private String description;

	/**
	 * 更新时间
	 */

	private java.util.Date updateTime;

	/**
	 * 顺序
	 */
	private Long orderIndex;

	/**
	 * 对应日期
	 */
	private String date;


}
