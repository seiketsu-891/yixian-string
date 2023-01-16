package com.star.string.req;

import lombok.Data;

@Data
public class UserLoginReq {
    String phoneNumber;
    String password;
    Boolean keepLogin;
}
