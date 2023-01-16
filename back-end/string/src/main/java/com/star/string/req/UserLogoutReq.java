package com.star.string.req;

import lombok.Data;

@Data
public class UserLogoutReq {
    String userId;
    String token;

}
