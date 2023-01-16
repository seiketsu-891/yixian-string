package com.star.string.resp;

import lombok.Data;

@Data
public class UserLoginResp {
    private String id;
    private String username;
    private String phoneNumber;
    private String profileImg;
    private Integer activated;
    private String timezone;
    private String timeFormat;
    private String goals;
    private Boolean showShortEntry;
}
