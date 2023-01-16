package com.star.string.util;

import com.star.string.enums.TimeFormatEnum;
import com.star.string.enums.TodoPriorityEnum;
import com.star.string.exception.ValidatorException;
import org.springframework.util.StringUtils;

public class ValidatorUtil {

    /**
     * 空校验（null or ""）
     */
    public static void require(Object str, String fieldName) {
        if (StringUtils.isEmpty(str)) {
            throw new ValidatorException(fieldName + "不能为空");
        }
    }

    /**
     * 长度校验
     */
    public static void length(String str, String fieldName, int min, int max) {
        if (StringUtils.isEmpty(str)) {
            return;
        }
        int length = 0;
        if (!StringUtils.isEmpty(str)) {
            length = str.length();
        }
        if (length < min || length > max) {
            throw new ValidatorException(fieldName + "长度" + min + "~" + max + "位");
        }
    }

    /**
     * 手机号校验
     */
    public static void legalMobileNumber(String str) {
        final String FIELD_MOBILE_NUMBER = "手机号";
        String regex = "^1[3|4|5|7|8][0-9]\\d{4,8}$";
        if (str.length() != 11 || !str.matches(regex)) {
            throw new ValidatorException(FIELD_MOBILE_NUMBER + "不合法");
        }
    }

    /**
     * 日期格式检验
     */
    public static void validYMDDate(String s) {
        if (!TimeUtil.isYMDDate(s)) {
            throw new ValidatorException("日期不符合ymd格式");
        }
    }

    /**
     * todo优先区域参数校验
     */
    public static void validTodoPriority(String s) {
        for (TodoPriorityEnum p : TodoPriorityEnum.values()) {
            if (p.getCode().equals(s)) {
                return;
            }
        }
        throw new ValidatorException("todo优先区域参数有误");
    }

    /**
     * 时间格式检验
     */
    public static void validTimeFormat(String s) {
        for (TimeFormatEnum tf : TimeFormatEnum.values()) {
            if (tf.getCode().equals(s)) {
                return;
            }
        }
        throw new ValidatorException("时间格式参数有误");
    }


    /**
     * 判断结束时间是否大于开始时间
     */
    public static void validStartAndEnd(Long start, Long end) {
        if (start == null || end == null || start >= end) {
            throw new ValidatorException("开始结束时间参数异常");
        }
    }

    /**
     * 判断某时间是否在当前时间之前
     */
    public static void isBeforeNow(Long t) {
        if (t == null || t >= System.currentTimeMillis()) {
            throw new ValidatorException("传入时间大于系统当前时间");
        }
    }
}
