package com.star.string.service;

import com.star.string.entity.Sms;
import com.star.string.enums.SmsStatusEnum;
import com.star.string.enums.SmsUseEnum;
import com.star.string.exception.BusinessException;
import com.star.string.exception.BusinessExceptionCode;
import com.star.string.repository.SmsRepository;
import com.star.string.repository.UserRepository;
import com.star.string.req.SmsReq;
import com.star.string.util.CopyUtil;
import com.zhenzi.sms.ZhenziSmsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class SmsService {

    private static final Logger LOG = LoggerFactory.getLogger(SmsService.class);
    @Resource
    private SmsRepository smsRepository;

    @Resource
    private UserRepository userRepository;

    /**
     * 发送手机短信验证码
     * 同手机号1分钟内不能重复发送短信
     */
    public void sendCode(SmsReq req)  {

        String forWhat =req.getForWhat();

        // 如果是发送忘记密码的验证码，则需要确保手机已注册
        // 而注册则相反
        if(userRepository.existsByPhoneNumber(req.getMobile())){
            if(SmsUseEnum.REGISTER.getCode().equals(forWhat) ){
                throw new BusinessException(BusinessExceptionCode.MOBILE_EXISTS);
            }
        }else{
            if( SmsUseEnum.FORGET.getCode().equals(forWhat)){
                throw new BusinessException(BusinessExceptionCode.MOBILE_NOT_EXISTS);
            }
        }

        // 查找1分钟内有没有同手机号同操作发送记录且没被使用过
        List<Sms>  smsList = smsRepository.findByMobileAndForWhatAndStatusAndAtIsAfterOrderByAtDesc(req.getMobile(), req.getForWhat(), SmsStatusEnum.NOT_USED.getCode(), new Date(new Date().getTime()- 60 * 1000));


        if(smsList.size() == 0){

            saveAndSend(req);
            // 把5分钟之内的上一条短信设置为已使用
            List<Sms>  smsListFiveMin = smsRepository.findByMobileAndForWhatAndStatusAndAtIsAfterOrderByAtDesc(req.getMobile(), req.getForWhat(), SmsStatusEnum.NOT_USED.getCode(), new Date(new Date().getTime()-5*  60 * 1000));
            if(smsListFiveMin.size() >  1){
               Sms prev = smsListFiveMin.get(1);
               prev.setStatus(SmsStatusEnum.USED.getCode());
               smsRepository.save(prev);
            }
        }else{
            LOG.warn("短信请求过于频繁, {}", req.getMobile());
            throw new BusinessException(BusinessExceptionCode.MOBILE_CODE_TOO_FREQUENT);
        }
    }

    /**
     * 榛子云短信服务
     */
    private void ZhenziYunSmsSend(String mobile, String code) {
        ZhenziSmsClient client = new ZhenziSmsClient("https://sms_developer.zhenzikj.com", "112608", "1332aec8-065d-4e5d-b2ae-db5af52c6026");
        Map<String, Object> params = new HashMap<>();
        params.put("number", mobile);
        params.put("templateId", "10778");
        String[] templateParams = new String[1];
        templateParams[0] = code;
        params.put("templateParams", templateParams);
        try {
            String result = client.send(params);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 保存并发送短信验证码
     */
    private  void saveAndSend(SmsReq req)  {
        // 生成6位数字
        String code = String.valueOf((int)(Math.random()*900000 + 100000));
        LOG.info("生成的验证码为:{}", code);

        // 调用短信发送服务
        this.ZhenziYunSmsSend(req.getMobile(), code);
        Sms sms = CopyUtil.copy(req, Sms.class);


        sms.setAt(new Date());
        sms.setId(UUID.randomUUID().toString());
        sms.setStatus(SmsStatusEnum.NOT_USED.getCode());
        sms.setCode(code);
        save(sms);
        //
    }

    /**
     * 保存验证码
     */
    private  void save (Sms sms){
        smsRepository.save(sms);
    }

    /**
     * 验证码5分钟内有效，且操作类型要一致
     */
    public void validCode(SmsReq req){
        System.out.println(req);
        List<Sms>  smsList = smsRepository.findByMobileAndForWhatAndStatusAndAtIsAfterOrderByAtDesc(req.getMobile(), req.getForWhat(), SmsStatusEnum.NOT_USED.getCode(), new Date(new Date().getTime()-5*  60 * 1000));

        if(smsList.size() > 0) {
            Sms smsDb = smsList.get(0);
            if(!smsDb.getCode().equals(req.getCode())){
                LOG.warn("短信验证码错误,数据库验证码为:{},用户输入的验证码为:{}",smsDb.getCode(),req.getCode());
                throw new BusinessException(BusinessExceptionCode.MOBILE_CODE_ERROR);
            }else{
                smsDb.setStatus(SmsStatusEnum.USED.getCode());
                save(smsDb);
            }
        }else{
            LOG.warn("短信验证码不存在或已过期");
            throw new BusinessException(BusinessExceptionCode.MOBILE_CODE_INVALID);
        }
    }

}
