package com.star.string.enums;

public enum TimeFormatEnum {
    HOUR24("HOUR24", "24小时制"),
    HOUR12("HOUR12", "12小时制");

    private String code;
    private String desc;

    TimeFormatEnum(String code, String desc){
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
