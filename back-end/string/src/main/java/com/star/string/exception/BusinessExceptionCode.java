package com.star.string.exception;

public enum BusinessExceptionCode {
    USER_NAME_EXIST("用户名已存在"),
    LOGIN_ERROR("手机号未注册或密码错误"),
    MOBILE_CODE_TOO_FREQUENT("短信请求过于频繁"),
    MOBILE_CODE_ERROR("短信验证码错误"),
    MOBILE_CODE_INVALID("验证码不存在或已过期"),
    MOBILE_EXISTS("该手机号已注册"),
    TODO_NOT_EXISTS("该待办不存在"),
    NOTIFICATION_NOT_EXISTS("该条信息不存在"),
    PROJECTCAT_NAME_EXISTS("已存在同名项目分类"),
    PROJECTCAT_DOES_NOT_EXISTS("该项目分类不存在"),
    TIME_ENTRY_DOES_NOT_EXISTS("该时间条目不存在"), TIME_ENTRY_TIMER_NOT_EXISTS("当前没有正在计时的条目"), DIARY_QUESTION_IN_USE_SET_FAILED("该问题不存在或之前已删除"), DIARY_NOT_EXISTS("该日记内容不存在"), PASSWORD_WRONG("原密码输入错误"),
    MOBILE_NOT_EXISTS("该手机号尚未注册"), PASSWORD_NO_CHANGE("新密码与原密码相同");
    private String desc;

    BusinessExceptionCode(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
