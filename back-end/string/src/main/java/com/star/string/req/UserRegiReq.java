package com.star.string.req;

import lombok.Data;

@Data
public class UserRegiReq {
    String phoneNumber;
    String password;
    String timezone;
    String code;
}
