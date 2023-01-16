package com.star.string.req;

import lombok.Data;

@Data
public class SmsReq {
    private String mobile;
    private String forWhat;
    private String code;
}
