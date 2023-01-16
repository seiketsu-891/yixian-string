package com.star.string.controller;

import com.star.string.entity.User;
import com.star.string.enums.TokenExpEnum;
import com.star.string.req.*;
import com.star.string.resp.CommonResp;
import com.star.string.resp.UserLoginResp;
import com.star.string.service.TokenService;
import com.star.string.service.UserService;
import com.star.string.util.CopyUtil;
import com.star.string.util.ValidatorUtil;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@RequestMapping("/account")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private TokenService tokenService;

    /**
     * 注册
     */
    @PostMapping("/register")
    public CommonResp register(@RequestBody UserRegiReq req) {
        CommonResp<UserLoginResp> resp = new CommonResp<>();

        ValidatorUtil.require(req.getPhoneNumber(), "手机号");
        ValidatorUtil.require(req.getCode(), "验证码");
        ValidatorUtil.require(req.getPassword(), "密码");
        ValidatorUtil.require(req.getTimezone(), "时区");
        ValidatorUtil.legalMobileNumber(req.getPhoneNumber());

        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));

        UserLoginResp userLogin = userService.register(req);
        resp.setContent(userLogin);

        // 生成token
        String token = tokenService.generateToken(userLogin.getId(), TokenExpEnum.ONE_DAY.getCode());
        resp.setMessage(token);

        return resp;
    }
    
    /**
     * 登录
     */
    @PostMapping("/login")
    public CommonResp login(@RequestBody UserLoginReq req) {
        ValidatorUtil.require(req.getPhoneNumber(), "手机号");
        ValidatorUtil.require(req.getPassword(), "密码");
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));

        if (req.getKeepLogin() == null) {
            req.setKeepLogin(false);
        }

        CommonResp<UserLoginResp> resp = new CommonResp<>();
        UserLoginResp userLoginResp = userService.login(req);

        resp.setContent(userLoginResp);

        // 判断token有效期并生成token
        String tokenExp;
        if (req.getKeepLogin()) {
            tokenExp = TokenExpEnum.THIRTY_DAYS.getCode();
        } else {
            tokenExp = TokenExpEnum.ONE_DAY.getCode();
        }
        String token = tokenService.generateToken(userLoginResp.getId(), tokenExp);
        resp.setMessage(token);
        return resp;
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public CommonResp logout(@RequestBody UserLogoutReq req) {
        userService.logout(req);
        CommonResp resp = new CommonResp();
        return resp;
    }

    /**
     * 更新用户信息
     */
    @PostMapping("/update/{userId}")
    public CommonResp<UserLoginResp> update(@PathVariable String userId, @RequestBody UserUpdateReq req) {
        CommonResp<UserLoginResp> resp = new CommonResp();
        if (req.getUsername() != null) {
            ValidatorUtil.length(req.getUsername(), "用户名", 3, 16);
        }
        if (req.getTimeFormat() != null) {
            ValidatorUtil.validTimeFormat(req.getTimeFormat());
        }

        User user = userService.update(userId, req);
        UserLoginResp userLogin = CopyUtil.copy(user, UserLoginResp.class);

        resp.setContent(userLogin);
        return resp;
    }

    /**
     * 重置密码
     */
    @PostMapping("/resetpswd/{userId}")
    public CommonResp resetPassword(@PathVariable String userId, @RequestBody PasswordResetReq passwords) {
        CommonResp resp = new CommonResp();
        ValidatorUtil.require(passwords.getNewPassword(), "新密码");
        ValidatorUtil.require(passwords.getOldPassword(), "旧密码");
        String oldPwd = passwords.getOldPassword();
        String newPwd = passwords.getNewPassword();

        oldPwd = DigestUtils.md5DigestAsHex(oldPwd.getBytes());
        newPwd = DigestUtils.md5DigestAsHex(newPwd.getBytes());

        userService.resetPassword(userId, oldPwd, newPwd);

        return resp;
    }

    /**
     * 忘记密码时的重置密码
     */
    @PostMapping("/resetpswd-forgot")
    public CommonResp resetPasswordForgot(@RequestBody PswdRestForgotReq req) {
        CommonResp resp = new CommonResp();
        ValidatorUtil.require(req.getPassword(), "新密码");
        ValidatorUtil.legalMobileNumber(req.getMobile());
        ValidatorUtil.require(req.getCode(), "短信验证码");
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));

        userService.resetPasswordForgot(req);
        return resp;
    }
}