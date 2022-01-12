package com.yjxxt.crm;


import com.alibaba.fastjson.JSON;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.exceptions.NoAuthException;
import com.yjxxt.crm.exceptions.NoLoginException;
import com.yjxxt.crm.exceptions.ParamsException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {

        /**
         * 拦截器异常
         */
        if(e instanceof NoLoginException){
            //如果捕获的是未登录异常，则重定向的是登录页面
            ModelAndView mav = new ModelAndView("redirect:/index");
            return mav;
        }

        //实例化对象
        ModelAndView mav=  new ModelAndView("error");
        //存储数据
        mav.addObject("code",400);
        mav.addObject("msg","系统异常，请稍后再试...");

        //判断handler
        if(handler instanceof HandlerMethod){
            //类型转换
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //获得每个方法上的 responsebody 注解
            ResponseBody responseBody = handlerMethod.getMethod().getDeclaredAnnotation(ResponseBody.class);
            //判断 responsebody 注解是否存在
            // 如果不存在返回的是视图
            // 如果存在返回的是JSON
            if(null == responseBody){
                //返回的是视图
                if(e instanceof ParamsException){
                    ParamsException pe = (ParamsException) e;
                    mav.addObject("code",pe.getCode());
                    mav.addObject("msg",pe.getMsg());
                }
                return mav;
            }else{
                ResultInfo resultInfo = new ResultInfo();
                resultInfo.setCode(300);
                resultInfo.setMsg("系统异常，请重试！");
                if(e instanceof ParamsException){
                    ParamsException pe = (ParamsException)e;
                    resultInfo.setCode(pe.getCode());
                    resultInfo.setMsg(pe.getMsg());
                }
                if(e instanceof NoAuthException){
                    NoAuthException pe=(NoAuthException)e;
                    resultInfo.setCode(pe.getCode());
                    resultInfo.setMsg(pe.getMsg());
                }
                //因为要传的是JSON 所以要设置响应头里的相应类型和编码格式
                response.setContentType("application/json;charset=utf-8");
                //构建输出流将json写出
                PrintWriter pw = null;
                try {
                    pw = response.getWriter();
                    pw.write(JSON.toJSONString(resultInfo));
                    pw.flush();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } finally {
                    if(pw != null){
                        pw.close();
                    }
                }
                return null;
            }
        }
        return mav;
    }
}
