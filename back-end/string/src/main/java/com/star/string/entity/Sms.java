package com.star.string.entity;


import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Sms {
	@Id
	/**
	 * id
	 */
	private String id;

	/**
	 * 手机号
	 */
	private String mobile;

	/**
	 * 验证码
	 */
	private String code;

	/**
	 * 用途|枚举[SmsUseEnum]：REGISTER("R", "注册"), FORGET("F", "忘记密码")
	 */
	private String forWhat;

	/**
	 * 生成时间
	 */
	private java.util.Date at;

	/**
	 * 状态|枚举[SmsStatusEnum]：USED("U", "已使用"), NOT_USED("N", "未使用")
	 */
	private String status;


}
