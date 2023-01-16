package com.star.string.interceptor;

import com.alibaba.fastjson2.JSONObject;
import com.star.string.entity.Token;
import com.star.string.repository.TokenRepository;
import com.star.string.util.TokenUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

@Component
public class TokenInterceptor  implements HandlerInterceptor {
    @Resource
    private TokenRepository repository;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }


        response.setCharacterEncoding("utf-8");
        Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        System.out.println("请求路径为：" + request.getRequestURI());
        String userId = (String)pathVariables.get("userId");
        String token = request.getHeader("Authorization");

        if (token != null) {
            // substring 目的是去掉前面Bearer ，避免校验失败
            token = token.substring(7);
            boolean result = TokenUtil.verify(token);

            Token tokenDb = repository.findByUserIdAndContent(userId, token);
            if(tokenDb == null || tokenDb .getHasLogout()){
                result = false;
            }

            if (result) {
//                System.out.println("通过拦截器");
                return true;
            }
        }

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            System.out.println("认证失败，未通过拦截器");
            response.sendError(401);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500);
            return false;
        }
        return false;
    }
}
