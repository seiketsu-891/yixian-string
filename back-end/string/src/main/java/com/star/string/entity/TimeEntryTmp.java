package com.star.string.entity;


import javax.persistence.Entity;
import lombok.Data;
import javax.persistence.Id;

@Data
@Entity
public class TimeEntryTmp {

	/**
	 * 主键id
	 */
	@Id
	private String id;

	private String categoryId;

	private String userId;

	private String description;

	private java.util.Date createTime;

	private Long start;


}
