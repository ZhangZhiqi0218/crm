package com.yjxxt.crm.interceptors;

import com.yjxxt.crm.exceptions.NoLoginException;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NoLoginInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //通过cookie中的用户ID
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        //判断用户ID是否不为空，且数据库中存在相应的记录
        if(null == userId || userService.selectByPrimaryKey(userId)==null){
            //抛出未登录异常
            throw new NoLoginException();
        }
        return true;
    }
}
