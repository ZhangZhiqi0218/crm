package com.yjxxt.crm.controller;

import com.yjxxt.crm.annotation.RequiredPermission;
import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.Role;
import com.yjxxt.crm.query.RoleQuery;
import com.yjxxt.crm.service.RoleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("role")
public class RoleController extends BaseController {

    @Resource
    private RoleService roleService;

    /**
     *  用户管理信息查询
     * @return
     */
    @RequestMapping("findRoles")
    @ResponseBody
    public List<Map<String,Object>> findAllRoles(Integer userId){
        return roleService.findAllRoles(userId);
    }

    /**
     *  查询角色信息
     * @param roleQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    @RequiredPermission(code="60")
    public Map<String,Object> list(RoleQuery roleQuery){
        return roleService.findRoleByParam(roleQuery);
    }

    /**
     *  跳转到角色管理页面
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "role/role";
    }

    /**
     * 跳转到授权模块页面
     * @return
     */
    @RequestMapping("toRoleGrantPage")
    public String toRoleGrantPage(Integer roleId,Model model){
        model.addAttribute("roleId",roleId);
        return "role/grant";
    }

    /**
     *  跳转到添加角色信息页面
     * @param roleId
     * @param model
     * @return
     */
    @RequestMapping("addOrUpdateRolePage")
    public String addOrUpdateRolePage(Integer roleId, Model model){
        if(roleId!=null){
            Role role = roleService.selectByPrimaryKey(roleId);
            model.addAttribute("role",role);
        }
        return "role/add_update";
    }

    /**
     *  添加角色信息
     * @param role
     * @return
     */
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo sava(Role role){
        roleService.savaRole(role);
        return success("添加角色成功了");
    }

    /**
     *  修改角色信息
     * @param role
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo update(Role role){
        roleService.updateRole(role);
        return success("修改角色成功了");
    }

    /**
     * 删除角色信息
     * @param role
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo delete(Role role){
        roleService.removeRoleId(role);
        return success("删除成功了");
    }

    /**
     * 添加角色权限
     * @param roleId
     * @param mids
     * @return
     */
    @RequestMapping("addGrant")
    @ResponseBody
    public ResultInfo addGrant(Integer roleId,Integer[] mids){
        roleService.addGrant(roleId,mids);
        return success("权限添加成功了！");
    }

}
