package com.star.string.req;

import lombok.Data;

@Data
public class UserUpdateReq {
    String username;
    String profileImg;
    String timezone;
    String timeFormat;
    boolean showShortEntry;
    String goals;
}
