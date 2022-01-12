package com.yjxxt.crm.controller;

import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.mapper.PermissionMapper;
import com.yjxxt.crm.service.PermissionService;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController extends BaseController {

    @Resource
    private UserService userService;

    @Resource
    private PermissionService permissionService;

    /**
     * 登陆页面
     *
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "index";
    }

    /**
     * 后台资源页面
     *
     * @return
     */
    @RequestMapping("main")
    public String main(HttpServletRequest request){

        //通过工具类从cookie中获取用户userId
        int userId = LoginUserUtil.releaseUserIdFromCookie(request);
        //调用service层的方法查询，通过userId查询用户对象的信息
        User user = userService.selectByPrimaryKey(userId);
        //将用户对象存入到request作用域中
        request.setAttribute("user",user);
        //将权限码存入session中
        List<String> permissions = permissionService.queryUserHasRolesHasPermissions(userId);
        request.getSession().setAttribute("permissions",permissions);
        return "main";
    }

    /**
     * 欢迎页面
     *
     * @return
     */
    @RequestMapping("welcome")
    public String welcome(){
        return "welcome";
    }

}
