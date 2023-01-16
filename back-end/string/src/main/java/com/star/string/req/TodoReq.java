package com.star.string.req;


import lombok.Data;


@Data
public class TodoReq {

	/**
	 * ID
	 */
	private String id;


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
	 * 对应日期
	 */
	private String date;


}
