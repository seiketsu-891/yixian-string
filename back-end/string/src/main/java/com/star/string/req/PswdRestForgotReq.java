package com.star.string.req;

import lombok.Data;

@Data
public class PswdRestForgotReq {
    private String mobile;
    private String code;
    private String password;
    private String codeForWhat;
}
