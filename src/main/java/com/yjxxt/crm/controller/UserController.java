package com.yjxxt.crm.controller;

import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.exceptions.ParamsException;
import com.yjxxt.crm.model.UserModel;
import com.yjxxt.crm.query.UserQuery;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
public class UserController extends BaseController {

    @Resource
    private UserService userService;

    /**
     *  跳转到修改密码页面
     * @return
     */
    @RequestMapping("user/toPasswordPage")
    public String updatePwd(){

        return "user/password";
    }

    /**
     *  跳转到修改数据页面（将当前数据展示到页面中）
     * @param request
     * @return
     */
    @RequestMapping("user/toSettingPage")
    public String setting(HttpServletRequest request){
        //获取用户id
        int userId = LoginUserUtil.releaseUserIdFromCookie(request);
        //调用方法
        User user = userService.selectByPrimaryKey(userId);
        //存储信息
        request.setAttribute("user",user);
        //转发
        return "user/setting";
    }

    /**
     * 登录模块
     *
     * @param userName
     * @param userPwd
     * @return
     */
    @PostMapping("user/login")
    @ResponseBody
    public ResultInfo userLogin(String userName,String userPwd){

        ResultInfo resultInfo = new ResultInfo();
        // 调用Service层的登录方法，得到返回的用户对象
        UserModel userModel = userService.userLogin(userName, userPwd);
        // 将返回的UserModel对象设置到 ResultInfo 对象中
        resultInfo.setResult(userModel);

        return resultInfo;
    }

    /**
     *  修改密码
     * @param request
     * @param oldPassword
     * @param newPassword
     * @param confirmPassword
     * @return
     */
    @PostMapping("user/updatePassword")
    @ResponseBody
    public ResultInfo updateUserPassword(HttpServletRequest request,String oldPassword,String newPassword,String confirmPassword){
        ResultInfo resultInfo = new ResultInfo();
        //获得userId
        int userId = LoginUserUtil.releaseUserIdFromCookie(request);
        //调用service层的修改方法
        userService.changeUserPwd(userId,oldPassword,newPassword,confirmPassword);
        return resultInfo;
    }

    /**
     *  进行信息的修改
     * @param user
     * @return
     */
    @PostMapping("user/setting")
    @ResponseBody
    public ResultInfo sayUpdate(User user){
        ResultInfo resultInfo = new ResultInfo();
        userService.updateByPrimaryKeySelective(user);
        return resultInfo;
    }

    /**
     *  查询销售人员
     * @return
     */
    @RequestMapping("user/sales")
    @ResponseBody
    public List<Map<String, Object>> findSales(){
        List<Map<String, Object>> maps = userService.querySales();
        return maps;
    }

    /**
     * 用户权限模块
     *  查询用户列表信息
     * @param userQuery
     * @return
     */
    @RequestMapping("user/list")
    @ResponseBody
    public Map<String,Object> queryUserByParams(UserQuery userQuery){
        return userService.findUserByParams(userQuery);
    }

    /**
     *  展示用户管理页面
     * @return
     */
    @RequestMapping("user/index")
    public String index(){
        return "user/user";
    }

    /**
     * 跳转到修改或更新页面
     * @return
     */
    @RequestMapping("user/addOrUpdatePage")
    public String addOrUpdatePage(Integer id,Model model){
        if(id!=null){
            User temp = userService.selectByPrimaryKey(id);
            model.addAttribute("user",temp);
        }
        return "user/add_update";
    }

    /**
     *  添加用户
     * @param user
     * @return
     */
    @RequestMapping("user/save")
    @ResponseBody
    public ResultInfo save(User user){
        userService.addUser(user);
        return success("用户添加成功！");
    }

    /**
     *  更新用户信息
     * @param user
     * @return
     */
    @RequestMapping("user/update")
    @ResponseBody
    public ResultInfo update(User user){
        userService.changeUser(user);
        return success("用户修改成功！");
    }

    @RequestMapping("user/delete")
    @ResponseBody
    public ResultInfo delete(Integer[] ids){
        userService.removeUserIds(ids);
        return success("删除数据成功！");
    }

}
