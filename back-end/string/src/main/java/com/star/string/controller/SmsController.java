package com.star.string.controller;

import com.star.string.req.SmsReq;
import com.star.string.resp.CommonResp;
import com.star.string.service.SmsService;
import com.star.string.util.ValidatorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/account/sms")
public class SmsController {
    private static final Logger LOG = LoggerFactory.getLogger(SmsController.class);

    @Resource
    private SmsService smsService;

    /**
     * 发送短信验证码
     */
    @PostMapping("/send")
    public CommonResp send(@RequestBody SmsReq smsReq) {
        LOG.info("收到发送短信请求：{}", smsReq);
        ValidatorUtil.require(smsReq.getMobile(), "手机号");
        ValidatorUtil.require(smsReq.getForWhat(), "用途");
        ValidatorUtil.legalMobileNumber(smsReq.getMobile());

        LOG.info("发送短信请求开始：{}", smsReq);
        CommonResp resp = new CommonResp();
        smsService.sendCode(smsReq);
        LOG.info("发送短信请求结束");
        return resp;
    }
}
