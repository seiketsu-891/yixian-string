package com.star.string.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.star.string.enums.TokenExpEnum;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TokenUtil {
    /**
     * token过期时间
     */
    private static final long EXPIRE_AFTER_THIRTY_DAYS = 30L * 24 * 60 * 60 * 1000;
    //    private static final long EXPIRE_AFTER_ONE_DAY = 24 * 60 * 60 * 1000;
    private static final long EXPIRE_AFTER_ONE_DAY = 24 * 60 * 1000;

    private static final long TEST_EXPIRE_AFTER_TWO_MIN = 2 * 60 * 1000;
    /**
     * token秘钥
     */
    private static final String TOKEN_SECRET = "BEST_SELF";


    /**
     * 生成签名
     *
     * @param userId    用户id
     * @param loginTime 登录时间
     * @return 生成的token
     */
    public static String sign(String userId, String loginTime, String exp) {
        try {
            // 设置过期时间
//            long expireTime = TEST_EXPIRE_AFTER_TWO_MIN;
            long expireTime = EXPIRE_AFTER_THIRTY_DAYS;
            if (Objects.equals(exp, TokenExpEnum.ONE_DAY.getCode())) {
                expireTime = EXPIRE_AFTER_ONE_DAY;
            }
            Date date = new Date(System.currentTimeMillis() + expireTime);
            System.out.println("新token过期时间为：" + date);
            // 私钥和加密算法
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            // 设置头部信息
            Map<String, Object> header = new HashMap<>(2);
            header.put("Type", "Jwt");
            header.put("alg", "HS256");
            // 返回token字符串
            return JWT.create()
                    .withHeader(header)
                    .withClaim("userId", userId)
                    .withClaim("loginTime", loginTime)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 检验token是否正确
     *
     * @param token 需要校验的token
     * @return 校验是否成功
     */
    public static boolean verify(String token) {
        try {
            //设置签名的加密算法：HMAC256
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

