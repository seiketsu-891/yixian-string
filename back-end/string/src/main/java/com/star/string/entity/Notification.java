package com.star.string.entity;


import javax.persistence.Entity;
import lombok.Data;
import javax.persistence.Id;

@Data
@Entity
public class Notification {

	/**
	 * 主键id
	 */
	@Id
	private String id;

	private String content;

	private String userId;

	private java.util.Date createTime;

	private Boolean status;

	private String type;

	private String title;


}
