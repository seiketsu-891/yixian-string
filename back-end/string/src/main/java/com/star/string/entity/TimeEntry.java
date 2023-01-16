package com.star.string.entity;


import javax.persistence.Entity;
import lombok.Data;
import javax.persistence.Id;

@Data
@Entity
public class TimeEntry {

	/**
	 * 主键id
	 */
	@Id
	private String id;

	private String description;

	private String categoryId;

	private Long duration;

	private String userId;

	private java.util.Date createTime;

	private java.util.Date updateTime;

	private Long start;

	private Long end;

	public interface ProjectCategoryIdAndDuration {
		String getCategoryId();
		Long getDuration();
		String getStart();

	}
}
