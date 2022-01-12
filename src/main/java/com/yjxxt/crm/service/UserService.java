package com.yjxxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.bean.UserRole;
import com.yjxxt.crm.mapper.UserMapper;
import com.yjxxt.crm.mapper.UserRoleMapper;
import com.yjxxt.crm.model.UserModel;
import com.yjxxt.crm.query.UserQuery;
import com.yjxxt.crm.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.net.URL;
import java.util.*;

@Service
public class UserService extends BaseService<User,Integer> {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    /**
     * 用户登录
     *
     */
    public UserModel userLogin(String userName,String userPwd){
        //验证是否为空
        checkLoginParam(userName,userPwd);
        //验证数据库中用户是否存在
        User user =  userMapper.queryUserByUserName(userName);
        AssertUtil.isTrue(null == user,"用户名不存在");
        //验证密码
        checkLoginPwd(userPwd,user.getUserPwd());
        //密码正确、登录成功
        return buildUserInfo(user);

    }

    /**
     * 返回目标对象
     *
     * @param user
     * @return
     */
    private UserModel buildUserInfo(User user) {

        UserModel userModel = new UserModel();
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());

        return userModel;
    }

    /**
     * 验证密码
     *
     * @param userPwd
     * @param userPwd1
     */
    private void checkLoginPwd(String userPwd, String userPwd1) {
        //数据库中密码是加密的，所以要给得到的密码进行加密再和数据库中的密码进行比较
        //第一个参数是用户输入的密码、第二个参数是数据库中的密码
        // 使用Md5Utils中的encode方法进行加密
        userPwd = Md5Util.encode(userPwd);
        //比较两个密码
        AssertUtil.isTrue(!userPwd.equals(userPwd1),"用户密码不正确");
    }

    /**
     * 判空
     *
     * @param userName
     * @param userPwd
     */
    private void checkLoginParam(String userName, String userPwd) {
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(userPwd),"用户密码不能为空");
    }


    /**
     * 修改密码
     *
     */
    public void changeUserPwd(Integer userId,String oldPassword,String newPassword,String confirmPassword){
        //通过sql获取user对象
        User user = userMapper.selectByPrimaryKey(userId);
        //参数的校验、创建一个新的方法
        checkPasswordParams(user,oldPassword,newPassword,confirmPassword);
        //设置新密码
        user.setUserPwd(Md5Util.encode(newPassword));
        //更新成功
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"修改密码失败");

    }

    /**
     * 校验参数
     *
     * @param user
     * @param oldPassword
     * @param newPassword
     * @param confirmPassword
     */
    private void checkPasswordParams(User user, String oldPassword, String newPassword, String confirmPassword) {
        //用户对象验证
        AssertUtil.isTrue(null == user,"用户名未登录或不存在");
        //原始密码非空验证
        AssertUtil.isTrue(StringUtils.isBlank(oldPassword),"原始密码不能为空");
        //原始密码正确验证
        AssertUtil.isTrue(!(user.getUserPwd().equals(Md5Util.encode(oldPassword))),"原始密码不正确");
        //新密码非空验证
        AssertUtil.isTrue(StringUtils.isBlank(newPassword),"新密码不能为空");
        //新密码和原始密码相等判断
        AssertUtil.isTrue(oldPassword.equals(newPassword),"新密码不能和原始密码相等");
        //确认密码非空验证
        AssertUtil.isTrue(StringUtils.isBlank(newPassword),"确认密码不能为空");
        //确认密码和新密码相等判断
        AssertUtil.isTrue(!(newPassword.equals(confirmPassword)),"确认密码和新密码不相等");
    }

    /**
     *  查询所有销售人员
     * @return
     */
    public List<Map<String,Object>> querySales(){

       return userMapper.selectSales();
    }

    /**
     *  查询用户信息
     * @param userQuery
     * @return
     */
    public Map<String,Object> findUserByParams(UserQuery userQuery){
        //实例化map
        Map<String,Object> map = new HashMap<>();
        //开始分页
        PageHelper.startPage(userQuery.getPage(),userQuery.getLimit());
        PageInfo<User> plist = new PageInfo<>(userMapper.selectByParams(userQuery));
        //准备数据
        map.put("code",0);
        map.put("msg","success");
        map.put("count",plist.getTotal());
        map.put("data",plist.getList());

        return map;
    }

    /**
     *  添加用户操作
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addUser(User user){
        //验证
        checkUser(user.getUserName(),user.getEmail(),user.getPhone());
        //用户名唯一
        User temp = userMapper.queryUserByUserName(user.getUserName());
        AssertUtil.isTrue(null!=temp,"用户名已经存在");
        //设定默认值
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        //加密密码
        user.setUserPwd(Md5Util.encode("123456"));
        //验证是否成功
        AssertUtil.isTrue(insertHasKey(user)<1,"添加失败");

        //关联中间表
        relationUserRole(user.getId(),user.getRoleIds());
    }

    /**
     *  操作中间表
     * @param userId
     * @param roleIds
     */
    private void relationUserRole(Integer userId, String roleIds) {
        //准备集合存储对象
        List<UserRole> ulist = new ArrayList<>();
        //判断用户信息是否为空
        AssertUtil.isTrue(StringUtils.isBlank(roleIds),"请选择角色信息！");
        //统计当前用户有多少个角色
        int count = userRoleMapper.countUserRoleNum(userId);
        if(count>0){
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId)!=count,"用户角色删除失败！");
        }

        String[] RoleStrId = roleIds.split(",");

        for(String rid:RoleStrId){
            //准备对象
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(Integer.parseInt(rid));
            userRole.setCreateDate(new Date());
            userRole.setUpdateDate(new Date());
            //存放到集合
            ulist.add(userRole);
        }
        AssertUtil.isTrue(userRoleMapper.insertBatch(ulist)!=ulist.size(),"用户角色分配失败！");
    }

    /**
     *  验证用户名
     * @param userName
     * @param email
     * @param phone
     */
    private void checkUser(String userName, String email, String phone) {
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空");        AssertUtil.isTrue(StringUtils.isBlank(email),"邮箱不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(phone),"手机号不能为空");
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone),"手机号不合法");
    }

    /**
     *  修改用户信息
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void changeUser(User user){
        User temp = userMapper.selectByPrimaryKey(user.getId());
        AssertUtil.isTrue(null==temp,"待修改的用户名不存在");

        User temp2 = userMapper.queryUserByUserName(user.getUserName());
        AssertUtil.isTrue(temp2!=null && !(temp2.getId().equals(user.getId())),"用户名称已经存在");

        //验证参数
        checkUser(user.getUserName(),user.getEmail(),user.getPhone());
        //设定默认值
        user.setUpdateDate(new Date());
        //判断修改是否成功
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"修改信息失败了！");

        //关联中间表
        relationUserRole(user.getId(),user.getRoleIds());
    }

    /**
     *  批量删除
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeUserIds(Integer[] ids){
        //判断是否选择了删除的数据
        AssertUtil.isTrue(ids==null||ids.length==0,"请选择要删除的数据");
        //遍历对象
        for (Integer userId :ids) {
            //统计当前用户有多少个角色
            int count = userRoleMapper.countUserRoleNum(userId);
            if(count>0){
                AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId)!=count,"用户角色删除失败！");
            }
        }

        //判断是否删除成功
        AssertUtil.isTrue(userMapper.deleteBatch(ids)<1,"删除失败了！");
    }
}
