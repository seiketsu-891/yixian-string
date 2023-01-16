package com.star.string.controller;
import com.star.string.exception.BusinessException;
import com.star.string.exception.ValidatorException;
import com.star.string.resp.CommonResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
@CrossOrigin
@ControllerAdvice
public class ControllerExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler(value = ValidatorException.class)
    @ResponseBody
    public CommonResp validatorExceptionHandler(ValidatorException e) {
        CommonResp resp = new CommonResp();
        resp.setSuccess(false);
        LOG.warn(e.getMessage());
        resp.setMessage("请求参数异常！");
        return resp;
    }

    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public CommonResp businessExceptionHandler(BusinessException e) {
        CommonResp responseDto = new CommonResp();
        responseDto.setSuccess(false);
        LOG.error("业务异常：{}", e.getCode().getDesc());
        responseDto.setMessage(e.getCode().getDesc());
        return responseDto;
    }
}
