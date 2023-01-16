package com.star.string.entity;


import javax.persistence.Entity;
import lombok.Data;
import javax.persistence.Id;

@Data
@Entity
public class TodoMemo {

	/**
	 * 主键id
	 */
	@Id
	private String id;

	private String userId;

	private java.util.Date createTime;

	private java.util.Date updateTime;

	private Boolean allDone;

	private String year;

	private String month;

	private String day;
}
