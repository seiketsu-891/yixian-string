package com.star.string.enums;

public enum NotificationTypeEnum {
    WELCOME("WELCOME", "欢迎信息"),
    TEST("TEST", "测试信息"),
    UPDATE("UPDATE", "网站更新通知");

    private String code;
    private String desc;

    NotificationTypeEnum(String code, String desc){
        this.code =code;
        this.desc=desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
