package com.star.string.entity;


import javax.persistence.Entity;
import lombok.Data;
import javax.persistence.Id;

@Data
@Entity
public class TimeEntryCategory {

	/**
	 * 主键id
	 */
	@Id
	private String id;

	private String name;

	private String color;

	private String userId;

	private java.util.Date createTime;

	private java.util.Date updateTime;


}
