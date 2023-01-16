package com.star.string.enums;

public enum TokenExpEnum {
    THIRTY_DAYS("DAY30", "30天有效期"),
    ONE_DAY("DAY1", "1天有效期");

    private String code;
    private String desc;

    TokenExpEnum(String code, String desc){
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
