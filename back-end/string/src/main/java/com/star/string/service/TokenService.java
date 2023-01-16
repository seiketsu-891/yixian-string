package com.star.string.service;

import com.star.string.entity.Token;
import com.star.string.repository.TokenRepository;
import com.star.string.req.UserLogoutReq;
import com.star.string.util.TokenUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

@Service
public class TokenService {
    @Resource
    private TokenRepository tokenRepository;

    /**
     * 根据id和过期时间生成token
     */
    public String generateToken(String userId, String exp) {
      String token = TokenUtil.sign(userId, String.valueOf(new Date()),exp);
      Token toBeSaved = new Token();
      toBeSaved.setHasLogout(false);
      toBeSaved.setUserId(userId);
      toBeSaved.setId(String.valueOf(UUID.randomUUID()));
      toBeSaved.setContent(token);
      toBeSaved.setCreateTime(new Date());
      tokenRepository.save(toBeSaved);
      return token;
    }

    /**
     * 设置token失效（退出登录时）
     */
    public void makeTokenInvalid(UserLogoutReq req) {
        Token tokenDb = tokenRepository.findByUserIdAndContent(req.getUserId(),req.getToken());
        if(tokenDb!= null){
            tokenDb.setHasLogout(true);
        }
    }
}
