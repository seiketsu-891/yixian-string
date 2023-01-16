package com.star.string.enums;

public enum TodoPriorityEnum {
    URGENT_IMPORTANT("11", "紧急/重要"),
    URGENT_NOT_IMPORTANT("10", "紧急/不重要"),
    NOT_URGENT_IMPORTANT("01", "不紧急/重要"),
    NOT_URGENT_NOT_IMPORTANT("00", "不紧急/不重要");

    private String code;
    private String desc;

    TodoPriorityEnum(String code, String desc){
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
