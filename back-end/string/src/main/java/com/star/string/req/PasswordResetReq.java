package com.star.string.req;

import lombok.Data;

@Data
public class PasswordResetReq {
    String oldPassword;
    String newPassword;
}
