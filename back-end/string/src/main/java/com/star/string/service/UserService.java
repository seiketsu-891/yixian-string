package com.star.string.service;

import com.star.string.entity.User;
import com.star.string.enums.SmsUseEnum;
import com.star.string.enums.TimeFormatEnum;
import com.star.string.exception.BusinessException;
import com.star.string.exception.BusinessExceptionCode;
import com.star.string.exception.DataException;
import com.star.string.repository.UserRepository;
import com.star.string.req.*;
import com.star.string.resp.UserLoginResp;
import com.star.string.util.CopyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static com.star.string.util.BeanUtil.getNullPropertyNames;

@Service
public class UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    private static final String DEFAULT_PROFILE_IMG = "https://save-1304263782.cos.ap-hongkong.myqcloud.com/avatar.png";
    private static final String DEFAULT_USER_NAME = "未命名用户";
     @Resource
     private  UserRepository userRepository;
     @Resource
     private SmsService smsService;
     @Resource
     private NotificationService notificationService;
     @Resource
     private TimeEntryCategoryService timeEntryCategoryService;

     @Resource
     private TokenService tokenService;
    /**
     * 注册
     */
    public UserLoginResp register (UserRegiReq req){
        // 检查验证码
        SmsReq  smsReq = new SmsReq();
        smsReq.setMobile(req.getPhoneNumber());
        smsReq.setCode(req.getCode());
        smsReq.setForWhat(SmsUseEnum.REGISTER.getCode());
        smsService.validCode(smsReq);

        // 创建user实例，填充默认数据
        User user = CopyUtil.copy(req, User.class);
        user.setProfileImg(DEFAULT_PROFILE_IMG);
        user.setUsername(DEFAULT_USER_NAME);
        user.setTimeFormat(TimeFormatEnum.HOUR24.getCode());
        user.setShowShortEntry(true);
        user.setGoals("");
        User savedUser  = insert(user);

        // 自动新增一条欢迎通知
        notificationService.welcomeMessage(user.getId());

        // 自动新增时间条目默认分类
        timeEntryCategoryService.defaultCat(user.getId());

        return CopyUtil.copy(savedUser,UserLoginResp.class);
    }



    /**
     * 添加新用户
     */
    private User insert(User user){
        Date now = new Date();
        user.setId(String.valueOf(UUID.randomUUID()));
        user.setCreateTime(now);
        user.setUpdateTime(now);
        return this.save(user);
    }


    private User save(User user){
        return userRepository.save(user);
    }
    /**
     * 登录
     */
    public UserLoginResp login(UserLoginReq req){
        User userDb = userRepository.findByPhoneNumber((req.getPhoneNumber()));
        if(ObjectUtils.isEmpty(userDb)){
            LOG.info("该手机号尚未注册, {}", req.getPhoneNumber());
            throw new BusinessException(BusinessExceptionCode.LOGIN_ERROR);
        }else{
                if (userDb.getPassword().equals(req.getPassword())) {
                    // 登录成功
                    UserLoginResp userLoginResp = CopyUtil.copy(userDb, UserLoginResp.class);
                    return userLoginResp;
                } else {
                    // 密码不对
                    LOG.info("密码错误, 输入密码：{}, 数据库密码：{}", req.getPassword(), userDb.getPassword());
                    throw new BusinessException(BusinessExceptionCode.LOGIN_ERROR);
                }
            }
    }

    /**
     * 更新用户信息
     *
     */
    public User update(String userId, UserUpdateReq req) {
      Optional<User> user = userRepository.findById(userId);
      if(user.isEmpty()){
          throw new DataException("用户id数据异常");
      }else{
          User userDb = user.get();
          BeanUtils.copyProperties(req, userDb,  getNullPropertyNames(req));
          return this.save(userDb);
      }

    }

    /**
     * 更新密码
     */
    public void resetPassword(String userId, String oldPwd, String newPwd) {
        if(newPwd.equals(oldPwd)){
            throw new BusinessException(BusinessExceptionCode.PASSWORD_NO_CHANGE);
        }
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()){
            throw new DataException("用户id数据异常");
        }else {
           User  userDb = user.get();
           if(!oldPwd .equals(userDb.getPassword())) {
             throw new BusinessException(BusinessExceptionCode.PASSWORD_WRONG);
           }
           userDb.setPassword(newPwd);
           this.save(userDb);
        }
    }

    /**
     * 重设密码（忘记密码时）
     */
    public void resetPasswordForgot(PswdRestForgotReq req) {
        System.out.println(req);
       SmsReq smsReq  = new SmsReq();
       smsReq.setForWhat(req.getCodeForWhat());
       smsReq.setCode(req.getCode());
       smsReq.setMobile(req.getMobile());
       smsService.validCode(smsReq);

       User userDb = userRepository.findByPhoneNumber(req.getMobile());
       if(userDb == null){
           throw new BusinessException(BusinessExceptionCode.MOBILE_NOT_EXISTS);
       }

       if(userDb.getPassword().equals(req.getPassword())){
           throw new BusinessException(BusinessExceptionCode.PASSWORD_NO_CHANGE);
       }

       userDb.setPassword(req.getPassword());
       userRepository.save(userDb);
    }

    /**
     * 用户退出登录
     */
    public void logout(UserLogoutReq req) {
        tokenService.makeTokenInvalid(req);
    }
}
